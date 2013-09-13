package net.collapse.minefriends;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class EditableListFragment<D> extends ListFragment
{
	protected int rootViewId;

	protected List<D> data;

	protected InvalidationListener<D> listener;

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

	@Override
	public void onPause()
	{
		super.onPause();
		notifyDataInvalidation();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		notifyDataInvalidation();
	}

	public List<D> getData()
	{
		return data;
	}

	public void setData(List<D> data)
	{
		this.data = data;
	}

	public void setListener(InvalidationListener<D> listener)
	{
		this.listener = listener;
	}

	public void notifyDataInvalidation()
	{
		if(listener != null)
		{
			listener.onInvalidation(this.data);
		}
	}

	public void onDataInvalidation()
	{
	}

	public interface InvalidationListener<D>
	{
		public void onInvalidation(List<D> data);
	}
}
