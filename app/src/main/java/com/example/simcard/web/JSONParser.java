package com.example.simcard.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.util.Log;

public class JSONParser {

	private static InputStream is = null;
	private static String json = "";

	public JSONParser() {
	}

	public String makeHttpRequest(String url, String method,
			List<NameValuePair> params) {

		String error = null;
		// making HTTP request
		try {

			if (method == "POST") {
				Log.i("makeHttpRequest", "POST url=" + url);
				
				final HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
				
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);

				final int statusCode = httpResponse.getStatusLine()
						.getStatusCode();
				Log.i("makeHttpRequest", "POST url=" + url + "\n"
						+ "statusCode=" + statusCode);

				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} else if (method == "GET") {
				Log.i("makeHttpRequest", "GET url=" + url);

				final HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 10000);

				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;

				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				final int statusCode = httpResponse.getStatusLine()
						.getStatusCode();
				Log.i("makeHttpRequest", "GET url=" + url + "\n"
						+ "statusCode=" + statusCode);

				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}

		} catch (UnsupportedEncodingException e) {
			error = "UnsupportedEncodingException:"+e.getMessage();
		} catch (ClientProtocolException e) {
			error = "ClientProtocolException:"+e.getMessage();
		} catch (IOException e) {
			error = "IOException:"+e.getMessage();
		}

		if (error != null) {
			json = error;
			Log.e("makeHttpRequest", error);
		} else {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is), 8); // "iso-8859-1"
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				Log.i("makeHttpRequest", json);
			} catch (Exception e) {
				json = e.toString();
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
			try {
				json = getJsonFromXml();
			} catch (Exception e) {
				Log.e("json", e.getMessage());
			}
		}

		return json;
	}

	private String getJsonFromXml() throws ParserConfigurationException,
			SAXException, IOException {
		String stringElement = "";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(json.getBytes("UTF-8"));
		Document doc = dBuilder.parse(is);

		stringElement = doc.getElementsByTagName("string").item(0)
				.getTextContent();
		return stringElement;
	}
}
