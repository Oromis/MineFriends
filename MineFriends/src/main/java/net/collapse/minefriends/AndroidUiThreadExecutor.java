package net.collapse.minefriends;

import android.app.Activity;

import java.util.concurrent.Executor;

/**
 * Executes it's Runnables on the Android UI Thread
 * @author David
 */
public class AndroidUiThreadExecutor implements Executor
{
	private final Activity activity;

	public AndroidUiThreadExecutor(Activity activity)
	{
		this.activity = activity;
	}

	@Override
	public void execute(Runnable command)
	{
		this.activity.runOnUiThread(command);
	}
}
