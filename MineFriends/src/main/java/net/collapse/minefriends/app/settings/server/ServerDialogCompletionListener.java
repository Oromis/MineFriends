package net.collapse.minefriends.app.settings.server;

import net.collapse.minefriends.model.Server;

public interface ServerDialogCompletionListener
{
	public void onSuccess(Server server);
	public void onDelete(Server server);
	public void onFail(Server server, Throwable exception, int errorId);
}