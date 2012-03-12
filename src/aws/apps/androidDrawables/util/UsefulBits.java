package aws.apps.androidDrawables.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.ui.MyAlertBox;

public class UsefulBits {
	final String TAG =  this.getClass().getName();

	private Context c;

	public UsefulBits(Context cntx) {
		c = cntx;
	}

	public Calendar convertMillisToDate(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar;
	}

	public String formatDateTime(String formatString, Date d){
		Format formatter = new SimpleDateFormat(formatString);
		return formatter.format(d);
	} 

	public String getAppVersion(){
		PackageInfo pi;
		try {
			pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}

	}

	public boolean isOnline() {
		try{ 
			ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm != null) {
				Log.d(TAG, "^ isOnline()=true");
				return cm.getActiveNetworkInfo().isConnected();
			} else {
				Log.d(TAG, "^ isOnline()=false");
				return false;
			}

		}catch(Exception e){
			Log.e(TAG, "^ isOnline()=false", e);
			return false;
		}
	}

	public void showAboutDialogue(){
		String title = c.getString(R.string.app_name) + " v"+ getAppVersion();

		StringBuffer sb = new StringBuffer();

		sb.append(c.getString(R.string.app_changelog));
		sb.append("\n\n");
		sb.append(c.getString(R.string.app_notes));
		sb.append("\n\n");
		sb.append(c.getString(R.string.app_acknowledgements));
		sb.append("\n\n");		
		sb.append(c.getString(R.string.app_copyright));



		if (!(c==null)){
			MyAlertBox.create(c, sb.toString(), title, c.getString(R.string.ok)).show();
		} else {
			Log.d(TAG, "^ context is null...");
		}
	}

	public void ShowAlert(String title, String text, String button){
		if (button.equals("")){button = c.getString(android.R.string.ok);}

		try{
			AlertDialog.Builder ad = new AlertDialog.Builder(c);
			ad.setTitle( title );
			ad.setMessage(text);

			ad.setPositiveButton( button, null );
			ad.show();
		}catch (Exception e){
			Log.e(TAG, "^ ShowAlert() Error: ", e);
		}	
	}

	public void showToast(String message, int duration, int location, int x_offset, int y_offset){
		Toast toast = Toast.makeText(c.getApplicationContext(), message, duration);
		toast.setGravity(location,x_offset,y_offset);
		toast.show();
	}

	@SuppressWarnings("deprecation")
	public void copyText(String text) {
		String message = "'" + text +  "' " + c.getString(R.string.text_copied);

		try{
			ClipboardManager ClipMan = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipMan.setText(text);
			showToast(message, Toast.LENGTH_SHORT, Gravity.TOP,0,0);
		}catch(Exception e){
			Log.e(TAG, "^ copyText() error: " + e.getMessage());
		}
	}
}
