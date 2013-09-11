package net.collapse.minefriends;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a list of servers which the user can edit.
 * @author David
 */
public class EditableListPreference<D> extends Preference
{
	private FragmentManager fragmentManager;
	private EditableListFragment fragment;

	private List<D> data;

	public EditableListPreference(Context context)
	{
		super(context);
	}

	public EditableListPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public EditableListPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onClick()
	{
		super.onClick();

		FragmentUtils.changeContentTo(this.fragment, getFragmentManager());
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
	{
		if(restorePersistedValue)
		{

		}
		else
		{

		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return new ArrayList<D>();
	}

	public FragmentManager getFragmentManager()
	{
		return fragmentManager;
	}

	public void setFragmentManager(FragmentManager fragmentManager)
	{
		this.fragmentManager = fragmentManager;
	}

	public void setFragmentToOpen(EditableListFragment fragment)
	{
		this.fragment = fragment;
	}
}
