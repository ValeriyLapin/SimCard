package com.example.simcard.db;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Nominal implements Serializable {
	// {"Name":"mnn - 10","Id":169}
	private static final long serialVersionUID = 1L;
	public String name;
	public int id;

	public Nominal() {

	}
	
	

	public Nominal(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}



	public Nominal(JSONObject jsonObject) {
		try {
			name = jsonObject.getString("Name");
		} catch (JSONException e) {
			Log.e("Nominal", e.toString());
		}

		try {
			id = jsonObject.getInt("Id");
		} catch (JSONException e) {
			Log.e("Nominal", e.toString());
		}

	}



	@Override
	public String toString() {
		return "" + name;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nominal other = (Nominal) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
