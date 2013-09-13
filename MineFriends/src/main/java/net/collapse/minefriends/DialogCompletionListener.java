package net.collapse.minefriends;

import net.collapse.minefriends.model.Server;

interface DialogCompletionListener
{
	public void onSuccess(Server server);
	public void onFail(Server server, Throwable exception, int errorId);
}