package com.example.simcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.simcard.db.SQLiteAdapter;
import com.example.simcard.web.UpdateTask;
import com.example.simcard.web.UpdateTasks;

public class MyApplication extends Application {

	// =============+=====================================
	// App's constants
	// ---------------------------------------------------

	public final static String SERVER_URL = "http://176.36.129.217:8082/Public/Service/OfflineClient.asmx/";
	// "http://192.168.0.8:8082/Public/Service/OfflineClient.asmx/";
	public static final int MAX_VAUCHER_INTERVAL_LENGTH = 2000;

	// ====================================================

	public static Context context;
	public static String userName;
	public static String password;
	private static boolean isRegistred = false;

	final private static String LOGIN_PREFERENCES = "LoginPrefs";
	final private static String LOGIN_PASSWORD = "Password";
	final private static String LOGIN_NAME = "Name";
	private static SharedPreferences mLoginSettings;

	private static List<UpdateTask> runningUpdateTasks = Collections
			.synchronizedList(new ArrayList<UpdateTask>());
	private static MyActivityWithProgressBar progressBarActivity;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		super.onCreate();

		mLoginSettings = getSharedPreferences(LOGIN_PREFERENCES,
				Context.MODE_PRIVATE);

		loadNameAndPasswordFromSharedPreferences();

	}

	public static boolean isRegistred() {
		return isRegistred;
	}

	private static void loadNameAndPasswordFromSharedPreferences() {
		if (mLoginSettings.contains(LOGIN_NAME)
				&& mLoginSettings.contains(LOGIN_PASSWORD)) {
			try {
				userName = mLoginSettings.getString(LOGIN_NAME, "");
				password = mLoginSettings.getString(LOGIN_PASSWORD, "");

				isRegistred = !password.equals(new String(""))
						&& !userName.equals(new String(""));

			} catch (Exception e) {
				Log.e("loadScoreTable", e.toString());
			}
		}
	}

	public static void saveNameAndPasswordToSharedPreferences() {
		final Editor edit = mLoginSettings.edit();
		edit.putString(LOGIN_NAME, userName);
		edit.putString(LOGIN_PASSWORD, password);
		edit.commit();
	}

	public static String getUserName() {
		return userName;
	}

	public static String getPassword() {
		return password;
	}

	public static void firstLogin() {
		isRegistred = true;
		saveNameAndPasswordToSharedPreferences();
		UpdateTasks.loadTablesFromServer();
	}

	@Override
	public void onTerminate() {
		terminateAllThreads();
		SQLiteAdapter.close();
		Log.i("MyApplication", "onTerminate");
		super.onTerminate();
	}

	synchronized public static void removeThread(UpdateTask updateTask) {
		runningUpdateTasks.remove(updateTask);
		if (runningUpdateTasks.size() == 0) {
			progressBarOff();
		}
	}

	public static void progressBarOff() {
		Log.i("thread", "progressBarOff");
		if (progressBarActivity != null) {

			try {
				progressBarActivity.runOnUiThread(new Runnable() {
					public void run() {
						try {
							if (((MyActivityWithProgressBar) progressBarActivity).progressBar
									.getVisibility() == View.VISIBLE) {
								progressBarActivity.progressBar
										.setVisibility(View.INVISIBLE);
							}
						} catch (Exception e) {
							Log.e("progressBarOn", e.toString());
						}
					}
				});

			} catch (Exception e) {
				Log.e("progressBarOn", e.toString());
			}

		}
	}

	public static void progressBarOn() {
		Log.i("thread", "progressBarOn");
		if (progressBarActivity != null) {
			try {
				progressBarActivity.runOnUiThread(new Runnable() {
					public void run() {
						try {
							if (((MyActivityWithProgressBar) progressBarActivity).progressBar
									.getVisibility() == View.INVISIBLE) {
								progressBarActivity.progressBar
										.setVisibility(View.VISIBLE);
							}
						} catch (Exception e) {
							Log.e("progressBarOn", e.toString());
						}
					}
				});

			} catch (Exception e) {
				Log.e("progressBarOn", e.toString());
			}

		}
	}

	private static void terminateAllThreads() {
		for (int i = 0; i < runningUpdateTasks.size(); i++) {
			final UpdateTask updateTask = runningUpdateTasks.get(i);
			if (!updateTask.isCancelled()) {
				try {
					updateTask.cancel(true);
				} catch (Exception e) {
					Log.i("threads", e.toString());
				}
			}
			progressBarOff();

		}
	}

	synchronized public static void addThread(UpdateTask updateTask) {
		runningUpdateTasks.add(updateTask);
		progressBarOn();
	}

	public static void registerCurrentProgressBarActivity(Activity activity) {
		Log.i("registerCurrentProgressBar", "runningUpdateTasks.size()="
				+ runningUpdateTasks.size());
		progressBarActivity = (MyActivityWithProgressBar) activity;

		if (runningUpdateTasks.size() > 0) {
			progressBarOn();
		}

	}

	public static void showErrorToastMessage(final String message) {
		final Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.getView().setBackgroundColor(
				MyApplication.context.getResources().getColor(
						R.color.toast_color_red));
		toast.show();
	}

}
