package net.collapse.minefriends.app.settings.friends;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import net.collapse.minefriends.R;
import net.collapse.minefriends.app.Constants;
import net.collapse.minefriends.app.EditableListFragment;

import java.util.List;

/**
 * An android Fragment used to display an editable List of Friends
 * @author David
 */
public class FriendsListFragment extends EditableListFragment<String> implements FriendsDialogFragment.CompletionListener,
		FriendsDialogSpawner
{
	private Adapter listAdapter;

	public FriendsListFragment()
	{
		super(R.layout.server_list);
		Log.i(Constants.TAG, "ServerListFragment is created");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		addClickListener(view, R.id.list_add, new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onAddClicked((Button) v);
			}
		});
		addClickListener(view, R.id.list_clear, new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onClearClicked((Button) v);
			}
		});

		this.listAdapter = new Adapter(getActivity(), android.R.layout.simple_list_item_1, this.data);
		this.listAdapter.setSpawner(this);
		this.setListAdapter(this.listAdapter);

		return view;
	}

	public void onAddClicked(Button button)
	{
		Log.i(Constants.TAG, "add player clicked");
		this.spawn(null);
	}

	public void onClearClicked(Button button)
	{
		this.data.clear();
		if(listAdapter != null)
		{
			listAdapter.notifyDataSetChanged();
		}
		notifyDataInvalidation();
	}

	@Override
	public void onDataInvalidation()
	{
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSuccess(String oldName, String newName)
	{
		Log.i(Constants.TAG, "New Player: " + newName);
		if(oldName != null && this.data.contains(oldName))
		{
			this.data.remove(oldName);
		}
		this.data.add(newName);
		listAdapter.notifyDataSetChanged();
		notifyDataInvalidation();
	}

	@Override
	public void onDelete(String oldName)
	{
		this.data.remove(oldName);
		listAdapter.notifyDataSetChanged();
		notifyDataInvalidation();
	}

	@Override
	public void spawn(String initialName)
	{
		new FriendsDialogFragment()
				.setCompletionListener(this)
				.setInitialName(initialName)
				.show(getFragmentManager(), null);
	}

	private static final class Adapter extends ArrayAdapter<String>
	{
		private FriendsDialogSpawner spawner;

		public Adapter(Context context, int resource)
		{
			super(context, resource);
		}
		public Adapter(Context context, int resource, int textViewResourceId)
		{
			super(context, resource, textViewResourceId);
		}
		public Adapter(Context context, int resource, String[] objects)
		{
			super(context, resource, objects);
		}
		public Adapter(Context context, int resource, int textViewResourceId, String[] objects)
		{
			super(context, resource, textViewResourceId, objects);
		}
		public Adapter(Context context, int resource, List<String> objects)
		{
			super(context, resource, objects);
		}
		public Adapter(Context context, int resource, int textViewResourceId,
		               List<String> objects)
		{
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final String name = this.getItem(position);
			View result = super.getView(position, convertView, parent);
			result.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					spawner.spawn(name);
				}
			});
			return result;
		}

		public void setSpawner(FriendsDialogSpawner spawner)
		{
			this.spawner = spawner;
		}
	}
}
