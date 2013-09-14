package net.collapse.minefriends.app.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import net.collapse.minefriends.R;
import net.collapse.minefriends.app.settings.SettingsActivity;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	public void onSettingsClicked(MenuItem item)
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		this.startActivity(intent);
	}
}
