package com.example.simcard.db;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Buyer {
	// {"Name":"Name","AgentsIds":[6047,6047,6050,6050],"Id":37539}
	public String name;
	String agentsIds;
	public int id;

	public Buyer() {

	}

	public Buyer(String name, String agentsIds, int id) {
		super();
		this.name = name;
		this.agentsIds = agentsIds;
		this.id = id;
	}

	

	public Buyer(JSONObject jsonObject) {
		try {
			agentsIds = jsonObject.getString("AgentsIds");
		} catch (JSONException e) {
			Log.e("Location", e.toString());
			agentsIds="null";
		}

		try {
			name = jsonObject.getString("Name");
		} catch (JSONException e) {
			Log.e("Buyer", e.toString());
		}
		try {
			id = jsonObject.getInt("Id");
		} catch (JSONException e) {
			Log.e("Buyer", e.toString());
		}
	}

	@Override
	public String toString() {
	//	return name + "-" + id + " a" + agentsIds;
		return name;
	}

	public String toStringLong() {
		return "Buyer [name=" + name + ", agentsIds=" + agentsIds + ", id="
				+ id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((agentsIds == null) ? 0 : agentsIds.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Buyer other = (Buyer) obj;
		if (agentsIds == null) {
			if (other.agentsIds != null)
				return false;
		} else if (!agentsIds.equals(other.agentsIds))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
