package net.collapse.minefriends.app.settings.friends;

import android.content.Context;
import android.util.AttributeSet;
import net.collapse.minefriends.app.EditableListPreference;
import net.collapse.minefriends.model.StringSerializer;

/**
 * @author David
 */
public class FriendsPreference extends EditableListPreference<String>
{
	public FriendsPreference(Context context)
	{
		super(context);
	}

	public FriendsPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FriendsPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	{
		serializer = new StringSerializer<String>()
		{
			@Override
			public String asString(String object)
			{
				return object;
			}

			@Override
			public String fromString(String input)
			{
				return input;
			}
		};
	}
}
