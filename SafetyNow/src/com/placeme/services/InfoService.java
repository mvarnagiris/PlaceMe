package com.placeme.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.placeme.BuildConfig;

public class InfoService extends IntentService
{
	public static final String	EXTRA_LAT	= "EXTRA_LAT";
	public static final String	EXTRA_LNG	= "EXTRA_LNG";
	public static final String	EXTRA_TYPE	= "EXTRA_TYPE";

	private static final String	BASE_URL	= "";				// FIXME Add proper URL

	private static final String	TAG			= "InfoService";

	public InfoService()
	{
		super("InfoService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		// Get values
		final double lat = intent.getDoubleExtra(EXTRA_LAT, 0);
		final double lng = intent.getDoubleExtra(EXTRA_LNG, 0);
		final int type = intent.getIntExtra(EXTRA_TYPE, 0);

		final String url = BASE_URL + "/?lat=" + lat + "&lng=" + lng + "&type=" + type;
		String response = null;
		try
		{
			response = makeRequest(url);
		}
		catch (Exception e)
		{
			Log.e(TAG, "Request failed", e);
		}

		// TODO Parse
		// TODO Broadcast parsed objects
	}

	// Protected methods
	// --------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Opens connection to the server and uploads data if necessary.
	 * 
	 * @param url
	 *            URL for connection.
	 * @return Open connection.
	 * @throws Exception
	 *             Any exception.
	 */
	protected HttpURLConnection getConnection(String url) throws Exception
	{
		// Prepare connection
		final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setConnectTimeout(30000);
		connection.setReadTimeout(30000);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/json");

		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.connect();

		return connection;
	}

	/**
	 * Makes a request to given to given URL with given data to upload and additional parameters to set on connection.
	 * 
	 * @param url
	 *            URL for connection.
	 * @return Response string.
	 * @throws Exception
	 *             Any exception.
	 */
	protected String makeRequest(String url) throws Exception
	{
		// Debug log
		if (BuildConfig.DEBUG)
		{
			Log.i(TAG, "---------------------------------------------------------");
			Log.i(TAG, "GET " + url);
			Log.i(TAG, "---------------------------------------------------------");
		}

		// Connect
		final HttpURLConnection connection = getConnection(url);

		// Get response
		final int responseCode = connection.getResponseCode();

		// Check response code and handle errors if necessary
		InputStream is = null;
		String response = null;

		// If request was not successful, read error
		if (responseCode == HttpStatus.SC_OK)
		{
			try
			{
				is = connection.getInputStream();
				response = readInputStream(is);
			}
			finally
			{
				if (is != null)
					is.close();
				if (connection != null)
					connection.disconnect();
			}
		}

		// Debug log
		if (BuildConfig.DEBUG)
		{
			Log.i(TAG, "=========================================================");
			Log.i(TAG, "Request " + url);
			Log.i(TAG, "Response code : " + responseCode);
			Log.i(TAG, "Response : " + response);
			Log.i(TAG, "=========================================================");
		}

		return response;
	}

	public static String readInputStream(InputStream is) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String s = null;

		while ((s = r.readLine()) != null)
			sb.append(s);

		return sb.toString();
	}
}