package net.collapse.minefriends.network;

import net.collapse.minefriends.model.ServerState;

public interface QueryResultListener
{
	/**
	 * Called when a query completes.
	 * @param result The result of the query. Is null if the query failed.
	 * @param error Null if the query was successful or the error object in case of failure.
	 */
	public void onCompletion(ServerState result, Throwable error);
}