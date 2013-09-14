package net.collapse.minefriends.app.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import net.collapse.minefriends.R;
import net.collapse.minefriends.app.AndroidUiThreadExecutor;
import net.collapse.minefriends.app.Constants;
import net.collapse.minefriends.app.settings.friends.FriendsListFragment;
import net.collapse.minefriends.app.settings.server.ServerListFragment;
import net.collapse.minefriends.network.Queries;

public class SettingsActivity extends Activity
{
	private ViewPager pager;

	private Fragment[] fragments = new Fragment[Constants.PAGE_COUNT];

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		Queries.INSTANCE.setCallbackExecutor(new AndroidUiThreadExecutor(this));

		if(savedInstanceState != null)
		{
			// Load existing Fragments
			FragmentManager fragmentManager = this.getFragmentManager();
			fragments[Constants.MAIN] = fragmentManager.getFragment(
					savedInstanceState, SettingsFragment.class.getName());
			fragments[Constants.SERVERS] = fragmentManager.getFragment(
					savedInstanceState, ServerListFragment.class.getName());
			fragments[Constants.FRIENDS] = fragmentManager.getFragment(
					savedInstanceState, FriendsListFragment.class.getName());
		}
		else
		{
			fragments[Constants.MAIN] = new SettingsFragment();
			fragments[Constants.SERVERS] = new ServerListFragment();
			fragments[Constants.FRIENDS] = new FriendsListFragment();
		}

		this.pager = (ViewPager) this.findViewById(R.id.pager);
		SettingsFragment settingsFragment = this.getSettingsFragment();
		settingsFragment.setViewPager(this.pager);
		settingsFragment.setServerFragment(this.getServerFragment());
		settingsFragment.setFriendsFragment(this.getFriendsFragment());

		PagerAdapter pagerAdapter = new PagerAdapter(getFragmentManager());
		pagerAdapter.setResources(getResources());
		pagerAdapter.setFragments(this.fragments);
		this.pager.setAdapter(pagerAdapter);
		this.pager.setOffscreenPageLimit(Constants.PAGE_COUNT); // Keep all pages loaded

		// This styling is unfortunately impossible in XML
		PagerTabStrip pagerTabStrip = (PagerTabStrip) this.findViewById(R.id.pager_title_strip);
		pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.divider));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
					// This activity is NOT part of this app's task, so create a new task
					// when navigating up, with a synthesized back stack.
					TaskStackBuilder.create(this)
							.addNextIntentWithParentStack(upIntent)
							.startActivities();
				} else {
					// This activity is part of this app's task, so simply
					// navigate up to the logical parent activity.
					NavUtils.navigateUpTo(this, upIntent);
				}
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		FragmentManager fragmentManager = this.getFragmentManager();

		for(Fragment fragment : fragments)
		{
			fragmentManager.putFragment(outState, ((Object) fragment).getClass().getName(), fragment);
		}
	}

	@Override
	public void onBackPressed()
	{
		if(this.pager.getCurrentItem() == Constants.MAIN)
		{
			// Currently at default view -> end activity
			super.onBackPressed();
		}
		else
		{
			this.pager.setCurrentItem(Constants.MAIN, true);
		}
	}

	private SettingsFragment getSettingsFragment()
	{
		return (SettingsFragment) this.fragments[Constants.MAIN];
	}

	private ServerListFragment getServerFragment()
	{
		return (ServerListFragment) this.fragments[Constants.SERVERS];
	}

	private FriendsListFragment getFriendsFragment()
	{
		return (FriendsListFragment) this.fragments[Constants.FRIENDS];
	}

	// -----------------------------------------------------------------------------------------------
	// Inner types
	// -----------------------------------------------------------------------------------------------

	private static final class PagerAdapter extends FragmentPagerAdapter
	{
		private Fragment[] fragments;
		private Resources resources;

		public PagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		public void setFragments(Fragment[] fragments)
		{
			this.fragments = fragments;
		}

		public void setResources(Resources resources)
		{
			this.resources = resources;
		}

		@Override
		public Fragment getItem(int i)
		{
			return this.fragments[i];
		}

		@Override
		public int getCount()
		{
			return this.fragments.length;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			String result = null;
			switch(position)
			{
				case Constants.MAIN:
					result = this.resources.getString(R.string.settings_title_main);
					break;
				case Constants.SERVERS:
					result = this.resources.getString(R.string.settings_title_servers);
					break;
				case Constants.FRIENDS:
					result = this.resources.getString(R.string.settings_title_friends);
					break;
			}
			return result;
		}
	}
}
