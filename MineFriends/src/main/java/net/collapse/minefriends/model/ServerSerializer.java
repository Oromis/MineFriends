package net.collapse.minefriends.model;

/**
 * @author David
 */
public class ServerSerializer implements StringSerializer<Server>
{
	@Override
	public String asString(Server object)
	{
		return new StringBuilder()
				.append(object.getName())
				.append("|")
				.append(object.getHost())
				.append("|")
				.append(object.getPort())
				.toString();
	}

	@Override
	public Server fromString(String input)
	{
		String[] parts = input.split("\\|");
		Server result = null;
		if(parts.length == 3)
		{
			result = new Server(parts[0], parts[1], Integer.parseInt(parts[2]));
		}
		return result;
	}
}
