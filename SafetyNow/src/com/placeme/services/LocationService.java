package com.placeme.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.placeme.Consts;
import com.placeme.R;

public class LocationService extends Service implements LocationListener {

	public static final String	TAG					= LocationService.class.getSimpleName();

	private static final int	TWO_MINUTES			= 1000 * 60 * 2;
	private static final int	MIN_TIME_MS			= 0;
	private static final int	MIN_DIST_M			= 0;

	private SharedPreferences	mSharedPrefs;
	private LocationManager		mLocationManager;
	private Criteria			mCriteria;
	private String				mBestProvider;
	private Location			mLocation;
	private int					mLat;
	private int					mLon;

	@Override
	public void onCreate() {
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mCriteria = new Criteria();
		mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		mCriteria.setPowerRequirement(Criteria.POWER_LOW);
		mCriteria.setCostAllowed(true);
		mCriteria.setAltitudeRequired(false);
		mCriteria.setBearingRequired(false);
		mCriteria.setSpeedRequired(false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null != intent) {
			String action = intent.getAction();
			if (Consts.ACTION_LOCATE_ME.equals(action)) {
				updateBestProvider();
				if (!TextUtils.isEmpty(mBestProvider)) {
					Location location = mLocationManager.getLastKnownLocation(mBestProvider);
					if (isBetterLocation(location, mLocation)) {
						mLocation = location;
					}
					if (null != mLocation) {
						updateCurrentLocation();
					}
					else {
						mLocationManager.requestLocationUpdates(mBestProvider, MIN_TIME_MS, MIN_DIST_M, this);
					}
				}
			}
		}
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocationManager.removeUpdates(this);
		Toast.makeText(this, R.string.location_acquired, Toast.LENGTH_LONG).show();
		if (isBetterLocation(location, mLocation)) {
			mLocation = location;
		}
		if (null != mLocation) {
			updateCurrentLocation();
		}
	}

	@Override
	public void onDestroy() {
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		updateBestProvider();
	}

	@Override
	public void onProviderEnabled(String provider) {
		updateBestProvider();
	}

	@Override
	public void onProviderDisabled(String provider) {
		updateBestProvider();
	}

	private void updateBestProvider() {
		mBestProvider = mLocationManager.getBestProvider(mCriteria, true);
		Log.v(TAG, String.format("Best provider found %s", mBestProvider));
		if (null == mBestProvider) {
			Toast.makeText(this, R.string.enable_location_settings, Toast.LENGTH_LONG).show();
		}
	}

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		}
		else if (isSignificantlyOlder) { return false; }

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		}
		else if (isNewer && !isLessAccurate) {
			return true;
		}
		else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) { return true; }
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) { return provider2 == null; }
		return provider1.equals(provider2);
	}

	private void updateCurrentLocation() {
		mLat = (int) (mLocation.getLatitude() * 1e6);
		mLon = (int) (mLocation.getLongitude() * 1e6);
		Log.v(TAG, String.format("Current location is %d, %d", mLat, mLon));
		storeLocation();
		broadcastLocation();
		stopSelf();
	}

	private void broadcastLocation() {
		Intent result = new Intent(Consts.ACTION_LOCATE_ME);
		result.putExtra(Consts.LAT, mLat).putExtra(Consts.LON, mLon);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(result);
	}

	private void storeLocation() {
		boolean stored = mSharedPrefs.edit().putInt(Consts.LAT, mLat).putInt(Consts.LON, mLon).commit();
		Log.v(TAG, "Stored current location in SharedPrefs:" + stored);
	}
}
