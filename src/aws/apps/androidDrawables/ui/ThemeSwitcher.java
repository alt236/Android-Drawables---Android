package aws.apps.androidDrawables.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import aws.apps.androidDrawables.util.Constants;

public class ThemeSwitcher {
	public final static String PREF_THEME_RESID_ID = "theme_resid";
	public final static int DEFAULT_THEME_RESID = android.R.style.Theme_Black;
	
	public static void switchTheme(Activity act, int themeID){	
		SharedPreferences settings = act.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(PREF_THEME_RESID_ID, themeID);
		editor.commit();

		Intent intent = act.getIntent();
		act.finish();
		act.startActivity(intent);
	}
	
	public static void applySharedTheme(Activity act){
		SharedPreferences sPref = act.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		int themeID = sPref.getInt(PREF_THEME_RESID_ID, DEFAULT_THEME_RESID);
		act.setTheme(themeID);
	}
}