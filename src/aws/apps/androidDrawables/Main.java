package aws.apps.androidDrawables;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.drawable;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import aws.apps.androidDrawables.R.color;
import aws.apps.androidDrawables.util.UsefulBits;

@SuppressWarnings("deprecation")
enum MENU_BUTTONS {
	ABOUT;

	public static MENU_BUTTONS lookUpByOrdinal(int i) {
		return MENU_BUTTONS.values()[i];
	}
}

public class Main extends Activity {
	final String TAG =  this.getClass().getName();
	private static final String CLASS_ANDROID_R_DRAWABLE = "android.R.drawable";


	private UsefulBits uB;
	private ListView myList;
	private Button btnBlack;
	private Button btnWhite;
	private Button btnGreen;
	private Button btnOrange;
	private Button btnGray;
	private TextView tvDrawables;
	private TextView tvOS;
	private int currentBgColour;

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

		tvOS = (TextView) findViewById(R.id.tvOS);
		tvDrawables = (TextView) findViewById(R.id.tvDrawables);

		btnBlack = (Button) findViewById(R.id.main_black);
		btnWhite = (Button) findViewById(R.id.main_white);
		btnGreen = (Button) findViewById(R.id.main_green);
		btnOrange = (Button) findViewById(R.id.main_orange);
		btnGray = (Button) findViewById(R.id.main_gray);

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
		
		btnBlack.setOnClickListener(colorButtonListener);
		btnWhite.setOnClickListener(colorButtonListener);
		btnGreen.setOnClickListener(colorButtonListener);
		btnOrange.setOnClickListener(colorButtonListener);
		btnGray.setOnClickListener(colorButtonListener);

		populateList();
	}

	@SuppressWarnings("unchecked")
	private void populateList(){
		try {
			Class <android.R.drawable> rDrawable = null;
			Class<?> rClass = Class.forName("android.R");
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (CLASS_ANDROID_R_DRAWABLE.equals(subclass.getCanonicalName())) {
					rDrawable = (Class<drawable>) subclass;
					Field[] drawables = rDrawable.getFields();

					for (Field dr : drawables) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", CLASS_ANDROID_R_DRAWABLE);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				} else {
					Log.d(TAG, "^ Skipping: " + subclass.getCanonicalName());
				}
			}

			myList.setAdapter(new SimpleAdapter(this, drInfo, R.layout.listitem,
					new String[] { "image", "name", "type"}, 
					new int[] { R.id.icon, R.id.name, R.id.type }));

			tvDrawables.setText(rDrawable.getFields().length+"");
			tvOS.setText(Build.VERSION.RELEASE);
		} catch (Exception e) {
			Log.e(TAG, "^ Error: " + e.getMessage());
		}
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