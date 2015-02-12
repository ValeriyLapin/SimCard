package com.example.simcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends MyActivityWithProgressBar implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((Button) findViewById(R.id.newVaucherButton)).setOnClickListener(this);
		((Button) findViewById(R.id.newPacketButton)).setOnClickListener(this);
		((Button) findViewById(R.id.listOfAllButton)).setOnClickListener(this);
		((Button) findViewById(R.id.listOfAllSyncButton))
				.setOnClickListener(this);
		((Button) findViewById(R.id.exitButton)).setOnClickListener(this);

	}


	@Override
	public void onClick(View arg0) {
		Intent intent;
		switch (arg0.getId()) {
		case R.id.newVaucherButton:
			Log.i("onClick", "newVaucherButton");

			intent = new Intent(MyApplication.context, SaleOderActivity.class);
			intent.putExtra("isVaucher", true);
			startActivity(intent);

			break;
		case R.id.newPacketButton:
			Log.i("onClick", "newPacketButton");

			intent = new Intent(MyApplication.context, SaleOderActivity.class);
			intent.putExtra("isVaucher", false);
			startActivity(intent);

			break;
		case R.id.listOfAllButton:
			Log.i("onClick", "listOfAllButton");
			intent = new Intent(MyApplication.context, ListOfAllActivity.class);
			intent.putExtra("OrderStatus", 1);
			startActivity(intent);
			break;

		case R.id.listOfAllSyncButton:
			Log.i("onClick", "listOfAllSyncButton");

			intent = new Intent(MyApplication.context, ListOfAllActivity.class);
			intent.putExtra("OrderStatus", 2);
			startActivity(intent);

			break;

		case R.id.exitButton:
			Log.i("onClick", "exitButton");
			finish();
			break;
		}

	}

}
