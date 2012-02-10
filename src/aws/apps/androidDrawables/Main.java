package aws.apps.androidDrawables;

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
import aws.apps.androidDrawables.reflection.MyResourceReflection;
import aws.apps.androidDrawables.util.UsefulBits;

@SuppressWarnings("deprecation")
enum MENU_BUTTONS {
	ABOUT;

	public static MENU_BUTTONS lookUpByOrdinal(int i) {
		return MENU_BUTTONS.values()[i];
	}
}

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
	private MyResourceReflection myReflection;
	private int currentBgColour;

	private final Hashtable<CharSequence, Integer> resourceString2Id = new Hashtable<CharSequence, Integer>();
	private final Hashtable<CharSequence, String> locationString2Type = new Hashtable<CharSequence, String>();

	private View.OnClickListener colorButtonListener;

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

		buildUi();

		myReflection = new MyResourceReflection(myList, Main.this);
		resourceString2Id.put(getString(R.string.android_r_drawable), R.string.android_r_drawable);
		resourceString2Id.put(getString(R.string.android_r_string), R.string.android_r_string);
		resourceString2Id.put(getString(R.string.android_r_color), R.string.android_r_color);

		resourceString2Id.put(getString(R.string.com_android_internal_r_color), R.string.com_android_internal_r_color);
		resourceString2Id.put(getString(R.string.com_android_internal_r_drawable), R.string.com_android_internal_r_drawable);
		resourceString2Id.put(getString(R.string.com_android_internal_r_string), R.string.com_android_internal_r_string);

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
				
				populateList(
						location,
						resourceString2Id.get(spinnerResources.getSelectedItem().toString()));
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
						resourceString2Id.get(spinnerResources.getSelectedItem().toString()));
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
		populateResourceSpinner(getString(R.string.resource_class_public));

		spinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);
		ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(
				this, R.array.resource_location_description_array, 
				android.R.layout.simple_spinner_item);
		adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerLocation.setAdapter(adapterLocation);

		buttonLayout = (LinearLayout) findViewById(R.id.main_colour_buttons);
	}

	private void populateResourceSpinner(String location){
		int array = -1;
		if(getString(R.string.resource_class_internal).equals(location)){
			array = R.array.resource_types_internal_array;
		}
		else if (getString(R.string.resource_class_public).equals(location)){
			array = R.array.resource_types_public_array;
		}
		
		ArrayAdapter<CharSequence> adapterResources = 
				ArrayAdapter.createFromResource(
				this, 
				array, 
				android.R.layout.simple_spinner_item);
		adapterResources.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerResources.setAdapter(adapterResources);
	}
	
	private void populateList(String baseClass, int listType) {
		int res = 0;
		String type = getString(listType);
		Log.i(TAG, "^ Populating list for '" + baseClass + "' / '" + type + "'");

		myList.setAdapter(null);

		if(R.string.android_r_drawable == listType || R.string.com_android_internal_r_drawable == listType){
			Log.i(TAG, "^ Populating list with drawables");
			tvTitleItems.setText(R.string.label_drawables);
			buttonLayout.setVisibility(View.VISIBLE);
			res = myReflection.getResourceDrawables(
					baseClass, 
					type);
		}
		else if(R.string.android_r_color == listType || R.string.com_android_internal_r_color == listType){
			Log.i(TAG, "^ Populating list with colours");
			tvTitleItems.setText(R.string.label_colours);
			buttonLayout.setVisibility(View.VISIBLE);
			res = myReflection.getResourceColors(
					baseClass, 
					type);
		}
		else if(R.string.android_r_string == listType || R.string.com_android_internal_r_string == listType){
			Log.i(TAG, "^ Populating list with strings");
			tvTitleItems.setText(R.string.label_strings);
			btnWhite.performClick();
			buttonLayout.setVisibility(View.GONE);
			res = myReflection.getResourceStrings(
					baseClass, 
					type);
		} else {
			Log.w(TAG, "^ NOT populating. Unknown type");
			tvTitleItems.setText(R.string.label_unknown);
			buttonLayout.setVisibility(View.GONE);
		}

		tvValueItems.setText(String.valueOf(res));
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