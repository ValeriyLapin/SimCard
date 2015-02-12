package com.example.simcard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simcard.db.Agent;
import com.example.simcard.db.Buyer;
import com.example.simcard.db.Card;
import com.example.simcard.db.Location;
import com.example.simcard.db.Nominal;
import com.example.simcard.db.SQLiteAdapter;
import com.example.simcard.db.SaleOrder;

public class SaleOderActivity extends MyActivityWithProgressBar {

	private boolean isVaucher;
	private EditText fromEditText;
	private EditText toEditText;
	private Spinner spinnerNominal;
	private Spinner spinnerAgent;
	private Spinner spinnerLocation;
	private Spinner spinnerBuyer;
	private CheckBox checkBoxDeleteLatestChars;
	private EditText editTextDeleteLatestChars;
	private ListView listOfCardsView;
	private ArrayList<Card> listOfCards;
	private ArrayList<Card> listOfSelectedCards;

	private ArrayAdapter<Card> adapterCardList;
	private TextView addedNumbersTextView;

	private TextView textViewDate;
	private EditText editTextDescription;
	private Button addButton;
	private Button delButton;
	private Button saveButton;
	private Button clearButton;
	private SaleOrder saleOrder;
	private Calendar calendar = Calendar.getInstance();
	private boolean isFirstLoadOfSpinnersForExistedSaleOrder = false;

	private TextView titleVaucherForm;
	private String UId;
	
	private ArrayAdapter<Agent> agentDataAdapter;
	private ArrayAdapter<Nominal> nominalsDataAdapter;
	private ArrayAdapter<Buyer> buyerDataAdapter;
	private ArrayAdapter<Location> locationDataAdapter;


	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_layout);

		getActionBar().hide();

		titleVaucherForm = (TextView) findViewById(R.id.titleFormTextView);
		fromEditText = (EditText) findViewById(R.id.fromEditText);
		toEditText = (EditText) findViewById(R.id.toEditText);
		listOfCardsView = (ListView) findViewById(R.id.listOfCards);
		listOfCards = new ArrayList<Card>();
		listOfSelectedCards = new ArrayList<Card>();

		textViewDate = (TextView) findViewById(R.id.textViewDate);
		editTextDescription = (EditText) findViewById(R.id.editTextDescription);
		addedNumbersTextView = (TextView) findViewById(R.id.addedNumbersTextView);

		clearButton = (Button) findViewById(R.id.clearButton);
		delButton = (Button) findViewById(R.id.delButton);
		addButton = (Button) findViewById(R.id.addButton);
		saveButton = (Button) findViewById(R.id.saveButton);

		spinnerNominal = (Spinner) findViewById(R.id.spinnerNominal);
		spinnerAgent = (Spinner) findViewById(R.id.spinnerAgent);
		spinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);
		spinnerBuyer = (Spinner) findViewById(R.id.spinnerBuyer);

		editTextDeleteLatestChars = (EditText) findViewById(R.id.editTextDeleteLatestChars);
		checkBoxDeleteLatestChars = (CheckBox) findViewById(R.id.checkBoxDeleteLatestChars);

		final Intent intent = this.getIntent();
		final Bundle extras = intent.getExtras();

		int indexAgentsSpinnerData = -1;
		int indexBuyersSpinnerData = -1;
		int indexLocationsSpinnerData = -1;
		int indexNominalsSpinnerData = -1;

		isVaucher = extras.getBoolean("isVaucher", true);

		UId = extras.getString("UId", "");
		if (UId.isEmpty()) {
			Log.i("openALL","create new saleOrder");
			createNewSaleOrderOnStart();
		} else {
			Log.i("openALL","open existed saleOrder");
			openExistingSaleOrderForEdit();
			isFirstLoadOfSpinnersForExistedSaleOrder = true;
		}

		final LinearLayout nominalLayout = (LinearLayout) findViewById(R.id.nominalLayout);

		final OnClickListener dateOnClickListener = new OnClickListener() {

			@Override
	        public void onClick(View v) {
	            new DatePickerDialog(SaleOderActivity.this, date, calendar
	                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
	                    calendar.get(Calendar.DAY_OF_MONTH)).show();
	        }
		};
		
		
		
		
		textViewDate.setOnClickListener(dateOnClickListener);

		((ImageView) findViewById(R.id.calendarIconImageView))
				.setOnClickListener(dateOnClickListener);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saleOrder.OrderStatus = 1;
				saleOrder.OrderType = (isVaucher) ? 2 : 1;

				saleOrder.dt = calendar.getTime();
				saleOrder.agent = (Agent) spinnerAgent.getSelectedItem();
				saleOrder.buyer = (Buyer) spinnerBuyer.getSelectedItem();
				saleOrder.location = (Location) spinnerLocation
						.getSelectedItem();
				saleOrder.Description = editTextDescription.getText()
						.toString();
				saleOrder.listOfCards = listOfCards;
				saleOrder.errorMessage = "";
				SQLiteAdapter.open(MyApplication.context);
				final long result = SQLiteAdapter
						.createOrUpdateSaleOrder(saleOrder);
				if (result == -1) {
					setResult(RESULT_CANCELED, null);
				}
				setResult(RESULT_OK, null);
				finish();
			}
		});

		clearButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				listOfCards = new ArrayList<Card>();
				addedNumbersTextView.setText(String.valueOf(listOfCards.size()));

				initListAdapter();
				changeCheckEnablityOfButtons();
			}
		});

		delButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				for (Card selectedCard : listOfSelectedCards) {
					listOfCards.remove(selectedCard);
				}
				listOfSelectedCards = new ArrayList<Card>();
				adapterCardList.notifyDataSetChanged();
				addedNumbersTextView.setText(String.valueOf(listOfCards.size()));
				delButton.setActivated(false);
				changeCheckEnablityOfButtons();
			}
		});

		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isVaucher) {
					addVaucher();

				} else {
					addPackage();
				}

			}
		});

		if (isVaucher) {

			if (UId.isEmpty()) {
				titleVaucherForm.setText(getString(R.string.titleVaucherForm));
			} else {
				titleVaucherForm
						.setText(getString(R.string.titleVaucherFormEdit));
			}

			nominalLayout.setVisibility(View.VISIBLE);
			toEditText.setVisibility(View.VISIBLE);
			toEditText
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {

							if (actionId == EditorInfo.IME_ACTION_DONE) {

								addVaucher();

								return true;
							}
							return false;
						}

					});

		} else {
			if (UId.isEmpty()) {
				titleVaucherForm.setText(getString(R.string.titlePackageForm));
			} else {
				titleVaucherForm
						.setText(getString(R.string.titlePackageFormEdit));
			}

			setTitle(getString(R.string.titlePackageForm));
			nominalLayout.setVisibility(View.GONE);
			toEditText.setVisibility(View.GONE);

			fromEditText
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE) {

								addPackage();

								return true;
							}
							return false;
						}

					});

		}

		showDate();
		SQLiteAdapter.open(this);

		if (savedInstanceState != null) {
			Log.i("SaveInstanceState", "Load InstanceState");

			listOfCards = ((ArrayList<Card>) savedInstanceState
					.getSerializable("listOfCards"));
			indexAgentsSpinnerData = savedInstanceState
					.getInt("indexAgentsSpinnerData");
			indexBuyersSpinnerData = savedInstanceState
					.getInt("indexBuyersSpinnerData");
			indexLocationsSpinnerData = savedInstanceState
					.getInt("indexLocationsSpinnerData");
			indexNominalsSpinnerData = savedInstanceState
					.getInt("indexNominalsSpinnerData");

			calendar = (Calendar) savedInstanceState
					.getSerializable("calendar");

			editTextDescription.setText(savedInstanceState
					.getString("editTextDescription"));
			checkBoxDeleteLatestChars.setChecked(savedInstanceState
					.getBoolean("checkBoxDeleteLatestChars"));
			editTextDeleteLatestChars.setText(savedInstanceState
					.getString("editTextDeleteLatestChars"));
			addedNumbersTextView.setText(savedInstanceState
					.getString("addedNumbersTextView"));
			fromEditText.setText(savedInstanceState.getString("fromEditText"));
			toEditText.setText(savedInstanceState.getString("toEditText"));
			listOfSelectedCards = (ArrayList<Card>) savedInstanceState
					.getSerializable("listOfSelectedCards");

		}

		if (isFirstLoadOfSpinnersForExistedSaleOrder) {

			settingSpinnerPositionBySaleOrder();
			setAllSpinnerOnItemSelectListeners(1);

			Log.i("loadTest", "----------------------------------");
			isFirstLoadOfSpinnersForExistedSaleOrder = false;
		} else {
			loadAgentsSpinnerData(indexAgentsSpinnerData);
			loadBuyersSpinnerData(indexBuyersSpinnerData);
			loadLocationsSpinnerData(indexLocationsSpinnerData);
			loadNominalsSpinnerData(indexNominalsSpinnerData);
			setAllSpinnerOnItemSelectListeners(0);
		}

		checkBoxDeleteLatestChars
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							checkBoxDeleteLatestChars
									.setText(getString(R.string.deleteLatestCharsString2));

							editTextDeleteLatestChars
									.setVisibility(View.VISIBLE);
							editTextDeleteLatestChars.setText("");
							editTextDeleteLatestChars.requestFocus();
						} else {
							checkBoxDeleteLatestChars
									.setText(getString(R.string.deleteLatestCharsString));
							editTextDeleteLatestChars
									.setVisibility(View.INVISIBLE);
						}

					}
				});

		setEnablityOfDeleteListItemsButton();
		changeCheckEnablityOfButtons();
		showDate();

		initListAdapter();

		final OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isAllSpinnersFillingOut()) {

					showToastErrorMessage(MyApplication.context.getResources()
							.getString(R.string.noAllFieldsPopulated));
				}
			}

		};

		toEditText.setOnClickListener(onClickListener);
		fromEditText.setOnClickListener(onClickListener);

		changeCheckEnablityOfButtons();

	}

	@Override
	protected void onStart() {
		Log.i("loadTest", "onStart------");
		super.onStart();
	}

	private void settingSpinnerPositionBySaleOrder() {

		loadAgentsSpinnerData(-1);

		for (int new_position = 0; new_position < agentDataAdapter.getCount(); new_position++) {
			final Agent agentObject = (Agent) spinnerAgent
					.getItemAtPosition(new_position);
			if (agentObject.value == saleOrder.agent.value) {
				Log.i("loadTest",
						" settingSpinnerPositionBySaleOrder agent new pos="
								+ new_position);
				spinnerAgent.setSelection(new_position);
				Log.i("loadTest",
						"settingSpinnerPositionBySaleOrder: chesk agent new pos="
								+ spinnerAgent.getSelectedItemPosition());
				break;
			}
		}
		loadBuyersSpinnerData(-1);

		for (int new_position = 0; new_position < buyerDataAdapter.getCount(); new_position++) {
			final Buyer buyerObject = (Buyer) spinnerBuyer
					.getItemAtPosition(new_position);
			Log.i("settingSpinnerPositionBySaleOrder", "buyerObject.id="
					+ buyerObject.id + ", saleOrder.buyer.id="
					+ saleOrder.buyer.id);
			if (buyerObject.id == saleOrder.buyer.id) {
				Log.i("settingSpinnerPositionBySaleOrder", "buyer new pos="
						+ new_position);
				spinnerBuyer.setSelection(new_position);
				Log.i("settingSpinnerPositionBySaleOrder",
						"chesk buyer new pos="
								+ spinnerBuyer.getSelectedItemPosition());
				break;
			}
		}
		loadLocationsSpinnerData(-1);

		for (int new_position = 0; new_position < locationDataAdapter
				.getCount(); new_position++) {
			final Location locationObject = (Location) spinnerLocation
					.getItemAtPosition(new_position);
			if (locationObject.id == saleOrder.location.id) {
				Log.i("settingSpinnerPositionBySaleOrder", "location new pos="
						+ new_position);
				spinnerLocation.setSelection(new_position);
				Log.i("settingSpinnerPositionBySaleOrder",
						"chesk buyer new pos="
								+ spinnerLocation.getSelectedItemPosition());
				break;
			}
		}

		loadNominalsSpinnerData(-1);

		for (int new_position = 0; new_position < nominalsDataAdapter
				.getCount(); new_position++) {
			final Nominal nominalObject = (Nominal) spinnerNominal
					.getItemAtPosition(new_position);
			if (nominalObject.id == saleOrder.listOfCards.get(0).getNominalId()) {
				Log.i("settingSpinnerPositionBySaleOrder", "location new pos="
						+ new_position);
				spinnerNominal.setSelection(new_position);
				Log.i("settingSpinnerPositionBySaleOrder",
						"chesk nominal new pos="
								+ spinnerNominal.getSelectedItemPosition());
				break;
			}
		}

	}

	private void setAllSpinnerOnItemSelectListeners(final int startAfter) {
		spinnerAgent.setOnItemSelectedListener(new OnItemSelectedListener() {
			int startAfter1 = startAfter;

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				Log.i("loadTest",
						" spinnerAgent.setOnItemSelectedListener position="
								+ position + ", startAfter1=" + startAfter1);
				if (startAfter1 > 0) {
					startAfter1--;
				} else {
					loadBuyersSpinnerData(-1);
					changeCheckEnablityOfButtons();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}

		});

		spinnerBuyer.setOnItemSelectedListener(new OnItemSelectedListener() {
			int startAfter1 = startAfter;

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				Log.i("loadTest",
						"spinnerBuyer.setOnItemSelectedListener, position="
								+ position + ", startAfter1=" + startAfter1);

				if (startAfter1 > 0) {
					startAfter1--;
				} else {
					loadLocationsSpinnerData(-1);
					changeCheckEnablityOfButtons();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}

		});

		spinnerLocation.setOnItemSelectedListener(new OnItemSelectedListener() {
			int startAfter1 = startAfter;

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				Log.i("loadTest",
						"spinnerLocation.setOnItemSelectedListener, position="
								+ position + ", startAfter1=" + startAfter1);

				changeCheckEnablityOfButtons();

				if (spinnerLocation.getSelectedItemPosition() > 0 && !isVaucher) {
					fromEditText.requestFocus();

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}

		});

		if (isVaucher) {
			Log.i("fromEditText", "set setOnEditorActionListener");

			fromEditText
					.setOnEditorActionListener(new EditText.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((actionId == EditorInfo.IME_ACTION_DONE)
									|| (event != null
											&& event.getAction() == KeyEvent.ACTION_DOWN && event
											.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
								Log.i("focus",
										"fromEditText - IME_ACTION_DONE: remove focuse to toEditText");
								toEditText.requestFocus();
								return !(actionId == EditorInfo.IME_ACTION_DONE);
							}
							return false;
						}
					});

			toEditText
					.setOnEditorActionListener(new EditText.OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((actionId == EditorInfo.IME_ACTION_DONE)
									|| (event != null
											&& event.getAction() == KeyEvent.ACTION_DOWN && event
											.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

								if (fromEditText.getText().toString().isEmpty()
										&& toEditText.getText().toString()
												.isEmpty()) {
									if (listOfCards.size() > 0) {

										saveButton.requestFocus();
										Log.i("focus",
												"toEditText - IME_ACTION_DONE: remove focuse to saveButton");

									} else {
										fromEditText.requestFocus();
										Log.i("focus",
												"toEditText - IME_ACTION_DONE: remove focuse to fromEditText");
									}
								} else {

									addVaucher();

									Log.i("focus",
											"toEditText - IME_ACTION_DONE: addVaucher");
								}
								return !(actionId == EditorInfo.IME_ACTION_DONE);
							}
							return false;
						}
					});

		} else {
			fromEditText
					.setOnEditorActionListener(new EditText.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {

							if ((actionId == EditorInfo.IME_ACTION_DONE)
									|| (event != null
											&& event.getAction() == KeyEvent.ACTION_DOWN && event
											.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
								if (fromEditText.getText().toString().isEmpty()) {
									if (listOfCards.size() > 0) {
										Log.i("focus",
												"remove focuse to saveButton");
										saveButton.requestFocus();
									}
								} else {

									addPackage();

									Log.i("focus", "addPackege ");
								}
								return !(actionId == EditorInfo.IME_ACTION_DONE);
							}
							return false;
						}
					});
		}

		spinnerNominal.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				changeCheckEnablityOfButtons();
				if (spinnerNominal.getSelectedItemPosition() > 0) {
					fromEditText.requestFocus();

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void loadAgentsSpinnerData(int index) {
		Log.i("loadTest", "loadAgentsSpinnerData index=" + index);
		final List<Agent> agents = SQLiteAdapter.getAllAgents();

		final Agent agent = new Agent();
		Log.i("test", "size=" + agents.size());
		if (agents.size() == 0) {
			agent.text = getString(R.string.no_agentSpinnerHintString);
			spinnerAgent.setClickable(false);
		} else {
			agent.text = getString(R.string.agentSpinnerHintString);
			spinnerAgent.setClickable(true);
		}
		agents.add(0, agent);

		agentDataAdapter = new ArrayAdapter<Agent>(this,
				android.R.layout.simple_spinner_item, agents);

		agentDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerAgent.setAdapter(agentDataAdapter);

		if (index == -1) {
			for (int i = 0; i < agentDataAdapter.getCount(); i++) {
				if (((Agent) agents.get(i)).isDefault) {
					Log.i("test",
							"set selection on Agent: "
									+ agentDataAdapter.getItemId(i));
					spinnerAgent.setSelection(i);
				}
			}
		} else {
			try {
				spinnerAgent.setSelection(index);
			} catch (Exception e) {
				Log.e("loadAgentsSpinnerData", e.toString());
			}
		}

	}

	private void loadBuyersSpinnerData(final int index) {
		Log.i("loadTest", "loadBuyersSpinnerData index=" + index);
		List<Buyer> buyers;
		final Buyer buyer = new Buyer();
		if (spinnerAgent.getSelectedItemPosition() == -1) {
			buyers = new ArrayList<Buyer>();
			buyer.name = getString(R.string.no_buyerSpinnerHintString);
			spinnerBuyer.setClickable(false);
		} else {
			buyers = SQLiteAdapter.getAgentBuyers(((Agent) spinnerAgent
					.getSelectedItem()).value);
			if (buyers.size() == 0) {
				buyer.name = getString(R.string.no_buyerSpinnerHintString);
				spinnerBuyer.setClickable(false);
			} else {
				buyer.name = getString(R.string.buyerSpinnerHintString);
				spinnerBuyer.setClickable(true);
			}
		}

		buyers.add(0, buyer);
		buyerDataAdapter = new ArrayAdapter<Buyer>(this,
				android.R.layout.simple_spinner_item, buyers);

		buyerDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerBuyer.setAdapter(buyerDataAdapter);

		Log.i("loadBuyersSpinnerData",
				"old index=" + spinnerBuyer.getSelectedItemPosition());

		if (index != -1) {
			try {
				spinnerBuyer.setSelection(index);
				Log.i("loadBuyersSpinnerData", "set selection on index="
						+ index + " of " + buyers.size());

			} catch (Exception e) {
				Log.e("loadBuyersSpinnerData", e.toString());
			}

		}

	}

	private void loadLocationsSpinnerData(int index) {
		Log.i("loadTest", "loadLocationsSpinnerData index=" + index);

		List<Location> locations = new ArrayList<Location>();

		final Location location = new Location();

		if (spinnerBuyer.getSelectedItemPosition() == -1) {
			location.name = getString(R.string.no_locationSpinneHintString);
			spinnerLocation.setClickable(false);
		} else {

			Log.i("testLocation",
					"posBuyer="
							+ spinnerBuyer.getSelectedItemPosition()
							+ ", "
							+ ((Buyer) spinnerBuyer.getSelectedItem())
									.toStringLong());
			Log.i("testLocation",
					"posAgent="
							+ spinnerAgent.getSelectedItemPosition()
							+ ", "
							+ ((Agent) spinnerAgent.getSelectedItem())
									.toStringLong());

			if (spinnerBuyer.getSelectedItemPosition() > 0
					&& spinnerAgent.getSelectedItemPosition() > 0) {
				Log.i("testLocation", "getBuyersLocation");

				locations = SQLiteAdapter.getBuyersLocation(
						((Buyer) spinnerBuyer.getSelectedItem()).id,
						((Agent) spinnerAgent.getSelectedItem()).value);
			}
			Log.i("testLocation", "loadLocationsSpinnerData, " + locations);

			if (locations.size() == 0) {
				location.name = getString(R.string.no_locationSpinneHintString);
				spinnerLocation.setClickable(false);
			} else {
				location.name = getString(R.string.locationSpinneHintString);
				spinnerLocation.setClickable(true);
			}
		}

		locations.add(0, location);

		// Creating adapter for spinner
		locationDataAdapter = new ArrayAdapter<Location>(this,
				android.R.layout.simple_spinner_item, locations);

		locationDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerLocation.setAdapter(locationDataAdapter);

		if (index != -1) {
			try {
				spinnerLocation.setSelection(index);
			} catch (Exception e) {
				Log.e("loadLocationsSpinnerData", e.toString());
			}
		}

	}

	private void loadNominalsSpinnerData(int index) {
		Log.i("loadTest", "loadNominalsSpinnerData index=" + index);

		final List<Nominal> nominals = SQLiteAdapter.getAllNominals();
		final Nominal nominal = new Nominal();

		if (nominals.size() == 0) {
			nominal.name = getString(R.string.no_nominalSpinnerHintString);
			spinnerNominal.setClickable(false);
		} else {
			nominal.name = getString(R.string.nominalSpinnerHintString);
			spinnerNominal.setClickable(true);
		}
		nominals.add(0, nominal);

		// Creating adapter for spinner
		nominalsDataAdapter = new ArrayAdapter<Nominal>(this,
				android.R.layout.simple_spinner_item, nominals);

		nominalsDataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerNominal.setAdapter(nominalsDataAdapter);

		if (index != -1) {
			try {
				spinnerNominal.setSelection(index);
			} catch (Exception e) {
				Log.e("loadNominalsSpinnerData", e.toString());
			}
		}

	}

	private void openExistingSaleOrderForEdit() {
		SQLiteAdapter.open(this);
		saleOrder = SQLiteAdapter.getSaleOrderByUId(UId, true);
		Log.i("openALL", "open for editing: \n" + saleOrder.toString());
		calendar.setTime(saleOrder.dt);
		if (saleOrder == null) {
			saleOrder = new SaleOrder();
		}
		if (saleOrder.Description != null) {
			editTextDescription.setText(saleOrder.Description);
		}

		listOfCards = (ArrayList<Card>) saleOrder.listOfCards;

		Log.i("test", "" + addedNumbersTextView);
		addedNumbersTextView.setText(String.valueOf(listOfCards.size()));
		changeCheckEnablityOfButtons();
	}

	private void createNewSaleOrderOnStart() {
		
		saleOrder = new SaleOrder();
		if (isVaucher) {
			saleOrder.OrderType = 2;
		} else {
			saleOrder.OrderType = 1;
		}
		calendar.setTimeInMillis(System.currentTimeMillis());
		Log.i("createNewSaleOrderOnStart",(new Date(calendar.getTimeInMillis())).toString());
	}


	private void showToastErrorMessage(final String message) {
		final Toast toast = Toast.makeText(MyApplication.context, message,
				Toast.LENGTH_LONG);
		toast.getView().setBackgroundColor(
				MyApplication.context.getResources().getColor(
						R.color.toast_color_red));
		toast.show();
	}

	private boolean isAllSpinnersFillingOut() {
		final boolean res = (spinnerNominal.getSelectedItemPosition() > 0 || !isVaucher)
				&& spinnerAgent.getSelectedItemPosition() > 0
				&& spinnerBuyer.getSelectedItemPosition() > 0
				&& spinnerLocation.getSelectedItemPosition() > 0;
		Log.i("loadTest", "isAllSpinnersFillingOut, res=" + res);
		return res;
	}

	private void changeCheckEnablityOfButtons() {
		Log.i("loadTest", "onSpinnerItemChangeCheckEnablityOfButtons");
		if (isAllSpinnersFillingOut()) {
			Log.i("loadTest", "turn buttons to Enabled");

			toEditText.setFocusable(true);
			fromEditText.setFocusable(true);

			toEditText.setFocusableInTouchMode(true);
			fromEditText.setFocusableInTouchMode(true);

			if (listOfCards.size() > 0) {
				saveButton.setEnabled(true);
			} else {
				saveButton.setEnabled(false);
			}

		} else {
			Log.i("loadTest", "turn buttons to Disabled");
			toEditText.setFocusable(false);
			fromEditText.setFocusable(false);

			toEditText.setFocusableInTouchMode(false);
			fromEditText.setFocusableInTouchMode(false);
			saveButton.setEnabled(false);
		}


		if (listOfCards.size() > 0) {
			clearButton.setEnabled(true);
		} else {
			clearButton.setEnabled(false);
		}

	}

	private void addPackage() {
		final String string = cutNumber(fromEditText.getText().toString());
		Log.i("addPocket", "fromEditText.getText().toString()="
				+ fromEditText.getText().toString() + ", string=" + string);
		if (string.isEmpty()) {
			tryToAddOneCard(fromEditText.getText().toString());
		} else {
			tryToAddOneCard(string);
		}

	}

	private int getIntFromString(final String string) {
		int numberFrom = 0;
		if (!string.isEmpty()) {
			try {
				numberFrom = Integer.parseInt(string);
			} catch (Exception e) {
				Log.e("onEditorAction", e.toString());
			}
		}
		return numberFrom;
	}

	private void addVaucher() {

		final String numberFrom = cutNumber(fromEditText.getText().toString()); 
		final String numberTo = cutNumber(toEditText.getText().toString()); 
		
		// all fields are empty
		if (numberFrom.isEmpty() && numberTo.isEmpty())
			return;

		// all fields are not empty
		if (!numberFrom.isEmpty() && !numberTo.isEmpty()) {
			if (numberFrom.equals(numberTo)) {
				tryToAddOneCard(numberFrom);
			} else {
				addIntervalOfVauchers(numberFrom, numberTo);
			}
		} else if (!numberTo.isEmpty()) {
			tryToAddOneCard(numberTo);
		} else if (!numberFrom.isEmpty()) {
			tryToAddOneCard(numberFrom);
		}

	}

	private void addIntervalOfVauchers(String numberFrom, String numberTo) {

		if (numberFrom.length() <= 7 && numberFrom.length() <= 7) {
			addIntervalOfVauchers7didgits(getIntFromString(numberFrom),
					getIntFromString(numberTo), "");
		} else {
			Log.i("addVaucher",
					"-----------------------------------\nlenght > 7 didgits");
			if (numberFrom.length() != numberFrom.length()) {
				showToastErrorMessage(MyApplication.context.getResources()
						.getString(R.string.error_invalidSerialNumber));
			} else {
				addIntervalOfVauchersMoreThat7didgits(numberFrom, numberTo);
			}
		}
	}

	private void addIntervalOfVauchersMoreThat7didgits(String numberFrom,
			String numberTo) {
		final int firstPartLength = numberFrom.length() - 7;
//		final String firstPartStringFrom = numberFrom.substring(0,
//				firstPartLength - 1);
//		final String firstPartStringTo = numberTo.substring(0,
//				firstPartLength - 1);
		
		
		final String firstPartStringFrom = numberFrom.substring(0,
				firstPartLength);
		final String firstPartStringTo = numberTo.substring(0,
				firstPartLength);

		if (!firstPartStringTo.equals(firstPartStringFrom)) {

			showToastErrorMessage(MyApplication.context.getResources()
					.getString(R.string.error_from_less_to));

		} else {

			final String endPartStringTo = numberTo.substring(firstPartLength,
					numberTo.length());
			final String endPartStringFrom = numberFrom.substring(
					firstPartLength, numberFrom.length());

			addIntervalOfVauchers7didgits(getIntFromString(endPartStringFrom),
					getIntFromString(endPartStringTo), firstPartStringTo);
		}
	}

	private void addIntervalOfVauchers7didgits(int intNumberFrom,
			int intNumberTo, String firstPartString) {

		if (intNumberTo >= intNumberFrom) {
			tryToAddIntervalOfCardsToList(intNumberFrom, intNumberTo,
					firstPartString);
		} else {
			showToastErrorMessage(MyApplication.context.getResources()
					.getString(R.string.error_from_less_to));
		}

	}

	private void tryToAddOneCard(final String number) {
		Log.i("tryToAddOneCard", "number=");

		boolean isExist = false;
		for (Card card : listOfCards) {
			if (card.Sn.equals(number)) {
				isExist = true;
				break;
			}
		}

		if (isExist) {
			showToastErrorMessage(MyApplication.context.getResources()
					.getString(R.string.errorSerialNumberIsExist));
		} else {
			addOneCardToList(number);
			fromEditText.requestFocus();
		}
	}

	private void addOneCardToList(String number) {
		Card card;
		if (isVaucher) {
			final Nominal nominal = (Nominal) spinnerNominal.getSelectedItem();
			card = new Card(number, nominal);
		} else {
			card = new Card(number, null);
		}

		listOfCards.add(card);
		changeCheckEnablityOfButtons();
		initListAdapter();

		addedNumbersTextView.setText(String.valueOf(listOfCards.size()));
		toEditText.setText("");
		fromEditText.setText("");
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initListAdapter() {
		Log.i("initListAdapter", listOfCards.toString());

		adapterCardList = new ArrayAdapter<Card>(this,
				R.layout.my_simple_list_item_1, listOfCards) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				convertView = super.getView(position, convertView, parent);
				final Card card = adapterCardList.getItem(position);
				if (card.isSelected) {
					convertView.setBackgroundColor(MyApplication.context
							.getResources().getColor(R.color.holo_blue_light));
				} else {
					convertView.setBackgroundColor(Color.TRANSPARENT);

				}
				return convertView;
			}
		};
		listOfCardsView.setAdapter(adapterCardList);
		listOfCardsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listOfCardsView.setOnTouchListener(new OnTouchListener() {
			// Setting on Touch Listener for handling the touch inside
			// ScrollView
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Disallow the touch request for parent scroll on touch of
				// child view
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		listOfCardsView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1,
					int position, long id) {
				final Card card = adapterCardList.getItem(position);

				if (card.isSelected) {
					listOfSelectedCards.remove(card);
				} else {
					listOfSelectedCards.add(card);
				}
				card.isSelected = !card.isSelected;
				adapterCardList.notifyDataSetChanged();

				setEnablityOfDeleteListItemsButton();
			}
		});

	}

	protected void setEnablityOfDeleteListItemsButton() {
		Log.i("setActivityOfDeleteListItemsButton", "checkedListItems="
				+ listOfSelectedCards.size());
		if (listOfSelectedCards.size() > 0) {
			delButton.setEnabled(true);
		} else {
			delButton.setEnabled(false);
		}
	}

	private void tryToAddIntervalOfCardsToList(final int numberFrom,
			int numberTo, final String addToStart) {

		int interval = numberTo - numberFrom;

		if (interval > MyApplication.MAX_VAUCHER_INTERVAL_LENGTH - 1) {
			showToastErrorMessage(MyApplication.context.getResources()
					.getString(R.string.error_max_interval)
					+ " "
					+ String.valueOf(MyApplication.MAX_VAUCHER_INTERVAL_LENGTH));
			return;
		}

		boolean isExist = false;
		String sn7;
		for (Card card : listOfCards) {
			sn7 = new String(card.Sn);
			if (sn7.length() > 7) {

				final int firstPartLength = sn7.length() - 7;
				sn7 = sn7.substring(firstPartLength, sn7.length());
			}
			final int intSn = Integer.parseInt(sn7);

			if (!(intSn < numberFrom || intSn > numberTo)) {
				isExist = true;
				break;
			}
		}

		if (isExist) {
			showToastErrorMessage(MyApplication.context.getResources()
					.getString(R.string.errorSerialNumberIsExist));
		} else {
			addIntervalOfCardsToList(numberFrom, numberTo, addToStart);
			fromEditText.requestFocus();
		}

	}

	private void addIntervalOfCardsToList(final int numberFrom, int numberTo,
			final String addToStart) {
		Log.i("addIntervalOfCardsToList", "addToStart=" + addToStart
				+ ", numberFrom=" + numberFrom + ", numberTo=" + numberTo);

		final Nominal nominal = (Nominal) spinnerNominal.getSelectedItem();
		Card card;
		for (int i = numberFrom; i <= numberTo; i++) {
			card = new Card(addToStart + String.valueOf(i), nominal);
			listOfCards.add(card);
		}
		changeCheckEnablityOfButtons();
		initListAdapter();
		addedNumbersTextView.setText(String.valueOf(listOfCards.size()));
		toEditText.setText("");
		fromEditText.setText("");
	}

	private String cutNumber(final String number) {
		String res = number;

		if (checkBoxDeleteLatestChars.isChecked()) {
			final int cutDidgits = getIntFromEditTextDeleteLatestChars();

			if (res.length() > cutDidgits) {
				res = res.substring(0, res.length() - cutDidgits);
			}
		}
		return res;
	}

	private int getIntFromEditTextDeleteLatestChars() {
		int res = 0;
		try {
			res = Integer.parseInt(editTextDeleteLatestChars.getText()
					.toString());
		} catch (Exception e) {
			Log.e("getIntFromEditTextDeleteLatestChars", e.toString());
		}
		return res;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.i("SaveInstanceState", "onSaveInstanceState");
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putSerializable("listOfCards", listOfCards);

		savedInstanceState.putInt("indexAgentsSpinnerData",
				spinnerAgent.getSelectedItemPosition());
		savedInstanceState.putInt("indexBuyersSpinnerData",
				spinnerBuyer.getSelectedItemPosition());
		savedInstanceState.putInt("indexLocationsSpinnerData",
				spinnerLocation.getSelectedItemPosition());
		savedInstanceState.putInt("indexNominalsSpinnerData",
				spinnerNominal.getSelectedItemPosition());

		savedInstanceState.putString("textViewDate", textViewDate.getText()
				.toString());
		savedInstanceState.putString("editTextDescription", editTextDescription
				.getText().toString());
		savedInstanceState.putBoolean("checkBoxDeleteLatestChars",
				checkBoxDeleteLatestChars.isSelected());
		savedInstanceState.putString("editTextDeleteLatestChars",
				editTextDeleteLatestChars.getText().toString());
		savedInstanceState.putString("addedNumbersTextView",
				addedNumbersTextView.getText().toString());
		savedInstanceState.putString("fromEditText", fromEditText.getText()
				.toString());
		savedInstanceState.putString("toEditText", toEditText.getText()
				.toString());

		savedInstanceState.putSerializable("listOfSelectedCards",
				listOfSelectedCards);

		savedInstanceState.putSerializable("calendar", calendar);

	}



	@SuppressLint("SimpleDateFormat")
	private void showDate() {
		final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		final Date today = calendar.getTime();
		final String reportDate = df.format(today);
		textViewDate.setText(reportDate);
	}
	
	
	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

	    @Override
	    public void onDateSet(DatePicker view, int year, int monthOfYear,
	            int dayOfMonth) {
	    	calendar.set(Calendar.YEAR, year);
	    	calendar.set(Calendar.MONTH, monthOfYear);
	    	calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	        showDate();
	    }

	};

}
