package net.collapse.minefriends.model;

/**
 * @author David
 */
public interface StringSerializer<D>
{
	public String asString(D object);
	public D fromString(String input);
}
