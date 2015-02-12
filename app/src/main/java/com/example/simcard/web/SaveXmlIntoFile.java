package com.example.simcard.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.simcard.ListOfAllActivity;
import com.example.simcard.MyApplication;
import com.example.simcard.R;

public class SaveXmlIntoFile extends AsyncTask<Void, Void, String> {

	final String xmlString;
	File file;
	final ListOfAllActivity context;

	public SaveXmlIntoFile(final String xmlString, ListOfAllActivity context) {
		super();
		this.xmlString = xmlString;
		this.context = context;

	}

	@Override
	protected String doInBackground(Void... params) {
		String result = "";
		FileWriter writer = null;

		final File PATH = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		final String FILENAME = (String) android.text.format.DateFormat.format(
				"yyyyMMdd_hhmmss", new java.util.Date()) + ".xml";

		file = new File(PATH, FILENAME);

		try {

			writer = new FileWriter(file, true);
			writer.append(xmlString);
			writer.flush();

		} catch (IOException e) {
			result = e.getLocalizedMessage();
			e.printStackTrace();

		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				if (result.isEmpty()) {
					result = e.getLocalizedMessage();
				} else {
					result = result + "\n" + e.getLocalizedMessage();
				}
			}
		}

		return result;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		String message = "";
		if (result.isEmpty()) {
			message = context
					.getString(R.string.sales_orders_are_saved_to_file_message)
					+ file.toString();
		} else {
			message = context.getString(R.string.saving_error_message) + result;
		}

		new AlertDialog.Builder(context)
				.setTitle(
						MyApplication.context
								.getString(R.string.selling_sale_orders_dialog_title))
				.setMessage(message)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();

	}
}
