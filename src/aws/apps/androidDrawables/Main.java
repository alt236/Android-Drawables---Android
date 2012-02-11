package aws.apps.androidDrawables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import aws.apps.androidDrawables.R.color;
import aws.apps.androidDrawables.reflection.ResourceReflector;
import aws.apps.androidDrawables.util.UsefulBits;

public class Main extends Activity {
	private final String TAG =  this.getClass().getName();

	private UsefulBits uB;
	private ListView myList;
	private Button btnBlack;
	private Button btnWhite;
	private Button btnGreen;
	private Button btnOrange;
	private Button btnGray;
	private TextView tvTitleItems;
	private TextView tvValueItems;
	private TextView tvOS; 
	private LinearLayout buttonLayout;
	private Spinner spinnerLocation;
	private Spinner spinnerResources;
	private ResourceReflector myReflector;
	private int currentBgColour;

	private final Hashtable<CharSequence, String> locationString2Type = new Hashtable<CharSequence, String>();

	private View.OnClickListener colorButtonListener;

	private void buildUi(){
		tvOS = (TextView) findViewById(R.id.tvOS);

		tvTitleItems = (TextView) findViewById(R.id.titleItems);
		tvValueItems = (TextView) findViewById(R.id.valueItems);

		btnBlack = (Button) findViewById(R.id.main_black);
		btnWhite = (Button) findViewById(R.id.main_white);
		btnGreen = (Button) findViewById(R.id.main_green);
		btnOrange = (Button) findViewById(R.id.main_orange);
		btnGray = (Button) findViewById(R.id.main_gray);

		spinnerResources = (Spinner) findViewById(R.id.spinnerResource);

		spinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);
		ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(
				this, R.array.resource_location_description_array, 
				android.R.layout.simple_spinner_item);
		adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerLocation.setAdapter(adapterLocation);

		buttonLayout = (LinearLayout) findViewById(R.id.main_colour_buttons);

		populateResourceSpinner(getString(R.string.resource_class_public));
	}

	private String getTitle(String subClass){
		// Epically English only function.
		String subClassArray[];
		String title = "";
		subClassArray = subClass.split("\\.");

		title = subClassArray[subClassArray.length -1];

		if(title.length()>0){
			title = title.substring(0,1).toUpperCase() + title.substring(1); 

			if(!(title.endsWith("s"))){
				title +=  "s";
			}

			title += ":";
		}

		return title;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		uB = new UsefulBits(this);

		final Object data = getLastNonConfigurationInstance();

		if (data==null){
			myList = (ListView) findViewById(R.id.main_list);
			currentBgColour = getResources().getColor(color.white);
			myList.setBackgroundColor(currentBgColour);

		} else{
			if(myList==null){myList = (ListView) findViewById(R.id.main_list);}
			currentBgColour = (Integer) data;
			myList.setBackgroundColor(currentBgColour);
		}
		myList.setEmptyView(findViewById(R.id.empty));
		myReflector = new ResourceReflector(myList, Main.this);

		buildUi();

		locationString2Type.put(getString(R.string.resources_internal), getString(R.string.resource_class_internal));
		locationString2Type.put(getString(R.string.resources_public), getString(R.string.resource_class_public));

		colorButtonListener =  new View.OnClickListener() {
			public void onClick(View v) {
				Button b = (Button) v;
				// You need to set android:cacheColorHint="#00000000" in the 
				// list xml to make the list background stick.
				currentBgColour = Color.parseColor(b.getTag().toString());
				myList.setBackgroundColor(currentBgColour);
			}
		};

		myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				resourceListOnClick(v, pos, id);
			}
		});

		spinnerLocation.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String location = locationString2Type.get(spinnerLocation.getSelectedItem().toString());
				populateResourceSpinner(location);

				//				populateList(
				//						location,
				//						spinnerResources.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//
			}   	
		});

		spinnerResources.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				populateList(
						locationString2Type.get(spinnerLocation.getSelectedItem().toString()),
						spinnerResources.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//
			}   	
		});

		btnBlack.setOnClickListener(colorButtonListener);
		btnWhite.setOnClickListener(colorButtonListener);
		btnGreen.setOnClickListener(colorButtonListener);
		btnOrange.setOnClickListener(colorButtonListener);
		btnGray.setOnClickListener(colorButtonListener);

		myList.setFastScrollEnabled(true);

		tvOS.setText(Build.VERSION.RELEASE);
	}

	/** Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_BUTTONS.ABOUT.ordinal(), 0,
				getString(R.string.label_menu_about)).setIcon(android.R.drawable.ic_menu_info_details);
		return true;
	}

	/** Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (MENU_BUTTONS.lookUpByOrdinal(item.getItemId())) {
		case ABOUT:
			uB.showAboutDialogue();
			return true;
		}
		return false;
	}


	@Override
	public Object onRetainNonConfigurationInstance() {
		return currentBgColour;
	}

	private void populateList(String baseClass, String subClass) {
		int res = 0;

		Log.i(TAG, "^ Populating list for '" + baseClass + "' / '" + subClass + "'");
		myList.setAdapter(null);

		if(subClass == null || subClass.length() <=0){return;}

		tvTitleItems.setText(getTitle(subClass));

		if(subClass.endsWith(".drawable")){
			Log.i(TAG, "^ Populating list with drawables");
			buttonLayout.setVisibility(View.VISIBLE);
			res = myReflector.getResourceDrawables(
					baseClass, 
					subClass);
		}
		else if(subClass.endsWith(".bool")){
			Log.i(TAG, "^ Populating list with boolean");
			buttonLayout.setVisibility(View.VISIBLE);
			res = myReflector.getResourceBoolean(
					baseClass, 
					subClass);
		}
		else if(subClass.endsWith(".color")){
			Log.i(TAG, "^ Populating list with colours");
			buttonLayout.setVisibility(View.VISIBLE);
			res = myReflector.getResourceColors(
					baseClass, 
					subClass);
		}
		else if(subClass.endsWith(".integer")){
			Log.i(TAG, "^ Populating list with integers");
			btnWhite.performClick();
			buttonLayout.setVisibility(View.GONE);
			res = myReflector.getResourceInteger(
					baseClass, 
					subClass);
		}else if(subClass.endsWith(".string")){
			Log.i(TAG, "^ Populating list with strings");
			btnWhite.performClick();
			buttonLayout.setVisibility(View.GONE);
			res = myReflector.getResourceStrings(
					baseClass, 
					subClass);
		}else{
			Log.i(TAG, "^ Populating list with generic data");
			btnWhite.performClick();
			buttonLayout.setVisibility(View.GONE);
			res = myReflector.getResourceGeneric(
					baseClass, 
					subClass);
		}

		tvValueItems.setText(String.valueOf(res));
	}


	private void populateResourceSpinner(String location){

		ArrayList<String> list = myReflector.getSubClasses(location);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_spinner_item, 
				list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerResources.setAdapter(adapter);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private void resourceListOnClick(View v, int pos, long id){
		String message = "";
		String copied_text = "";

		try{
			HashMap<String, Object> selection = (HashMap<String, Object>) myList.getItemAtPosition(pos);

			copied_text ="'"+ selection.get("name") + "'"; 
			message = "'" + copied_text +  "' "	+ getString(R.string.text_copied);

			uB.showToast(message, Toast.LENGTH_SHORT, Gravity.TOP,0,0);
			ClipboardManager ClipMan = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipMan.setText(copied_text);

		}catch(Exception e){
			Log.e(TAG, "^ listOnClick error: " + e.getMessage());
		}
	}
}

enum MENU_BUTTONS {
	ABOUT;

	public static MENU_BUTTONS lookUpByOrdinal(int i) {
		return MENU_BUTTONS.values()[i];
	}
}