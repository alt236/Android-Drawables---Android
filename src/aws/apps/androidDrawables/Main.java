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
import aws.apps.androidDrawables.reflection.MyReflection;
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
	private Spinner spinner;
	private MyReflection myReflection;
	private int currentBgColour;

	private final Hashtable<CharSequence, Integer> ressourceString2Id = new Hashtable<CharSequence, Integer>();

	private View.OnClickListener colorButtonListener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "^ Intent started");

		setContentView(R.layout.main);
		setTitle(getString(R.string.main_title));

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

		ressourceString2Id.put(getString(R.string.android_r_drawable), R.string.android_r_drawable);
		ressourceString2Id.put(getString(R.string.android_r_string), R.string.android_r_string);
		ressourceString2Id.put(getString(R.string.android_r_color), R.string.android_r_color);
		
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
				listOnClick(v, pos, id);
			}
		});

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				populateList(ressourceString2Id.get(parent.getItemAtPosition(pos).toString()));
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

		myReflection = new MyReflection(myList, this);

		myList.setFastScrollEnabled(true);

		tvOS.setText(Build.VERSION.RELEASE);
		//populateList(R.string.android_r_drawable);
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

		spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.resource_types, 
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		buttonLayout = (LinearLayout) findViewById(R.id.main_colour_buttons);
	}

	private void populateList(long listType) {
		int res = 0;

		myList.setAdapter(null);

		if(R.string.android_r_drawable == listType){
			Log.i(TAG, "^ Populating list with drawables");
			tvTitleItems.setText(R.string.label_drawables);
			buttonLayout.setVisibility(View.VISIBLE);
			res = myReflection.getDrawables();
		}
		else if(R.string.android_r_color == listType){
			Log.i(TAG, "^ Populating list with colours");
			tvTitleItems.setText(R.string.label_colours);
			buttonLayout.setVisibility(View.VISIBLE);
			res = myReflection.getColors();
		}
		else if(R.string.android_r_string == listType){
			Log.i(TAG, "^ Populating list with strings");
			tvTitleItems.setText(R.string.label_strings);
			btnWhite.performClick();
			buttonLayout.setVisibility(View.GONE);
			res = myReflection.getStrings();
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
	private void listOnClick(View v, int pos, long id){
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