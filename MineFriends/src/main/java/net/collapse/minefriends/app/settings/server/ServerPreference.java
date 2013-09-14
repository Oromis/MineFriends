package net.collapse.minefriends.app.settings.server;

import android.content.Context;
import android.util.AttributeSet;
import net.collapse.minefriends.app.EditableListPreference;
import net.collapse.minefriends.model.Server;
import net.collapse.minefriends.model.ServerSerializer;

/**
 * @author David
 */
public class ServerPreference extends EditableListPreference<Server>
{
	public ServerPreference(Context context)
	{
		super(context);
	}

	public ServerPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ServerPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	{
		this.serializer = new ServerSerializer();
	}
}
