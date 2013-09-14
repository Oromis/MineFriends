package net.collapse.minefriends.app.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.view.ViewPager;
import net.collapse.minefriends.app.ChangeListenerManager;
import net.collapse.minefriends.app.Constants;
import net.collapse.minefriends.app.EditableListFragment;
import net.collapse.minefriends.app.EditableListPreference;
import net.collapse.minefriends.R;
import net.collapse.minefriends.model.Server;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment
{
	private ViewPager viewPager;

	private EditableListFragment<Server> serverFragment;
	private EditableListFragment<String> friendsFragment;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_settings);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences
		// to their values. When their values change, their summaries are
		// updated to reflect the new value, per the Android Design
		// guidelines.
		ChangeListenerManager.bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		ChangeListenerManager.bindPreferenceSummaryToValue(findPreference("poll_frequency"));

		this.updatePreference("pref_key_servers", Constants.SERVERS, serverFragment);
		this.updatePreference("pref_key_friends", Constants.FRIENDS, friendsFragment);
	}

	public void setServerFragment(EditableListFragment<Server> serverFragment)
	{
		this.serverFragment = serverFragment;
		this.updatePreference("pref_key_servers", Constants.SERVERS, serverFragment);
	}

	public void setFriendsFragment(EditableListFragment<String> friendsFragment)
	{
		this.friendsFragment = friendsFragment;
		this.updatePreference("pref_key_friends", Constants.FRIENDS, friendsFragment);
	}

	public void setViewPager(ViewPager viewPager)
	{
		this.viewPager = viewPager;
	}

	private <D> void updatePreference(String key, int pageIndex, EditableListFragment<D> fragment)
	{
		@SuppressWarnings("unchecked")
		EditableListPreference<D> serverPreference =
				(EditableListPreference<D>) findPreference(key);
		if(serverPreference != null && fragment != null)
		{
			serverPreference.setPager(viewPager);
			serverPreference.setTargetPage(pageIndex);
			serverPreference.setFragment(fragment);
		}
	}
}