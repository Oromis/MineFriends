package net.collapse.minefriends.app.settings.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import net.collapse.minefriends.R;
import net.collapse.minefriends.model.Server;

/**
 * Shows the dialog used to create and edit servers
 */
public final class ServerDialogFragment extends DialogFragment
{
	private ServerDialogCompletionListener dialogCompletionListener;

	private Server server;

	public ServerDialogFragment setDialogCompletionListener(ServerDialogCompletionListener dialogCompletionListener)
	{
		this.dialogCompletionListener = dialogCompletionListener;
		return this;
	}

	public ServerDialogFragment setServer(Server server)
	{
		this.server = server;
		return this;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		final View view = getActivity().getLayoutInflater().inflate(R.layout.server_dialog, null);
		// Set initial values
		{
			String name = "", host = "", port = String.valueOf(Server.DEFAULT_PORT);
			if(server != null)
			{
				name = server.getName();
				host = server.getHost();
				port = String.valueOf(server.getPort());
			}
			((EditText) view.findViewById(R.id.server_dialog_name)).setText(name);
			((EditText) view.findViewById(R.id.server_dialog_url)).setText(host);
			((EditText) view.findViewById(R.id.server_dialog_port)).setText(port);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(server != null ?
						          R.string.server_dialog_title_edit : R.string.server_dialog_title_add)
				.setView(view)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						EditText nameField = (EditText) view.findViewById(R.id.server_dialog_name);
						EditText urlField = (EditText) view.findViewById(R.id.server_dialog_url);
						EditText portField = (EditText) view.findViewById(R.id.server_dialog_port);

						String portString = portField.getText().toString();
						if(portString.length() == 0)
						{
							portString = portField.getHint().toString();
						}

						String urlString = urlField.getText().toString();
						String name = nameField.getText().toString();

						int port;
						try
						{
							port = Integer.parseInt(portString);
							if(port > 2 << 16 - 1)  // 2 ^ 16 - 1
							{
								// Invalid Port number
								throw new NumberFormatException();
							}
						}
						catch(NumberFormatException e)
						{
							if(dialogCompletionListener != null)
							{
								dialogCompletionListener.onFail(server, e, R.string.server_dialog_error_port);
							}
							return;
						}

						name = name.replaceAll("\\|", "_");    // Pipe is used as separator

						if(server == null)
						{
							server = new Server(name, urlString, port);
						}
						else
						{
							server.setName(name);
							server.setHost(urlString);
							server.setPort(port);
						}

						if(dialogCompletionListener != null)
						{
							dialogCompletionListener.onSuccess(server);
						}
					}
				})
				.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});
		if(this.server != null)
		{
			builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if(dialogCompletionListener != null)
					{
						dialogCompletionListener.onDelete(server);
					}
				}
			});
		}
		return builder.create();
	}

}