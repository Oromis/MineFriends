package net.collapse.minefriends.network;

import net.collapse.minefriends.model.Server;

/**
 * @author David
 */
public class Request
{
	private final Server server;
	private int    sessionId      = SessionIdProvider.getNextSessionId();
	private byte[] challengeToken = null;
	private QueryResultListener listener;

	private long startTime;
	private int triesLeft = 4;

	public Request(Server server)
	{
		this.server = server;
	}

	public Server getServer()
	{
		return server;
	}

	public int getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(int sessionId)
	{
		this.sessionId = sessionId;
	}

	public byte[] getChallengeToken()
	{
		return challengeToken;
	}

	public void setChallengeToken(byte[] challengeToken)
	{
		this.challengeToken = challengeToken;
	}

	public QueryResultListener getListener()
	{
		return listener;
	}

	public void setListener(QueryResultListener listener)
	{
		this.listener = listener;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	public int getTriesLeft()
	{
		return triesLeft;
	}

	public void decrementTries()
	{
		--triesLeft;
	}
}
