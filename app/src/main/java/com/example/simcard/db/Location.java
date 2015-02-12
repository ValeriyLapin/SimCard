package com.example.simcard.db;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Location {
	// {"BuyerId":37525,"Name":"334 Address","AgentsIds":null,"Id":32723}

	int buyerId;
	public String name;
	String agentsIds;
	public int id;

	public Location(int buyerId, String name, String agentsIds, int id) {
		super();
		this.buyerId = buyerId;
		this.name = name;
		this.agentsIds = agentsIds;
		this.id = id;
	}

	public Location() {
	}

	public Location(JSONObject jsonObject) {
		Log.i("Location", jsonObject.toString());
		try {
			name = jsonObject.getString("Name");
		} catch (JSONException e) {
			Log.e("Location", e.toString());
		}

		try {
			id = jsonObject.getInt("Id");
		} catch (JSONException e) {
			Log.e("Location", e.toString());
		}

		try {
			buyerId = jsonObject.getInt("BuyerId");
		} catch (JSONException e) {
			Log.e("Location", e.toString());
		}
		try {
			agentsIds = jsonObject.getString("AgentsIds");
		} catch (JSONException e) {
			Log.e("Location", e.toString());
			agentsIds = "null";
		}
	}

	@Override
	public String toString() {
		// return name + " b" + buyerId + " a" + agentsIds;
		return name;
	}
}
