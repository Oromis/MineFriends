package net.collapse.minefriends;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import net.collapse.minefriends.model.Server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An android Fragment used to display an editable List of Servers
 * @author David
 */
public class ServerListFragment extends EditableListFragment
{
	private List<Server> servers = new ArrayList<Server>();
	private ServerAdapter serverAdapter;

	public ServerListFragment()
	{
		super(R.layout.server_list);
		Log.i(Constants.TAG, "ServerListFragment is created");

		try
		{
			servers.add(new Server("Default Server", new URL("http://amazon.de")));
			servers.add(new Server("Default Server 2", new URL("http://facebook.com")));
		}
		catch(MalformedURLException e)
		{
			Log.e(Constants.TAG, "", e);
		}
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

		this.setListAdapter(this.serverAdapter = new ServerAdapter(getActivity(), R.layout.server_item, servers));

		return view;
	}

	public void onAddClicked(Button button)
	{
		Log.i(Constants.TAG, "add clicked");
		try
		{
			this.servers.add(new Server("Test Server", new URL("http://www.google.de")));
			this.serverAdapter.notifyDataSetChanged();
		}
		catch(MalformedURLException e)
		{
			Log.e(Constants.TAG, "Google doesn't exist anymore o.O", e);
		}
	}

	public void onClearClicked(Button button)
	{
		this.servers.clear();
		if(serverAdapter != null)
		{
			serverAdapter.notifyDataSetChanged();
		}
	}

	private static final class ServerAdapter extends ArrayAdapter<Server>
	{
		private List<Server> urls;

		public ServerAdapter(Context context, int resource)
		{
			super(context, resource);
		}

		public ServerAdapter(Context context, int resource, List<Server> objects)
		{
			super(context, resource, objects);
			this.urls = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = LayoutInflater.from(getContext()).inflate(R.layout.server_item, null);
			TextView nameView = (TextView) view.findViewById(R.id.server_item_name);
			nameView.setText(urls.get(position).getName());

			TextView urlView = (TextView) view.findViewById(R.id.server_item_url);
			urlView.setText(urls.get(position).getUrl().toExternalForm());

			return view;
		}
	}
}
