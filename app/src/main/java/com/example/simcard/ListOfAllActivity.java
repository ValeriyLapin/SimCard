package com.example.simcard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simcard.db.SQLiteAdapter;
import com.example.simcard.db.SaleOrder;
import com.example.simcard.web.ConvertorToXML;
import com.example.simcard.web.SaveXmlIntoFile;
import com.example.simcard.web.SynchonizationErrorReport;
import com.example.simcard.web.UpdateTask;
import com.example.simcard.web.UpdateTasks;

public class ListOfAllActivity extends MyActivityWithProgressBar implements
		OnClickListener {

	private ArrayAdapter<SaleOrder> listAdapter;
	private ArrayList<SaleOrder> listOfSaleOrders;
	private ListView listView;
	// private int checkedListItems = 0;
	private Button delSelectedButton;
	private Button dellAllButton;
	private Button synchSelectedButton;
	private Button synchAllButton;
	private Button saveToFileButton;
	private int orderStatus;
	CheckBox checkBoxAll;
	final int EDIT_ORDER_ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list_of_all);

		final Intent intent = getIntent();
		final Bundle bundle = intent.getExtras();
		orderStatus = bundle.getInt("OrderStatus", 1);
		Log.i("ListOfAllActivity", "orderStatus=" + orderStatus);

		delSelectedButton = (Button) findViewById(R.id.delSelectedButton);
		synchSelectedButton = (Button) findViewById(R.id.synchSelectedButton);
		synchAllButton = (Button) findViewById(R.id.synchAllButton);

		dellAllButton = (Button) findViewById(R.id.dellAllButton);

		saveToFileButton = (Button) findViewById(R.id.saveToFileButton);
		saveToFileButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SaveXmlIntoFile saveXmlToFile = new SaveXmlIntoFile(
						ConvertorToXML.getXml(getListOfSelected()),
						ListOfAllActivity.this);
				saveXmlToFile.execute();

			}
		});

		checkBoxAll = (CheckBox) findViewById(R.id.checkBoxAll);

		try {
			int id = Resources.getSystem().getIdentifier(
					"btn_check_holo_light", "drawable", "android");
			checkBoxAll.setButtonDrawable(id);
		} catch (Exception e) {
			Log.e("getView", e.toString());
		}

		listView = (ListView) findViewById(R.id.listOFSaleOrdersListView);
		initListAdapter();

		checkBoxAll.setOnClickListener(this);

		ActionBar ab = getActionBar();
		if (orderStatus == 1) {
			ab.setTitle(getString(R.string.listOfAllNewButtonString));
			synchSelectedButton.setVisibility(View.VISIBLE);
			saveToFileButton.setVisibility(View.VISIBLE);
			dellAllButton.setVisibility(View.GONE);
			synchAllButton.setVisibility(View.VISIBLE);
		} else {
			synchAllButton.setVisibility(View.GONE);
			dellAllButton.setVisibility(View.VISIBLE);
			ab.setTitle(getString(R.string.listOfAllSynhButtonString));
			synchSelectedButton.setVisibility(View.GONE);
		}

		delSelectedButton.setOnClickListener(this);
		synchSelectedButton.setOnClickListener(this);
		dellAllButton.setOnClickListener(this);
		synchAllButton.setOnClickListener(this);

		setEnablityOfButtons();

	}

	protected void setEnablityOfButtons() {
		final int checkedListItems = getCountOfSelected();
		Log.i("setActivityOfDeleteListItemsButton", "checkedListItems="
				+ checkedListItems);

		if (checkedListItems == listAdapter.getCount()) {
			checkBoxAll.setChecked(true);
		} else {
			checkBoxAll.setChecked(false);
		}

		if (checkedListItems > 0) {
			delSelectedButton.setEnabled(true);
			synchSelectedButton.setEnabled(true);
			saveToFileButton.setEnabled(true);
		} else {
			delSelectedButton.setEnabled(false);
			synchSelectedButton.setEnabled(false);
			saveToFileButton.setEnabled(false);
		}

		if (listOfSaleOrders.size() > 0) {
			synchAllButton.setEnabled(true);
			dellAllButton.setEnabled(true);
			checkBoxAll.setClickable(true);
		} else {
			synchAllButton.setEnabled(false);
			dellAllButton.setEnabled(false);
			checkBoxAll.setClickable(false);
			checkBoxAll.setChecked(false);
		}

		if (isSynchronizingNow) {
			synchSelectedButton.setEnabled(false);
			synchAllButton.setEnabled(false);
		}

	}

	private void showToastErrorMessage(final String message) {
		final Toast toast = Toast.makeText(MyApplication.context, message,
				Toast.LENGTH_LONG);
		toast.getView().setBackgroundColor(
				MyApplication.context.getResources().getColor(
						R.color.toast_color_red));
		toast.show();
	}

	private void showToastSuccessMessage(final String message) {
		final Toast toast = Toast.makeText(MyApplication.context, message,
				Toast.LENGTH_LONG);
		toast.getView().setBackgroundColor(
				MyApplication.context.getResources().getColor(
						R.color.toast_color_green));
		toast.show();
	}

	private void initListAdapter() {
		SQLiteAdapter.open(MyApplication.context);
		listOfSaleOrders = SQLiteAdapter.getAllSaleOrdersByStatus(orderStatus,
				false);

		Log.i("initListAdapter", listOfSaleOrders.toString());

		listAdapter = new ArrayAdapter<SaleOrder>(this,
				R.layout.list_of_all_table_row, listOfSaleOrders) {

			private final OnClickListener checkBoxOnClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					final SaleOrder order = (SaleOrder) (v.getTag());
					order.isSelected = !order.isSelected;

					listAdapter.notifyDataSetChanged();

					setEnablityOfButtons();

				}
			};

			@SuppressLint("SimpleDateFormat")
			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				Log.i("test", "getView pos=" + position);

				final SaleOrder order = listOfSaleOrders.get(position);
				ViewHolder holder; // to reference the child views for later
									// actions
				if (null == convertView) {
					Log.i("test", "getView null == convertView");
					convertView = LayoutInflater.from(MyApplication.context)
							.inflate(R.layout.list_of_all_table_row, parent,
									false);

					holder = new ViewHolder();

					holder.editImageView = (ImageView) convertView
							.findViewById(R.id.editImageView);

					holder.checkBox1 = (CheckBox) convertView
							.findViewById(R.id.checkBox1);

					holder.column3 = (TextView) convertView
							.findViewById(R.id.column3);

					holder.column4 = (TextView) convertView
							.findViewById(R.id.column4);
					holder.column5 = (TextView) convertView
							.findViewById(R.id.column5);
					holder.column6 = (TextView) convertView
							.findViewById(R.id.column6);
					holder.column7 = (TextView) convertView
							.findViewById(R.id.column7);
					holder.column8 = (TextView) convertView
							.findViewById(R.id.column8);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				if (orderStatus == 2) {
					holder.editImageView.setVisibility(View.INVISIBLE);
				} else {
					holder.editImageView.setTag(order);
					holder.editImageView
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									final SaleOrder order = (SaleOrder) (v
											.getTag());
									final Intent intent = new Intent(
											MyApplication.context,
											SaleOderActivity.class);
									intent.putExtra("UId", order.getUId());

									if (order.OrderType == 2) {
										intent.putExtra("isVaucher", true);
									} else {
										intent.putExtra("isVaucher", false);
									}
									Log.i("testAll", "before start the intent");
									startActivityForResult(intent,
											EDIT_ORDER_ACTIVITY);

								}
							});
				}

				holder.checkBox1.setTag(order);
				try {
					int id = Resources.getSystem().getIdentifier(
							"btn_check_holo_light", "drawable", "android");
					holder.checkBox1.setButtonDrawable(id);
				} catch (Exception e) {
					Log.e("getView", e.toString());
				}

				if (order.isSelected) {
					holder.checkBox1.setChecked(true);
				} else {
					holder.checkBox1.setChecked(false);
				}

				holder.checkBox1.setOnClickListener(checkBoxOnClickListener);

				if (order.OrderStatus == 1) {
					if (order.errorMessage != null
							&& order.errorMessage.isEmpty()) {
						holder.column3.setText(MyApplication.context
								.getResources().getString(R.string.status_new));
					} else {
						holder.column3.setText(MyApplication.context
								.getResources().getString(
										R.string.status_with_errors));
						holder.column3.setTextColor(Color.RED);

						holder.column3.setTag(order);
						holder.column3
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {

										final SaleOrder order = (SaleOrder) (v
												.getTag());

										Log.i("error", order.errorMessage);

										final StringBuffer stringBuffer = new StringBuffer();
										stringBuffer
												.append(MyApplication.context
														.getString(R.string.dateColumnTitle))
												.append(": ")
												.append(order.getDtString())
												.append("\n")
												.append(MyApplication.context
														.getString(R.string.typeColumnTitle))
												.append(": ");

										if (order.OrderType == 1) {
											stringBuffer
													.append(MyApplication.context
															.getString(R.string.typeOfNakladnayaPStringShort));
										} else if (order.OrderType == 2) {
											stringBuffer
													.append(MyApplication.context
															.getString(R.string.typeOfNakladnayaVStringShort));
										}

										stringBuffer
												.append("\n")
												.append(MyApplication.context
														.getString(R.string.pokupatelColumnTitle))
												.append(": ")
												.append(order.buyer.name)
												.append("\n")
												.append(MyApplication.context
														.getString(R.string.sellPointColumnTitle))
												.append(": ")
												.append(order.location.name)
												.append("\n");

										new AlertDialog.Builder(
												ListOfAllActivity.this)

												.setTitle(
														MyApplication.context
																.getString(R.string.error_description_title))
												.setMessage(order.errorMessage)
												.setPositiveButton(
														android.R.string.yes,
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {

															}
														})

												.setIcon(
														android.R.drawable.ic_dialog_alert)
												.show();

									}
								});
					}

				} else {
					if (order.errorMessage == null
							|| order.errorMessage.isEmpty()) {
						holder.column3.setText(MyApplication.context
								.getResources().getString(
										R.string.status_synchronized));
					} else {
						holder.column3.setText(MyApplication.context
								.getResources().getString(
										R.string.status_with_errors));
					}
				}

				if (order.OrderType == 2) {
					// vaucher
					holder.column4.setText(MyApplication.context.getResources()
							.getString(R.string.typeOfNakladnayaVStringShort));
				} else {
					holder.column4.setText(MyApplication.context.getResources()
							.getString(R.string.typeOfNakladnayaPStringShort));
				}

				final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
				final String reportDate = df.format(order.dt);
				holder.column5.setText(reportDate);

				holder.column6.setText(String.valueOf(order.buyer));

				holder.column7.setText(String.valueOf(order.location));

				holder.column8.setText(String.valueOf(order.getCards()));
				Log.i("test", "order.listOf=" + order.getUId()
						+ ",\norder.listOf=" + order.listOfCards);
				return convertView;
			}
		};
		listView.setAdapter(listAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	static class ViewHolder {
		ImageView editImageView;
		CheckBox checkBox1;
		TextView column3;
		TextView column4;
		TextView column5;
		TextView column6;
		TextView column7;
		TextView column8;
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.delSelectedButton:
			Log.i("onClick", "delSelectedButton");
			delSaleOrders(getListOfSelected(),
					getString(R.string.delling_some_sale_orders_dialog_title));
			setEnablityOfButtons();
			break;
		case R.id.synchSelectedButton:
			Log.i("onClick", "synchSelected ");
			syncSelected();

			break;

		case R.id.dellAllButton:
			delSaleOrders(listOfSaleOrders,
					getString(R.string.delling_all_sale_orders_dialog_title));
			setEnablityOfButtons();
			break;

		case R.id.synchAllButton:
			Log.i("onClick", "synchAllButton");

			if (!UpdateTasks.isInternetConnected()) {
				showToastErrorMessage(MyApplication.context
						.getString(R.string.error_no_internet));

			} else {
				loadOrdersToServerAndSync(listOfSaleOrders);

			}

			break;

		case R.id.checkBoxAll:
			if (checkBoxAll.isChecked()) {
				for (SaleOrder order : listOfSaleOrders) {
					order.isSelected = true;
				}

				setEnablityOfButtons();
				listAdapter.notifyDataSetChanged();
			} else {
				for (SaleOrder order : listOfSaleOrders) {
					order.isSelected = false;
				}

				setEnablityOfButtons();
				listAdapter.notifyDataSetChanged();

			}

			break;
		}

	}

	private void delSaleOrders(final ArrayList<SaleOrder> list,
			String stringTitle) {

		if (list != null && list.size() > 0) {
			final StringBuffer stringBuffer = new StringBuffer();

			for (SaleOrder saleOrder : list) {

				stringBuffer.append(saleOrder.getDtString()).append(
						", " + getString(R.string.buyer) + ": ");
				try {

					stringBuffer.append(saleOrder.buyer.name);
				} catch (Exception e) {
					Log.e("delSaleOrders", "" + e.getLocalizedMessage());
				}

				stringBuffer.append("\n");
			}

			new AlertDialog.Builder(ListOfAllActivity.this)
					.setTitle(stringTitle)
					.setMessage(stringBuffer.toString())
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (list != null && list.size() > 0) {
										SQLiteAdapter
												.open(MyApplication.context);
										SQLiteAdapter.delSaleOrders(list);
										initListAdapter();
									}
									// checkedListItems = 0;
									setEnablityOfButtons();
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();

		}

	}

	boolean isSynchronizingNow = false;

	private void syncSelected() {
		if (!UpdateTasks.isInternetConnected()) {

			showToastErrorMessage(MyApplication.context
					.getString(R.string.error_no_internet));

		} else {
			loadOrdersToServerAndSync(getListOfSelected());
		}
	}

	private ArrayList<SaleOrder> getListOfSelected() {
		ArrayList<SaleOrder> listOfSelected = new ArrayList<SaleOrder>();
		for (SaleOrder order : listOfSaleOrders) {
			if (order.isSelected)
				listOfSelected.add(order);
		}
		return listOfSelected;
	}

	private int getCountOfSelected() {
		int res = 0;
		for (SaleOrder order : listOfSaleOrders) {
			if (order.isSelected)
				res++;
		}
		return res;
	}

	SynchonizationErrorReport errorReport;

	private void loadOrdersToServerAndSync(ArrayList<SaleOrder> list) {
		isSynchronizingNow = true;
		setEnablityOfButtons();
		errorReport = new SynchonizationErrorReport();
		Log.i("synh", "list size =" + list.size());

		if (list == null || list.size() == 0)
			return;

		final UpdateTask updateTaskSaveSaleOrder = new UpdateTask(
				MyApplication.context, list) {
			@Override
			protected void myOnPostExecute(String json) {

				boolean res = false;

				Log.i("loadOrdersToServerAndSync", "json=" + json);

				if (json.contains("Exception")) {

					showToastErrorMessage(MyApplication.context
							.getString(R.string.status_with_errors)
							+ ":\n"
							+ json);
				} else {
					workWithResponse(json, res);
					UpdateTasks.loadTablesFromServer();
				}
				isSynchronizingNow = false;
				setEnablityOfButtons();

			}

			private void workWithResponse(String json, boolean res) {

				String message = "";
				JSONObject jsonObject = UpdateTasks
						.getJSONObjectFromString(json);

				if (jsonObject == null) {
					showToastErrorMessage(getString(R.string.incorrect_answer_of_server_message));
				} else {

					Log.i("loadOrdersToServerAndSync", jsonObject.toString());
					try {
						res = jsonObject.getBoolean("Success");
						message = jsonObject.getString("Message");

						Log.i("onPostExecute", "res=" + res);
					} catch (JSONException e) {
						errorReport.add(e);
					}

					if (res == false) {
						if (!message.isEmpty())
							showToastErrorMessage(message);
						parseResponse(res, jsonObject);

					} else if (res == true) {
						if (!message.isEmpty()) {
							showToastSuccessMessage(message);
							parseResponse(res, jsonObject);
						}
					}
				}
			}
		};

		updateTaskSaveSaleOrder.execute(MyApplication.SERVER_URL
				+ "SaveSaleOrders");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Log.i("testAll", "onActivityResult start the intent");
			initListAdapter();
		}

	}

	private void parseResponse(final boolean res, final JSONObject jsonObject) {
		Log.i("parse", "parseResponse res=" + res + ", jsonObject="
				+ jsonObject);

		for (int i = 0; i < jsonObject.names().length(); i++) {
			try {
				Log.i("parse",
						"key="
								+ jsonObject.names().getString(i)
								+ "\nvalue = "
								+ jsonObject.get(jsonObject.names()
										.getString(i)));
			} catch (JSONException e) {
				errorReport.add(e);
			}
		}
		Log.i("parse", "-----------------------------------------");

		try {
			String string = (String) jsonObject.get("Data");

			JSONObject data_jsonObject = new JSONObject(string);

			for (int i = 0; i < data_jsonObject.names().length(); i++) {
				try {
					Log.i("parse",
							"key="
									+ data_jsonObject.names().getString(i)
									+ "\nvalue = "
									+ data_jsonObject.get(data_jsonObject
											.names().getString(i)));

				} catch (JSONException e) {
					errorReport.add(e);
				}
				Log.i("parse", "----------------------------------");
			}

			try {
				if (data_jsonObject.has("FullySavedOrders"))
					workWithFullySavedOrders(data_jsonObject
							.getJSONArray("FullySavedOrders"));

			} catch (JSONException e) {
				errorReport.add(e);
			}

			try {
				if (data_jsonObject.has("ScratchCardErrorModel"))
					workWithCardErrorModels(data_jsonObject
							.getJSONArray("ScratchCardErrorModel"));

			} catch (JSONException e) {
				errorReport.add(e);
			}

			try {
				if (data_jsonObject.has("SimCardErrorModel"))
					workWithCardErrorModels(data_jsonObject
							.getJSONArray("SimCardErrorModel"));
			} catch (JSONException e) {
				errorReport.add(e);
			}

		} catch (JSONException e) {
			errorReport.add(e);
		}

		initListAdapter();
		Log.i("errorReport", errorReport.toString());
	}

	private void workWithCardErrorModels(JSONArray scratchCardErrorModel) {
		Log.i("parse2", "doSomethingWithScratchCardErrorModel");

		for (int j = 0; j < scratchCardErrorModel.length(); j++) {
			try {

				doSomethingWithCardErrorModelItem(scratchCardErrorModel
						.getJSONObject(j));

			} catch (JSONException e) {
				errorReport.add(e);
			}

		}

	}

	private void doSomethingWithCardErrorModelItem(JSONObject jsonObject) {
		Log.i("parse2", "doSomethingWithScratchCardErrorModelItem");

		StringBuffer stringBuffer = new StringBuffer();

		String uid = "";
		try {
			uid = jsonObject.getString("UId");
			// stringBuffer.append(uid);
		} catch (JSONException e) {
			errorReport.add(e);
		}

		JSONArray Errors;
		try {
			Errors = jsonObject.getJSONArray("Errors");

			for (int i = 0; i < Errors.length(); i++) {
				JSONObject errorItemJSONObject;

				try {
					errorItemJSONObject = Errors.getJSONObject(i);
					Log.i("parse2", "errorItemJSONObject="
							+ errorItemJSONObject);

					JSONObject Error = errorItemJSONObject
							.getJSONObject("Error");

					final String ErrorCode = Error.getString("ErrorCode");

					final String Message = Error.getString("Message");

					stringBuffer.append("\n").append("ErrorCode: ")
							.append(ErrorCode).append(". ").append(Message);

					JSONObject ModelJSONObject = errorItemJSONObject
							.getJSONObject("Model");
					final String SerialNumber = ModelJSONObject
							.getString("SerialNumber");

					stringBuffer.append("\n").append("SerialNumber: ")
							.append(SerialNumber);

				} catch (JSONException e) {
					errorReport.add(e);
				}
			}

		} catch (JSONException e) {
			errorReport.add(e);
		}

		saveErrorMessageToSaleOrder(uid, stringBuffer.toString());

	}

	private void saveErrorMessageToSaleOrder(String uid, String string) {

		Log.i("parse2", "saveErrorMessageToSaleOrder:\n\n" + "UId=" + uid
				+ "\n" + string);

		SQLiteAdapter.open(this);
		final SaleOrder order = SQLiteAdapter.getSaleOrderByUId(uid, true);
		if (order != null) {
			order.errorMessage = string;
			SQLiteAdapter.createOrUpdateSaleOrder(order);
		}

	}

	private void workWithFullySavedOrders(JSONArray fullySavedOrders) {
		Log.i("parse2", "doSomethingWithFullySavedOrders");

		SQLiteAdapter.open(this);

		for (int i = 0; i < fullySavedOrders.length(); i++) {
			try {
				final String uid = fullySavedOrders.getString(i);
				final SaleOrder order = SQLiteAdapter.getSaleOrderByUId(uid,
						true);
				if (order != null) {
					order.OrderStatus = 2;
					order.errorMessage = "";
					SQLiteAdapter.createOrUpdateSaleOrder(order);
				}
			} catch (JSONException e) {
				Log.e("workWithFullySavedOrders", "" + e.getLocalizedMessage());
			}

		}

	}

}
