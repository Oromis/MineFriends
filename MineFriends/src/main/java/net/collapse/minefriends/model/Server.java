package net.collapse.minefriends.model;

/**
 * A server to check for online players
 */
public class Server
{
	public static final int DEFAULT_PORT = 25565;

	private String name;
	private String host;
	private int port;

	public Server()
	{
	}

	public Server(String name, String host, int port)
	{
		this.name = name;
		this.host = host;
		this.port = port;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public String toString()
	{
		return "Server{" +
				"name='" + name + '\'' +
				", host='" + host + '\'' +
				", port=" + port +
				'}';
	}
}
