package com.placeme.ui;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;

import com.placeme.R;
import com.placeme.services.LocationService;
import com.placeme.ui.MenuFragment.MenuListener;
import com.placeme.views.DrawerView;

public class MainActivity extends FragmentActivity implements MenuListener
{
	public static final String	TAG				= MainActivity.class.getSimpleName();

	private static final String	FRAGMENT_MENU	= "FRAGMENT_MENU";

	private MenuFragment		menu_F;

	private DrawerView			drawer_V;

	private IntentFilter		mFilter;
	private LocationReceiver	mReceiver;
	private SharedPreferences	mSharedPrefs;
	private int					mLat, mLon;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		// Get views
		drawer_V = (DrawerView) findViewById(R.id.drawer_V);

		mReceiver = new LocationReceiver();
		mFilter = new IntentFilter(LocationService.ACTION_LOCATE_ME);

		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mLat = mSharedPrefs.getInt(LocationService.LAT, -1);
		mLon = mSharedPrefs.getInt(LocationService.LON, -1);
		Log.v(TAG, String.format("Read location %d, %d from SharedPrefs.", mLat, mLon));

		// Add fragment
		menu_F = (MenuFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_MENU);
		if (menu_F == null)
		{
			menu_F = MenuFragment.getInstance(0);
			getSupportFragmentManager().beginTransaction().add(R.id.containerDrawer_V, menu_F).add(R.id.container_V, new CardsFragment()).commit();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, mFilter);
		if ((-1 == mLat || -1 == mLon) && !isServiceRunning(LocationService.class))
		{
			startService(new Intent(getApplicationContext(), LocationService.class).setAction(LocationService.ACTION_LOCATE_ME));
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
			{
				drawer_V.toggle(true);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private class LocationReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (LocationService.ACTION_LOCATE_ME.equals(action))
			{
				Log.d(TAG, String.format("Got location %d, %d", intent.getIntExtra(LocationService.LAT, -1), intent.getIntExtra(LocationService.LON, -1)));
			}
		}
	}

	private boolean isServiceRunning(Class<?> serviceClass)
	{
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if (serviceClass.getName().equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void onPositionChange(int position, String title)
	{
		getActionBar().setTitle(title);
		drawer_V.closeDrawer(true);
	}
}
