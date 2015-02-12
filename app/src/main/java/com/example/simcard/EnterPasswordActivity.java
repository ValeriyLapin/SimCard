package com.example.simcard;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.simcard.db.SQLiteAdapter;
import com.example.simcard.web.UpdateTask;
import com.example.simcard.web.UpdateTasks;

/**
 * A login screen that offers login via name/password.
 */

public class EnterPasswordActivity extends MyActivityWithProgressBar {

	private EditText editTextPassword;
	private EditText editTextName;

	// private boolean isCheckingLoginInProgress = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("EnterPasswordActivity", "onCreate");

		if (MyApplication.isRegistred()) {
			finish();
			Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
			startActivity(myIntent);
		}

		setContentView(R.layout.activity_enter_password);

		setTitle(getString(R.string.title_activity_enter_password));

		editTextPassword = (EditText) findViewById(R.id.password);
		editTextName = (EditText) findViewById(R.id.name);

		final Button sign_in_button = (Button) findViewById(R.id.sign_in_button);
		sign_in_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				MyApplication.userName = editTextName.getText().toString();
				MyApplication.password = editTextPassword.getText().toString();

				if (!UpdateTasks.isInternetConnected()) {

					MyApplication.showErrorToastMessage(MyApplication.context
							.getString(R.string.error_no_internet));
				} else {
					tryToLogin();
				}
			}
		});
	}

	private void tryToLogin() {
		Log.i("EnterPasswordActivity", "tryToLogin");

		final UpdateTask updateTaskLogin = new UpdateTask(MyApplication.context) {
			@Override
			protected void myOnPostExecute(String json) {
				
				Log.i("tryToLogin", "onPostExecute, json=" + json);


				boolean res = false;

				if (json.contains("Exception")) {
					MyApplication
							.showErrorToastMessage(getString(R.string.error_message)
									+ json);
				} else {
					JSONObject jObj = null;

					// JSON Object parsing
					try {
						jObj = new JSONObject(json);
					} catch (JSONException e) {
						Log.e("JSON Parser",
								"Error parsing data " + e.toString());
					}

					if (jObj != null) {
						try {
							res = jObj.getBoolean("Success");
							Log.i("tryToLogin", "onPostExecute: res=" + res);
						} catch (JSONException e) {
							Log.e("tryToLogin", "JSON Parser:\n" + e.toString());
						}
					}
					
					if (res) {

						MyApplication.firstLogin();
						Intent myIntent = new Intent(MyApplication.context,
								MainActivity.class);
						startActivity(myIntent);
						finish();

					} else {
						final String message = MyApplication.context
								.getString(R.string.error_incorrect_name_or_password);
						MyApplication.showErrorToastMessage(message);
					}

				}

			}

		};

		SQLiteAdapter.open(MyApplication.context);
		updateTaskLogin.execute(MyApplication.SERVER_URL + "Login");
		// SQLiteAdapter.close();
	}

}
