package com.placeme.ui;

import java.util.ArrayList;

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

import com.placeme.Consts;
import com.placeme.R;
import com.placeme.model.CardInfo;
import com.placeme.model.Place;
import com.placeme.services.InfoService;
import com.placeme.services.LocationService;
import com.placeme.ui.MenuFragment.MenuListener;
import com.placeme.views.DrawerView;

public class MainActivity extends FragmentActivity implements MenuListener
{
	public static final String	TAG				= MainActivity.class.getSimpleName();

	private static final String	FRAGMENT_MENU	= "FRAGMENT_MENU";
	private static final String	FRAGMENT_CARDS	= "FRAGMENT_CARDS";

	private MenuFragment		menu_F;
	private CardsFragment		cards_F;

	private DrawerView			drawer_V;

	private IntentFilter		mFilter;
	private LocalReceiver		mReceiver;
	private SharedPreferences	mSharedPrefs;
	private int					mLat, mLon;

	private class LocalReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (Consts.ACTION_LOCATE_ME.equals(action))
			{
				mLat = intent.getIntExtra(Consts.LAT, -1);
				mLon = intent.getIntExtra(Consts.LON, -1);
				Log.d(TAG, String.format("Got location %d, %d", mLat, mLon));
				fetchDataForLocation();

			}
			else if (Consts.ACTION_GET_DATA.equals(action))
			{
				Place place = (Place) intent.getSerializableExtra(Consts.PLACE);
				@SuppressWarnings("unchecked")
				ArrayList<CardInfo> cards = (ArrayList<CardInfo>) intent.getSerializableExtra(Consts.CARDS);
				Log.d(TAG, String.format("Got place %s and data %s", place, cards.toString()));

				cards_F.bind(place, cards);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(R.string.menu_live_here);

		// Get views
		drawer_V = (DrawerView) findViewById(R.id.drawer_V);

		mReceiver = new LocalReceiver();
		mFilter = new IntentFilter();
		mFilter.addAction(Consts.ACTION_LOCATE_ME);
		mFilter.addAction(Consts.ACTION_GET_DATA);

		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mLat = mSharedPrefs.getInt(Consts.LAT, -1);
		mLon = mSharedPrefs.getInt(Consts.LON, -1);

		// Add fragment
		menu_F = (MenuFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_MENU);
		cards_F = (CardsFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_CARDS);
		if (menu_F == null)
		{
			menu_F = MenuFragment.getInstance(0);
			cards_F = new CardsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.containerDrawer_V, menu_F).add(R.id.container_V, cards_F).commit();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, mFilter);
		if ((-1 == mLat || -1 == mLon) && !isServiceRunning(LocationService.class))
		{
			startService(new Intent(getApplicationContext(), LocationService.class).setAction(Consts.ACTION_LOCATE_ME));
		}
		else
		{
			Log.d(TAG, String.format("Got location %d, %d", mLat, mLon));
			fetchDataForLocation();
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

	private void fetchDataForLocation()
	{
		startService(new Intent(getApplicationContext(), InfoService.class).putExtra(Consts.LAT, mLat).putExtra(Consts.LON, mLon));
	}

}
