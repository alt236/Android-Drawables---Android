package aws.apps.androidDrawables.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.ui.MyAlertBox;

public class UsefulBits {
	/**
	 * Gets the software version and version name for this application
	 */
	public enum SOFTWARE_INFO{
		NAME, VERSION, NOTES, CHANGELOG, COPYRIGHT, ACKNOWLEDGEMENTS
	}
	final String TAG =  this.getClass().getName();

	private Context c;

	public UsefulBits(Context cntx) {
		Log.d(TAG, "^ Object created");
		c = cntx;
	}

	public Calendar convertMillisToDate(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar;
	}

//	public boolean createDirectories(String dirs){
//		Log.d(TAG, "^ createDirectories - Attempting to create: " + dirs);
//		try{
//
//			if (new File(dirs).exists()){
//				Log.d(TAG, "^ createDirectories - Directory already exist:" + dirs);
//				return true;
//			}
//
//			// create a File object for the parent directory
//			File newDirectories = new File(dirs);
//			// have the object build the directory structure, if needed.
//			if(newDirectories.mkdirs()){
//				showToast("Directories created: " + dirs, 
//						Toast.LENGTH_SHORT, Gravity.TOP,0,0);
//				Log.d(TAG, "^ createDirectories - Directory created:" + dirs);
//				return true;					
//			} else {
//				showToast("Could not create: " + dirs, 
//						Toast.LENGTH_SHORT, Gravity.TOP,0,0);
//				Log.e(TAG, "^ createDirectories - Could not create:" + dirs);
//				return false;
//			}
//
//		}catch (Exception e){//Catch exception if any
//			showToast("Could not create: " + dirs, 
//					Toast.LENGTH_SHORT, Gravity.TOP,0,0);
//			Log.e(TAG, "^ createDirectories - something went wrong (" + dirs + ") " + e.getMessage());	
//			return false;
//		}
//	}

	public String formatDateTime(String formatString, Date d){
		Format formatter = new SimpleDateFormat(formatString);
		return formatter.format(d);
	} 

	public String getSoftwareInfo(SOFTWARE_INFO info) {
		try {
			PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
			Resources appR = c.getResources();
			CharSequence txt;
			// Store the software version code and version name somewhere..
			switch(info){
			case VERSION:
				return pi.versionName;
			case NAME:
				txt = appR.getText(appR.getIdentifier("app_name", "string", c.getPackageName())); 
				break;
			case NOTES:
				txt = appR.getText(appR.getIdentifier("app_notes", "string", c.getPackageName())); 
				break;
			case CHANGELOG:
				txt = appR.getText(appR.getIdentifier("app_changelog", "string", c.getPackageName())); 
				break;
			case COPYRIGHT:
				txt = appR.getText(appR.getIdentifier("app_copyright", "string", c.getPackageName())); 
				break;
			case ACKNOWLEDGEMENTS:
				txt = appR.getText(appR.getIdentifier("app_acknowledgements", "string", c.getPackageName())); 
				break;
			default:
				return "";
			}
			String res = txt.toString();
			res = res.replaceAll("\\t", "");
			res = res.replaceAll("\\n\\n", "\n");

			return res.trim();
		} catch (Exception e) {
			Log.e(TAG, "^ Error @ getSoftwareInfo(" + info.name() + ") ", e);
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

//	public void saveToFile(String fileName, File directory, String contents){
//
//		if (android.os.Environment.getExternalStorageState().equals(
//				android.os.Environment.MEDIA_MOUNTED)){
//			try {
//
//				if (directory.canWrite()){
//					File gpxfile = new File(directory, fileName);
//					FileWriter gpxwriter = new FileWriter(gpxfile);
//					BufferedWriter out = new BufferedWriter(gpxwriter);
//					out.write(contents);
//					out.close();
//					showToast("Saved to SD as '" + directory.getAbsolutePath() + "/" + fileName + "'", 
//							Toast.LENGTH_SHORT, Gravity.TOP,0,0);
//				}
//
//			} catch (Exception e) {
//				showToast("Could not write file:\n+ e.getMessage()", 
//						Toast.LENGTH_SHORT, Gravity.TOP,0,0);
//				Log.e(TAG, "^ Could not write file " + e.getMessage());
//			}
//
//		}else{
//			showToast("No SD card is mounted...", Toast.LENGTH_SHORT, Gravity.TOP,0,0);
//			Log.e(TAG, "^ No SD card is mounted.");		
//		}
//	}

	public void showAboutDialogue(){
		String text = "";
		String title = "";

		text += this.getSoftwareInfo(SOFTWARE_INFO.CHANGELOG);
		text += "\n\n";
		text += this.getSoftwareInfo(SOFTWARE_INFO.NOTES);
		text += "\n\n";
		text += this.getSoftwareInfo(SOFTWARE_INFO.ACKNOWLEDGEMENTS);
		text += "\n\n";		
		text += this.getSoftwareInfo(SOFTWARE_INFO.COPYRIGHT);
		title = this.getSoftwareInfo(SOFTWARE_INFO.NAME) + " v"
		+ this.getSoftwareInfo(SOFTWARE_INFO.VERSION);

		if (!(c==null)){
			MyAlertBox.create(c, text, title, c.getString(R.string.ok)).show();
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
}
