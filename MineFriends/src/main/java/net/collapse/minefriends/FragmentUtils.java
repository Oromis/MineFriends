package net.collapse.minefriends;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * @author David
 */
public class FragmentUtils
{
	private FragmentUtils() { }

	public static void changeContentTo(Fragment fragment, FragmentManager fragmentManager)
	{
		fragmentManager.beginTransaction()
				.setTransition(fragment instanceof SettingsActivity.SettingsFragment ?
						               FragmentTransaction.TRANSIT_FRAGMENT_CLOSE :
						               FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.replace(android.R.id.content, fragment)
				.commit();
	}
}
