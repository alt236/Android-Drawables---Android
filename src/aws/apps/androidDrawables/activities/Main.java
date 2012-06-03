package aws.apps.androidDrawables.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.adapters.AbstractResourceAdapter;
import aws.apps.androidDrawables.reflection.ResourceReflector;
import aws.apps.androidDrawables.util.UsefulBits;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockActivity implements OnClickListener, OnActionItemClickListener{
	private final String TAG =  this.getClass().getName();

	private UsefulBits uB;
	private ListView mList;
	private Button btnBlack;
	private Button btnWhite;
	private Button btnGreen;
	private Button btnOrange;
	private Button btnGray;
	private TextView tvTitleItems;
	private TextView tvValueItems;
	private TextView tvOS; 
	private LinearLayout mColourButtonSegment;
	private Spinner mSpinnerLocation;
	private Spinner mSpinnerResources;
	private ResourceReflector mReflector;
	private int mCurrentListBgColour;
	private int mCurrentListTextColour;
	private QuickAction mQuickAction;

	private final Hashtable<CharSequence, String> locationString2Type = new Hashtable<CharSequence, String>();

	private static final int QUICK_ACTION_COPY_NAME = 1;
	private static final int QUICK_ACTION_SHARE = 2;
	private static final int QUICK_ACTION_SOURCE = 3;
	private static final int QUICK_ACTION_COPY_VALUE = 4;

	private void buildUi(){
		tvOS = (TextView) findViewById(R.id.tvOS);

		tvTitleItems = (TextView) findViewById(R.id.titleItems);
		tvValueItems = (TextView) findViewById(R.id.valueItems);

		btnBlack = (Button) findViewById(R.id.main_black);
		btnWhite = (Button) findViewById(R.id.main_white);
		btnGreen = (Button) findViewById(R.id.main_green);
		btnOrange = (Button) findViewById(R.id.main_orange);
		btnGray = (Button) findViewById(R.id.main_gray);

		btnBlack.setOnClickListener(this);
		btnWhite.setOnClickListener(this);
		btnGreen.setOnClickListener(this);
		btnOrange.setOnClickListener(this);
		btnGray.setOnClickListener(this);

		mSpinnerResources = (Spinner) findViewById(R.id.spinnerResource);
		mSpinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);

		ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(
				this, R.array.resource_location_description_array, 
				android.R.layout.simple_spinner_item);

		adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerLocation.setAdapter(adapterLocation);
		mColourButtonSegment = (LinearLayout) findViewById(R.id.main_colour_buttons);

		mList = (ListView) findViewById(R.id.main_list);

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

	@Override
	public void onClick(View v) {
		Button b = (Button) v;
		// You need to set android:cacheColorHint="#00000000" in the 
		// list xml to make the list background stick.
		mCurrentListBgColour = Color.parseColor(b.getTag().toString());

		if(mCurrentListBgColour == getResources().getColor(R.color.white)){
			mCurrentListTextColour = getResources().getColor(R.color.black);
		}
		else {
			mCurrentListTextColour = getResources().getColor(R.color.white);
		}

		if(mList.getAdapter()!=null && mList.getAdapter() instanceof AbstractResourceAdapter ){
			((AbstractResourceAdapter) mList.getAdapter()).updateTextColor(mCurrentListTextColour);
		}

		mList.setBackgroundColor(mCurrentListBgColour);
	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		uB = new UsefulBits(this);

		buildUi();
		//		final Object data = getLastNonConfigurationInstance();
		//
		//
		//		if (data==null){
		//			//			mList = (ListView) findViewById(R.id.main_list);
		//			//			currentBgColour = getResources().getColor(R.color.black);
		//			//			mList.setBackgroundColor(currentBgColour);
		//
		//		} else{
		//			//			if(mList==null){
		//			//				mList = (ListView) findViewById(R.id.main_list);
		//			//				}
		//			//			currentBgColour = (Integer) data;
		//			//			mList.setBackgroundColor(currentBgColour);
		//		}

		mReflector = new ResourceReflector(mList, Main.this);
		populateResourceSpinner(getString(R.string.resource_class_public));

		mList.setEmptyView(findViewById(R.id.empty));

		locationString2Type.put(getString(R.string.resources_internal), getString(R.string.resource_class_internal));
		locationString2Type.put(getString(R.string.resources_public), getString(R.string.resource_class_public));

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				resourceListOnClick(v, pos, id);
			}
		});

		mSpinnerLocation.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String location = locationString2Type.get(mSpinnerLocation.getSelectedItem().toString());
				populateResourceSpinner(location);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//
			}   	
		});

		mSpinnerResources.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				populateList(
						locationString2Type.get(mSpinnerLocation.getSelectedItem().toString()),
						mSpinnerResources.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//
			}   	
		});


		mList.setFastScrollEnabled(true);

		tvOS.setText(Build.VERSION.RELEASE);

		selectDefaultColour();
	}

	/** Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(QuickAction source, int pos, int actionId) {
		//ActionItem actionItem = quickAction.getActionItem(pos);
		HashMap<String, Object> selection;

		switch(actionId){
		case QUICK_ACTION_COPY_NAME:
			selection = (HashMap<String, Object>) mList.getItemAtPosition(pos);

			if(selection != null){
				Object name = selection.get("name");
				if(name!=null){
					uB.copyText((String) name);
				}
			}
			break;
		case QUICK_ACTION_COPY_VALUE:
			//			selection = (HashMap<String, Object>) mList.getItemAtPosition(pos);
			//
			//			if(selection != null){
			//				Object name = selection.get("name");
			//				if(name!=null){
			//					uB.copyText((String) name);
			//				}
			//			}
			Toast.makeText(getApplicationContext(), "copy value item selected", Toast.LENGTH_SHORT).show();
			break;			
		case QUICK_ACTION_SHARE:
			Toast.makeText(getApplicationContext(), "share item selected", Toast.LENGTH_SHORT).show();
			break;
		case QUICK_ACTION_SOURCE:
			Toast.makeText(getApplicationContext(), "source item selected", Toast.LENGTH_SHORT).show();
			break;
		default:
		}

	}

	/** Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		if(R.id.menu_about == item.getItemId()){
			uB.showAboutDialogue();
			return true;
		}
		return false;
	}


	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurrentListBgColour;
	}

	private void populateList(String baseClass, String subClass) {
		int res = 0;
		boolean bShowColourBar = false;

		Log.i(TAG, "^ Populating list for '" + baseClass + "' / '" + subClass + "'");
		mList.setAdapter(null);

		if(subClass == null || subClass.length() <=0){return;}

		tvTitleItems.setText(getTitle(subClass));

		if(subClass.endsWith(".drawable")){
			Log.i(TAG, "^ Populating list with drawables");
			bShowColourBar = true;
			res = mReflector.getResourceDrawables(
					baseClass, 
					subClass);
		}
		else if(subClass.endsWith(".color")){
			Log.i(TAG, "^ Populating list with colours");
			bShowColourBar = true;
			res = mReflector.getResourceColors(
					baseClass, 
					subClass);
		}
		else if(subClass.endsWith(".bool")){
			Log.i(TAG, "^ Populating list with boolean");
			res = mReflector.getResourceBoolean(
					baseClass, 
					subClass);
		}
		else if(subClass.endsWith(".integer")){
			Log.i(TAG, "^ Populating list with integers");
			res = mReflector.getResourceInteger(
					baseClass, 
					subClass);
		}else if(subClass.endsWith(".string")){
			Log.i(TAG, "^ Populating list with strings");
			res = mReflector.getResourceStrings(
					baseClass, 
					subClass);
		}else{
			Log.i(TAG, "^ Populating list with generic data");
			res = mReflector.getResourceGeneric(
					baseClass, 
					subClass);
		}

		if(bShowColourBar){
			mColourButtonSegment.setVisibility(View.VISIBLE);
		}else{
			mColourButtonSegment.setVisibility(View.GONE);
			selectDefaultColour();
		}

		tvValueItems.setText(String.valueOf(res));
	}
	private void populateResourceSpinner(String location){

		ArrayList<String> list = mReflector.getSubClasses(location);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_spinner_item, 
				list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerResources.setAdapter(adapter);

		setupQuickActions();
	}

	private void resourceListOnClick(View v, int pos, long id){
		mQuickAction.show(v);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	}

	private void selectDefaultColour(){
		btnBlack.performClick();
	}

	private void setupQuickActions(){
		mQuickAction 	= new QuickAction(this, QuickAction.ORIENTATION_VERTICAL, QuickAction.COLOUR_LIGHT);

		ActionItem copyItemName = new ActionItem(QUICK_ACTION_COPY_NAME, getString(R.string.quickaction_label_copy_name), getResources().getDrawable(R.drawable.ic_copy));
		//ActionItem copyItemValue = new ActionItem(QUICK_ACTION_COPY_VALUE, getString(R.string.quick_action_label_copy_value), getResources().getDrawable(R.drawable.ic_copy));
		//ActionItem sharetItem = new ActionItem(QUICK_ACTION_SHARE, "Share Item", getResources().getDrawable(R.drawable.ic_envelope));
		//ActionItem viewSourceItem = new ActionItem(QUICK_ACTION_SOURCE, "View in Github", getResources().getDrawable(R.drawable.ic_eye_open));

		mQuickAction.addActionItem(copyItemName);
		//mQuickAction.addActionItem(sharetItem);
		//mQuickAction.addActionItem(viewSourceItem);

		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(this);
	}
}