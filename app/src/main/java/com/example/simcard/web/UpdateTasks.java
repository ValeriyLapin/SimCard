package com.example.simcard.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.simcard.MyApplication;
import com.example.simcard.db.SQLiteAdapter;

public class UpdateTasks {

	private UpdateTasks() {

	}

	public static boolean isInternetConnected() {
		final ConnectivityManager conMgr = (ConnectivityManager) MyApplication.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	private static String loadJSON(String url) {

		final JSONParser jParser = new JSONParser();

		// parameters
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", MyApplication
				.getUserName()));
		params.add(new BasicNameValuePair("password", MyApplication
				.getPassword()));

		// GET request
		final String json = jParser.makeHttpRequest(url, "GET", params);
		return json;
	}

	public static void loadTablesFromServer() {

		UpdateTask updateTaskLocations = new UpdateTask(MyApplication.context) {
			@Override
			protected void myOnPostExecute(String json) {
				
				JSONArray jsonArray = null;

				// JSON Object parsing
				try {
					jsonArray = new JSONArray(json);
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data: \n" + json + "\n"
							+ e.toString());
				}

				if (jsonArray != null) {
					SQLiteAdapter.createLocations(jsonArray);
				}
			}
		};

		final UpdateTask updateTaskNominals = new UpdateTask(
				MyApplication.context) {
			@Override
			protected void myOnPostExecute(String json) {
				
				JSONArray jsonArray = null;

				// JSON Object parsing
				try {
					jsonArray = new JSONArray(json);
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data: \n" + json + "\n"
							+ e.toString());
				}

				if (jsonArray != null) {
					SQLiteAdapter.createNominals(jsonArray);
				}
			}
		};

		final UpdateTask updateTaskAgents = new UpdateTask(
				MyApplication.context) {
			@Override
			protected void myOnPostExecute(String json) {
				
				JSONArray jsonArray = null;

				// JSON Object parsing
				try {
					jsonArray = new JSONArray(json);
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data: \n" + json + "\n"
							+ e.toString());
				}

				if (jsonArray != null) {

					SQLiteAdapter.createAgents(jsonArray);
				}
			}
		};

		final UpdateTask updateTaskBuyers = new UpdateTask(
				MyApplication.context) {
			@Override
			protected void myOnPostExecute(String json) {
			
				JSONArray jsonArray = null;

				// JSON Object parsing
				try {
					jsonArray = new JSONArray(json);
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data: \n" + json + "\n"
							+ e.toString());
				}

				if (jsonArray != null) {

					SQLiteAdapter.createBuyers(jsonArray);
				}
			}
		};

		SQLiteAdapter.open(MyApplication.context);
		updateTaskAgents.execute(MyApplication.SERVER_URL + "Agents");
		updateTaskBuyers.execute(MyApplication.SERVER_URL + "Buyers");
		updateTaskLocations.execute(MyApplication.SERVER_URL + "Locations");
		updateTaskNominals.execute(MyApplication.SERVER_URL + "Nominals");
		// SQLiteAdapter.close();
	}


	public static boolean updateTaskLogin() {

		SQLiteAdapter.open(MyApplication.context);
		final String json = loadJSON(MyApplication.SERVER_URL + "Login");

		boolean res = false;

		JSONObject jsonObject = getJSONObjectFromString(json);

		if (jsonObject != null) {
			try {
				res = jsonObject.getBoolean("Success");
				Log.i("onPostExecute", "res=" + res);
			} catch (JSONException e) {
				Log.e("JSON Parser", e.toString());
			}
		}
		return res;
	}


	public static JSONObject getJSONObjectFromString(final String json) {
		JSONObject jObj = null;

		// JSON Object parsing
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser",
					"Error parsing data: \n" + json + "\n" + e.toString());
		}
		return jObj;
	}

}
