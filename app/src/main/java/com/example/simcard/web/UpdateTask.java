package com.example.simcard.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.simcard.MyApplication;
import com.example.simcard.db.SaleOrder;

public class UpdateTask extends AsyncTask<String, Void, String> {
	Context context;
	private String xmlString;
	final public UpdateTask task;

	final ArrayList<SaleOrder> list;

	public UpdateTask(Context context) {
		super();
		this.context = context;
		this.list = null;
		this.task = this;

	}

	public UpdateTask(Context context, ArrayList<SaleOrder> list) {
		super();
		this.context = context;
		// this.xmlString = xmlString;
		this.list = list;
		this.task = this;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... urls) {

		MyApplication.addThread(this);

		final String res = loadJSON(urls[0]);

		return res;
	}

	public String loadJSON(String url) {
		String json = "";
		JSONParser jParser = new JSONParser();

		// parameters
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", MyApplication
				.getUserName()));
		params.add(new BasicNameValuePair("password", MyApplication
				.getPassword()));

		// GET request

		// Public/Service/OfflineClient.asmx/SaveSaleOrder
		if (list == null) {
			json = jParser.makeHttpRequest(url, "GET", params);
		} else {
			xmlString = ConvertorToXML.getXml(list);
			Log.i("2xml", "length=" + xmlString.length() + "\n" + xmlString);
			if (xmlString != null && !xmlString.isEmpty()) {
				params.add(new BasicNameValuePair("data", xmlString));
				json = jParser.makeHttpRequest(url, "POST", params);
			}

		}

		return json;
	}

	@Override
	protected void onPostExecute(String json) {
		super.onPostExecute(json);
		myOnPostExecute(json);
		MyApplication.removeThread(this);
	}

	protected void myOnPostExecute(String json) {

	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

}
