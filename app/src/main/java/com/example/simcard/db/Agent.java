package com.example.simcard.db;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Agent {
	public String text;
	public boolean isDefault = false;
	public int value;

	public Agent(String text, boolean isDefault, int value) {
		super();
		this.text = text;
		this.isDefault = isDefault;
		this.value = value;
	}

	public Agent() {

	}

	public Agent(JSONObject jsonObject) {
		Log.i("Agent", jsonObject.toString());
		try {
			isDefault = jsonObject.getBoolean("IsDefault");
		} catch (JSONException e) {
			Log.e("Agent", e.toString());
		}
		try {
			text = jsonObject.getString("Text");
		} catch (JSONException e) {
			Log.e("Agent", e.toString());
		}
		try {
			value = jsonObject.getInt("Value");
		} catch (JSONException e) {
			Log.e("Agent", e.toString());
		}

	}

	@Override
	public String toString() {
		return text;
//		return text + "-" + value;
	}

	public String toStringLong() {
		return "Agent [text=" + text + ", isDefault=" + isDefault + ", value="
				+ value + "]";
	}

}
