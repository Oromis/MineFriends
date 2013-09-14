package net.collapse.minefriends.app.settings.friends;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import net.collapse.minefriends.R;

/**
 * Shows the dialog used to create and edit friends
 */
public final class FriendsDialogFragment extends DialogFragment
{
	private CompletionListener completionListener;

	private String initialName;

	public FriendsDialogFragment setCompletionListener(CompletionListener completionListener)
	{
		this.completionListener = completionListener;
		return this;
	}

	public FriendsDialogFragment setInitialName(String initialName)
	{
		this.initialName = initialName;
		return this;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		final View view = getActivity().getLayoutInflater().inflate(R.layout.friends_dialog, null);
		if(this.initialName != null)
		{
			((EditText) view.findViewById(R.id.friends_dialog_name)).setText(this.initialName);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(this.initialName == null ?
						          R.string.friends_dialog_title_add : R.string.friends_dialog_title_edit)
				.setView(view)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						EditText nameField = (EditText) view.findViewById(R.id.friends_dialog_name);
						String newName = nameField.getText().toString();
						newName = newName.replaceAll("\\|", "_");    // Pipe is used as separator

						if(completionListener != null)
						{
							completionListener.onSuccess(initialName, newName);
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
		if(this.initialName != null)
		{
			builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if(completionListener != null)
					{
						completionListener.onDelete(initialName);
					}
				}
			});
		}
		return builder.create();
	}

	// -----------------------------------------------------------------------------------------------
	// Inner types
	// -----------------------------------------------------------------------------------------------

	public interface CompletionListener
	{
		public void onSuccess(String oldName, String newName);
		public void onDelete(String oldName);
	}
}