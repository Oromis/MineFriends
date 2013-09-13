package net.collapse.minefriends.network;

import android.util.Log;
import net.collapse.minefriends.Constants;
import net.collapse.minefriends.model.Server;
import net.collapse.minefriends.model.ServerState;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

/**
 * Implements this Protocol: <a href="http://wiki.vg/Query">Query</a>
 * @author David
 */
public enum Queries implements Runnable
{
	INSTANCE;

	private static final int BUFFER_SIZE = 2048;

	private static final byte TYPE_HANDSHAKE = 0x09;
	private static final byte TYPE_STATS = 0x00;

	private static final long PACKET_TIMEOUT = 2000;

	private final Thread worker;

	private DatagramChannel channel;

	private Map<Integer, Request> requests     = new ConcurrentHashMap<Integer, Request>();
	private Queue<Request>        requestQueue = new ConcurrentLinkedQueue<Request>();

	private Executor callbackExecutor;

	private Queries()
	{
		worker = new Thread(this);
		worker.setDaemon(true);
		worker.start();
	}

	@Override
	public void run()
	{
		try
		{
			DatagramSocket socket = new DatagramSocket();
			socket.setSoTimeout(100);

			while(true)
			{
				try
				{
					// Send pending packets
					Request req;
					while((req = requestQueue.poll()) != null)
					{
						// Send a packet for this request
						requests.put(req.getSessionId(), req);
						req.setStartTime(System.currentTimeMillis());
						if(req.getTriesLeft() <= 0)
						{
							// Could not get connection: Too many packets lost. Kill request and notify caller
							requests.remove(req.getSessionId());
							final QueryResultListener listener = req.getListener();
							if(listener != null)
							{
								callbackExecutor.execute(new Runnable()
								{
									@Override
									public void run()
									{
										listener.onCompletion(null, new TimeoutException("Could not connect to server"));
									}
								});
							}
						}
						else
						{
							// Enough tries left -> Send packet
							req.decrementTries();
							DatagramPacket nextPacket = this.createNextPacket(req);
							if(nextPacket != null)
							{
								socket.send(nextPacket);
							}
						}
					}

					// Check all pending requests for timeout
					for(Request request : requests.values())
					{
						if(System.currentTimeMillis() - request.getStartTime() > PACKET_TIMEOUT)
						{
							// Timeout. Re-send request with new SessionID.
							requests.remove(request.getSessionId());
							request.setSessionId(SessionIdProvider.getNextSessionId());
							requestQueue.add(request);
						}
					}

					// Try to receive something
					byte[] buffer = new byte[BUFFER_SIZE];
					DatagramPacket recPacket = new DatagramPacket(buffer, BUFFER_SIZE);
					socket.receive(recPacket);
					analyzePacket(recPacket);
				}
				catch(SocketTimeoutException e)
				{
					// Nothing special, just go on
				}
				catch(Exception e)
				{
					Log.e(Constants.TAG, "Send / Receive - Cycle failed", e);
					// Just continue. The show must go on!
				}
			}
		}
		catch(Exception e)
		{
			Log.e(Constants.TAG, "Worker thread died due to IO Exception", e);
		}
	}

	public void startQuery(Server target, QueryResultListener listener)
	{
		Request request = new Request(target);
		request.setListener(listener);
		requestQueue.add(request);
	}

	public void setCallbackExecutor(Executor callbackExecutor)
	{
		this.callbackExecutor = callbackExecutor;
	}

	private void analyzePacket(DatagramPacket packet)
	{
		ByteBuffer recBuffer = ByteBuffer.wrap(packet.getData());
		byte type = recBuffer.get();
		if(!(type == TYPE_HANDSHAKE || type == TYPE_STATS))
		{
			// Unknown Identifier -> skip packet
			return;
		}

		int sessionId = recBuffer.getInt();
		Request req = this.requests.get(sessionId);
		if(req == null)
		{
			// No such request -> skip packet.
			return;
		}

		switch(type)
		{
			case 0:     // Stats
				analyzeStats(recBuffer, req);
				break;

			case 9:     // Handshake
				byte[] challengeToken = readChallengeToken(recBuffer);
				req.setChallengeToken(challengeToken);
				requestQueue.add(req);  // Send the Stats Request packet
				break;
		}
	}

	private void analyzeStats(ByteBuffer buffer, Request req)
	{
		try
		{
			final ServerState result = new ServerState(req.getServer());

			// First 11 bytes are unused
			for(int i = 0; i < 11; i++)
			{
				buffer.get();
			}

			// Key - Value - Section
			String key, value = null;
			while((key = readString(buffer)).length() > 0)
			{
				value = readString(buffer);
				this.handleKeyValuePair(key, value, result);
			}

			// 10 unused bytes
			for(int i = 0; i < 10; i++)
			{
				buffer.get();
			}

			// Player section
			String player;
			while((player = readString(buffer)).length() > 0)
			{
				result.addPLayer(player);
			}

			// Successful read -> Remove request
			this.requests.remove(req.getSessionId());

			// Notify caller
			final QueryResultListener listener = req.getListener();
			if(listener != null)
			{
				this.callbackExecutor.execute(new Runnable()
				{
					@Override
					public void run()
					{
						listener.onCompletion(result, null);
					}
				});
			}
		}
		catch(BufferUnderflowException e)
		{
			// Corrupt or incomplete package -> Resend the request
			requestQueue.add(req);
			Log.e(Constants.TAG, "Could not read Stats package", e);
		}
	}

	/// Reads a \0-terminated String
	private static String readString(ByteBuffer buffer)
	{
		StringBuffer sb = new StringBuffer();
		byte cur;
		while((cur = buffer.get()) != 0)
		{
			sb.append((char) cur);
		}
		return sb.toString();
	}

	private static byte[] readChallengeToken(ByteBuffer rec)
	{
		String tokenString = readString(rec);
		int token = Integer.parseInt(tokenString.toString());
		return ByteBuffer.allocate(4)
		                 .order(ByteOrder.BIG_ENDIAN)
		                 .putInt(token)
		                 .array();
	}

	private DatagramPacket createNextPacket(Request request) throws SocketException
	{
		if(request.getChallengeToken() == null)
		{
			return this.createHandshakePacket(request);
		}
		else
		{
			return this.createStatsPacket(request);
		}
	}

	private DatagramPacket createHandshakePacket(Request request) throws SocketException
	{
		byte[] data = ByteBuffer.allocate(7)
		          .put((byte) 0xFE).put((byte) 0xFD)    // Magic code
				  .put(TYPE_HANDSHAKE)                  // Handshake requested
				  .putInt(request.getSessionId())       // SessionID
				  .array();

		Server server = request.getServer();
		return new DatagramPacket(data, data.length, new InetSocketAddress(server.getHost(), server.getPort()));
	}

	private DatagramPacket createStatsPacket(Request request) throws SocketException
	{
		byte[] data = ByteBuffer.allocate(15)
				.put((byte) 0xFE).put((byte) 0xFD)      // Magic code
				.put(TYPE_STATS)                        // Stats requested
				.putInt(request.getSessionId())         // Session ID
				.put(request.getChallengeToken())       // Challenge Token
				.put((byte) 0).put((byte) 0)            // Padding (unused)
				.put((byte) 0).put((byte) 0)            // Padding (unused)
				.array();

		Server server = request.getServer();
		return new DatagramPacket(data, data.length, new InetSocketAddress(server.getHost(), server.getPort()));
	}

	private void handleKeyValuePair(String key, String value, ServerState result)
	{
		if("hostname".equals(key))
		{
			result.setMotd(value);
		}
		else if("version".equals(key))
		{
			result.setVersion(value);
		}
		else if("map".equals(key))
		{
			result.setMap(value);
		}
		else if("numplayers".equals(key))
		{
			result.setCurPlayers(Integer.parseInt(value));
		}
		else if("maxplayers".equals(key))
		{
			result.setMaxPlayers(Integer.parseInt(value));
		}
	}
}
