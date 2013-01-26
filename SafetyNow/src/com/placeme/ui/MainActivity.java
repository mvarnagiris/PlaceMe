package com.placeme.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.placeme.R;
import com.placeme.services.LocationService;
import com.placeme.views.DrawerView;

public class MainActivity extends Activity
{

	public static final String	TAG	= MainActivity.class.getSimpleName();

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

		// Get views
		drawer_V = (DrawerView) findViewById(R.id.drawer_V);
		drawer_V.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{

				drawer_V.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				// Opening this in a delayed runnable makes it look smoother.
				drawer_V.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						drawer_V.openDrawer(true);
					}
				}, 300);
			}
		});

		mReceiver = new LocationReceiver();
		mFilter = new IntentFilter(LocationService.ACTION_LOCATE_ME);

		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mLat = mSharedPrefs.getInt(LocationService.LAT, -1);
		mLon = mSharedPrefs.getInt(LocationService.LON, -1);
		Log.v(TAG, String.format("Read location %d, %d from SharedPrefs.", mLat, mLon));
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
}