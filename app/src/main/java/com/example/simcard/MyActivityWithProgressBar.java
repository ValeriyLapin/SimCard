package com.example.simcard;

import android.app.Activity;
import android.os.Bundle;

import com.example.simcard.web.ButteryProgressBar;

public class MyActivityWithProgressBar extends Activity {
	public ButteryProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		progressBar = ButteryProgressBar.getInstance(this);
		MyApplication.registerCurrentProgressBarActivity(this);

	}

}
