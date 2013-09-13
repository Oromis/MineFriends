package net.collapse.minefriends.network;

/**
 * Minecraft doesn't look at the upper 4 bits of a byte. So this mess is required to generate unique IDs.
 */
public class SessionIdProvider
{
	private static short counter = 1;

	public static synchronized int getNextSessionId()
	{
		int result =
				((int) counter & 0x000F) +
				(((int) counter & 0x00F0) << 4) +
				(((int) counter & 0x0F00) << 8) +
				(((int) counter & 0xF000) << 12);
		++counter;
		return result;
	}
}
