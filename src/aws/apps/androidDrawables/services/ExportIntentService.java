package aws.apps.androidDrawables.services;

import java.io.File;
import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.activities.Main;
import aws.apps.androidDrawables.containers.ResourceInfo;
import aws.apps.androidDrawables.reflection.ResourceReflector;
import aws.apps.androidDrawables.util.Exporter;

public class ExportIntentService extends IntentService {
	public static final String EXPORT_BASE_PATH = "AndroidResources";

	private static final int NOTIFICATION_ID = 1;

	private final String TAG = this.getClass().getName();

	public static final String BROADCAST_COMPLETED = "aws.apps.androidDrawables.service.ExportIntentService.BROADCAST_COMPLETED";
	public static final String BROADCAST_REQUEST_CANCELATION = "aws.apps.androidDrawables.service.ExportIntentService.BROADCAST_REQUEST_CANCELATION";

	public static final String EXTRA_R_LOCATION = "EXTRA_R_LOCATION";
	public static final String EXTRA_RESOURCE_NAMES = "EXTRA_RESOURCE_NAMES";

	private static boolean isRunning = false;

	private boolean mUserCancelled = false;
	private Exporter mExporter;

	private NotificationManager mNotificationManager;

	private BroadcastReceiver cancelReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.w(TAG, "^ EIS: Received cancelation request");
			mUserCancelled = true;
		}
	};

	public ExportIntentService() {
		super("AndroidDrawables-ExportIntentService");
	}

	public static boolean isRunning() {
		return isRunning;
	}

	private boolean hasUserCancelled() {
		if (mUserCancelled) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDestroy() {
		isRunning = false;
		unregisterReceiver(cancelReceiver);
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		isRunning = true;

		Log.i(TAG, "^ EIS: ExportIntentService started");
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Bundle b = intent.getExtras();

		if(b != null){
			String location = b.getString(EXTRA_R_LOCATION);
			String[] resourceNames = b.getStringArray(EXTRA_RESOURCE_NAMES);

			if(location != null && resourceNames != null && resourceNames.length > 0){
				performExport(location, resourceNames);
			} else {
				sendNotification("Export Error", "Nothing was exported...");
			}


		} else {
			Log.i(TAG, "^ EIS: Bundle is null!");
		}

		Intent updateIntent = new Intent(BROADCAST_COMPLETED);
		LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);

		isRunning = false;
		Log.i(TAG, "^ EIS: ExportIntentService done");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isRunning = true;

		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_REQUEST_CANCELATION);
		registerReceiver(cancelReceiver, filter);

		return super.onStartCommand(intent, flags, startId);
	}



	private void performExport(String location, String[] resourceNames){
		final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		mExporter = new Exporter();

		String exportPath  = sdPath + File.separator + EXPORT_BASE_PATH + File.separator;
		long count = 0;

		for(String type : resourceNames){
			if(hasUserCancelled()){break;}

			sendNotification("Starting Export", "Exporting: " + type);
			if(Exporter.EXPORTABLE_TYPE_COLOR.equals(type)){
				count += doExportColors(location, exportPath);
			} else if (Exporter.EXPORTABLE_TYPE_DRAWABLE.equals(type)){
				count += doExportDrawables(location, exportPath);
			} else if (Exporter.EXPORTABLE_TYPE_STRING.equals(type)){
				count += doExportStrings(location, exportPath);
			}
		}

		mExporter.forceMediaScan(this);
		sendNotification("Export Completed", count + " files saved.");
	}

	private long doExportDrawables(String location, final String basePath){

		final ResourceReflector reflector = new ResourceReflector(null, this);
		final String fullClass = location + ".drawable";
		final String targetPath = basePath + fullClass + File.separator;

		List<ResourceInfo> itemList = reflector.getDrawableList(location, fullClass);
		Log.d(TAG, "^ doExportDrawables() - Exporting " + itemList.size() + " items to " + targetPath);

		boolean res;
		long count = 0;
		String iconName;

		for(ResourceInfo item : itemList){
			iconName = (String) item.getName();
			if(iconName != null && item.getId() >0){
				sendNotification("Exporting Drawables", iconName);
				res = mExporter.saveDrawableToFile(this, (Integer) item.getId(), targetPath + iconName + ".png");
				if (res){
					count +=1;
				}
			}
		}	

		return count;
	}

	private long doExportColors(String location, final String basePath){

		final ResourceReflector reflector = new ResourceReflector(null, this);
		final String fullClass = location + ".color";
		final String targetPath = basePath + fullClass + File.separator;

		List<ResourceInfo> itemList = reflector.getItemList(location, fullClass, true);
		Log.d(TAG, "^ doExportColors() - Exporting " + itemList.size() + " items to " + targetPath);

		sendNotification("Exporting Colors", "");
		String colorsString = mExporter.getXmlString(this, itemList);

		boolean res = mExporter.writeStringToExternalStorage(colorsString, targetPath + "colors.xml");

		if(res){
			return 1;
		} else {
			return 0;
		}
	}
	
	private long doExportStrings(String location, final String basePath){

		final ResourceReflector reflector = new ResourceReflector(null, this);
		final String fullClass = location + ".string";
		final String targetPath = basePath + fullClass + File.separator;

		List<ResourceInfo> itemList = reflector.getItemList(location, fullClass, true);
		Log.i(TAG, "^ doExportStrings() - Exporting " + itemList.size() + " items to " + targetPath);

		sendNotification("Exporting Strings", "");
		String xml = mExporter.getXmlString(this, itemList);

		boolean res = mExporter.writeStringToExternalStorage(xml, targetPath + "strings.xml");

		if(res){
			return 1;
		} else {
			return 0;
		}
	}
	
	private void sendNotification(String title, String content){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(title)
		.setContentText(content)
		.setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, Main.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(Main.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);

		mNotificationManager.notify(NOTIFICATION_ID,  builder.getNotification());
	}
}
