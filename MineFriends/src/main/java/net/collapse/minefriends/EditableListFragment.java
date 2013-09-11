package net.collapse.minefriends;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class EditableListFragment extends ListFragment
{
	protected View rootView;
	protected int rootViewId;

	public EditableListFragment(int rootViewId)
	{
		this.rootViewId = rootViewId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(this.rootViewId, null);
		return view;
	}

	protected void addClickListener(View parent, int id, View.OnClickListener listener)
	{
		Button addButton = (Button) parent.findViewById(id);
		addButton.setOnClickListener(listener);
	}
}
