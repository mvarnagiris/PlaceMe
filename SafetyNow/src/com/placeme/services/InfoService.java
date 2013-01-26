package com.placeme.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpStatus;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.placeme.BuildConfig;
import com.placeme.Consts;
import com.placeme.model.CardInfo;
import com.placeme.model.Place;

public class InfoService extends IntentService {

	private static final String	BASE_URL		= "http://dia.offsetdesign.co.uk/api/data.json";

	private static final String	TAG				= "InfoService";

	public InfoService() {
		super("InfoService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Get values
		final double lat = intent.getIntExtra(Consts.LAT, 0) / 1e6;
		final double lng = intent.getIntExtra(Consts.LON, 0) / 1e6;
		//final String categ = intent.getStringExtra(Consts.CATEG);
		String categ = Consts.CATEG_LIVE_HERE;

		final String url = BASE_URL + "/?lat=" + lat + "&long=" + lng + "&cat=" + categ;
		String response = null;
		try {
			response = makeRequest(url);
			if (!TextUtils.isEmpty(response)) {
				Pair<Place, ArrayList<CardInfo>> result = parseCardsInfo(response);
				Intent resultIntent = new Intent(Consts.ACTION_GET_DATA);
				resultIntent.putExtra(Consts.PLACE, result.first);
				resultIntent.putExtra(Consts.CARDS, result.second);
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(resultIntent);
			}
		}
		catch (Exception e) {
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
	protected HttpURLConnection getConnection(String url) throws Exception {
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
	 * Makes a request to given to given URL with given data to upload and additional parameters to
	 * set on connection.
	 * 
	 * @param url
	 *            URL for connection.
	 * @return Response string.
	 * @throws Exception
	 *             Any exception.
	 */
	protected String makeRequest(String url) throws Exception {
		// Debug log
		if (BuildConfig.DEBUG) {
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
		if (responseCode == HttpStatus.SC_OK) {
			try {
				is = connection.getInputStream();
				response = readInputStream(is);
			}
			finally {
				if (is != null) is.close();
				if (connection != null) connection.disconnect();
			}
		}

		// Debug log
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "=========================================================");
			Log.i(TAG, "Request " + url);
			Log.i(TAG, "Response code : " + responseCode);
			Log.i(TAG, "Response : " + response);
			Log.i(TAG, "=========================================================");
		}

		return response;
	}

	public static String readInputStream(InputStream is) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String s = null;

		while ((s = r.readLine()) != null)
			sb.append(s);

		return sb.toString();
	}

	private Pair<Place, ArrayList<CardInfo>> parseCardsInfo(String response) {
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonObject rootObj = parser.parse(response).getAsJsonObject();
		JsonObject placeObj = rootObj.get(Consts.JSON_LOCATION).getAsJsonObject();

		Place place = gson.fromJson(placeObj, Place.class);
		ArrayList<CardInfo> cards = new ArrayList<CardInfo>();

		JsonArray array = rootObj.get(Consts.JSON_DATASETS).getAsJsonArray();
		if (null != array) {
			int size = array.size();
			for (int i = 0; i < size; i++) {
				cards.add(gson.fromJson(array.get(i), CardInfo.class));
			}
		}
		return new Pair<Place, ArrayList<CardInfo>>(place, cards);
	}

}