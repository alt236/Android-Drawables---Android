package aws.apps.androidDrawables.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.activities.Main;

public class ExportIntentService extends IntentService {
	private static final int NOTIFICATION_ID = 1;
	
	private final String TAG = this.getClass().getName();

	public static final String BROADCAST_COMPLETED = "aws.apps.androidDrawables.service.ExportIntentService.BROADCAST_COMPLETED";
	public static final String BROADCAST_REQUEST_CANCELATION = "aws.apps.androidDrawables.service.ExportIntentService.BROADCAST_REQUEST_CANCELATION";

	public static final String EXTRA_R_LOCATION = "EXTRA_R_LOCATION";
	public static final String EXTRA_RESOURCE_NAME = "EXTRA_RESOURCE_NAME";

	private static boolean isRunning = false;

	private boolean mUserCancelled = false;
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
		
		sendNotification("Starting Export", "Will now try to export: ");
		
		if(!hasUserCancelled()){
			//doMessageSync(DataStoreSingleton.getInstance(this).getDB().getLatestMessageTimestamp());
			//notifyActivity(messenger, MESSAGES_UPDATED);
		} else {
			Log.w(TAG, "^ EIS: Service run has been canceled - skipping MESSAGES");
		}

		sendNotification("Export Completed", "Thingies have been saved at");
		isRunning = false;

		Intent updateIntent = new Intent(BROADCAST_COMPLETED);
		LocalBroadcastManager.getInstance(this).sendBroadcast(updateIntent);
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


	private void sendNotification(String title, String Content){
		//NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		NotificationCompat.Builder builder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(title)
		        .setContentText(Content)
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

		// NOTIFICATION_ID allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID,  builder.getNotification());
	}

}
