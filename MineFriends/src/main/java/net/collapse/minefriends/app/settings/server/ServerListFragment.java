package net.collapse.minefriends.app.settings.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import net.collapse.minefriends.app.Constants;
import net.collapse.minefriends.app.EditableListFragment;
import net.collapse.minefriends.R;
import net.collapse.minefriends.model.Server;
import net.collapse.minefriends.model.ServerState;
import net.collapse.minefriends.network.Queries;
import net.collapse.minefriends.network.QueryResultListener;

import java.util.List;

/**
 * An android Fragment used to display an editable List of Servers
 * @author David
 */
public class ServerListFragment extends EditableListFragment<Server> implements ServerDialogCompletionListener,
		ServerDialogSpawner
{
	private ServerAdapter serverAdapter;

	public ServerListFragment()
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

		this.serverAdapter = new ServerAdapter(getActivity(), R.layout.server_item, this.data);
		this.serverAdapter.setServerDialogSpawner(this);
		this.setListAdapter(this.serverAdapter);

		return view;
	}

	public void onAddClicked(Button button)
	{
		Log.i(Constants.TAG, "add clicked");
		showServerDialog();
	}

	public void onClearClicked(Button button)
	{
		this.data.clear();
		if(serverAdapter != null)
		{
			serverAdapter.notifyDataSetChanged();
		}
		notifyDataInvalidation();
	}

	private void showServerDialog(Server server)
	{
		new ServerDialogFragment()
				.setDialogCompletionListener(this)
				.setServer(server)
				.show(getFragmentManager(), null);
	}

	private void showServerDialog()
	{
		this.showServerDialog(null);
	}

	private void showServerDialog(String name, String url, String port)
	{
		this.showServerDialog(new Server(
				name, url, Integer.parseInt(port)));
	}

	private void dialogFailed(int messageId, Server server)
	{
		new ErrorDialog(messageId, server, this)
				.show(getFragmentManager(), null);
	}

	@Override
	public void onSuccess(final Server server)
	{
		// User entered a Server, now check whether it's available.
		Log.i(Constants.TAG, "New Server: " + server);

		// Create a spinner to entertain the user
		final DialogFragment dialogFragment = new DialogFragment()
		{
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				ProgressDialog dialog = new ProgressDialog(getActivity());
				dialog.setIndeterminate(true);
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setTitle(R.string.server_dialog_busy);
				dialog.setMessage(getResources().getString(R.string.server_dialog_busy_message));
				return dialog;
			}
		};
		dialogFragment.setCancelable(false);
		dialogFragment.show(getFragmentManager(), null);

		Queries.INSTANCE.startQuery(server, new QueryResultListener()
		{
			@Override
			public void onCompletion(ServerState result, Throwable error)
			{
				Log.i(Constants.TAG, "Completed Query: " + result, error);
				dialogFragment.dismiss();

				if(error != null)
				{
					// Query failed.
					dialogFailed(R.string.server_dialog_error_connect_message, server);
				}
				else
				{
					// Ok, create a list entry if necessary
					if(!data.contains(server))
					{
						data.add(server);
					}
					serverAdapter.notifyDataSetChanged();
					notifyDataInvalidation();
				}
			}
		});
	}

	@Override
	public void onDelete(Server server)
	{
		this.data.remove(server);
		serverAdapter.notifyDataSetChanged();
		notifyDataInvalidation();
	}

	@Override
	public void onFail(Server server, Throwable exception, int errorId)
	{
		dialogFailed(errorId, server);
	}

	@Override
	public void onDataInvalidation()
	{
		serverAdapter.notifyDataSetChanged();
	}

	@Override
	public void spawn(Server server)
	{
		this.showServerDialog(server);
	}

	private static final class ServerAdapter extends ArrayAdapter<Server>
	{
		private ServerDialogSpawner serverDialogSpawner;

		public ServerAdapter(Context context, int resource)
		{
			super(context, resource);
		}

		public ServerAdapter(Context context, int resource, List<Server> objects)
		{
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final Server server = this.getItem(position);
			View view = LayoutInflater.from(getContext()).inflate(R.layout.server_item, null);
			TextView nameView = (TextView) view.findViewById(R.id.server_item_name);
			nameView.setText(server.getName());

			TextView urlView = (TextView) view.findViewById(R.id.server_item_url);
			urlView.setText(server.getHost() + ":" + server.getPort());

			view.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					serverDialogSpawner.spawn(server);
				}
			});

			return view;
		}

		public void setServerDialogSpawner(ServerDialogSpawner serverDialogSpawner)
		{
			this.serverDialogSpawner = serverDialogSpawner;
		}
	}

	private final static class ErrorDialog extends DialogFragment
	{
		private final int    textId;
		private final Server server;
		private final ServerDialogSpawner serverDialogSpawner;

		private ErrorDialog(int textId, Server server, ServerDialogSpawner serverDialogSpawner)
		{
			this.textId = textId;
			this.server = server;
			this.serverDialogSpawner = serverDialogSpawner;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			return new AlertDialog.Builder(getActivity())
					.setTitle(android.R.string.dialog_alert_title)
					.setMessage(textId)
					.setPositiveButton(R.string.server_dialog_edit, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							serverDialogSpawner.spawn(server);
						}
					})
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
						}
					})
					.create();
		}
	}
}
