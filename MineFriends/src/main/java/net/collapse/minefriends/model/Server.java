package net.collapse.minefriends.model;

import java.net.URL;

/**
 * A server to check for online players
 */
public class Server
{
	private String name;
	private URL url;

	public Server(String name, URL url)
	{
		this.name = name;
		this.url = url;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}
}
