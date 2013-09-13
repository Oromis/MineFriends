package net.collapse.minefriends.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Saves the state (MotD, number and names of players, ...) for a server
 * @author David
 */
public class ServerState
{
	private final Server server;
	private       String motd;
	private Set<String> players = new HashSet<String>();
	private int    maxPlayers;
	private int    curPlayers;
	private String map;
	private String version;

	public ServerState(Server server)
	{
		this.server = server;
	}

	public Server getServer()
	{
		return server;
	}

	public String getMotd()
	{
		return motd;
	}

	public Set<String> getPlayers()
	{
		return players;
	}

	public void setMotd(String motd)
	{
		this.motd = motd;
	}

	public void setPlayers(Set<String> players)
	{
		this.players = players;
	}

	public void addPLayer(String player)
	{
		this.players.add(player);
	}

	public int getMaxPlayers()
	{
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	public int getCurPlayers()
	{
		return curPlayers;
	}

	public void setCurPlayers(int curPlayers)
	{
		this.curPlayers = curPlayers;
	}

	public String getMap()
	{
		return map;
	}

	public void setMap(String map)
	{
		this.map = map;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	@Override
	public String toString()
	{
		return "ServerState{" +
				"server=" + server +
				", motd='" + motd + '\'' +
				", players=" + players +
				", maxPlayers=" + maxPlayers +
				", curPlayers=" + curPlayers +
				", map='" + map + '\'' +
				", version='" + version + '\'' +
				'}';
	}
}
