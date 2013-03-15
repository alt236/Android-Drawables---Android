package aws.apps.androidDrawables.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Exporter {
	private final String TAG =  this.getClass().getName();

	public boolean saveDrawableToFile(Context context, Integer drawableId, String saveAs){
		boolean res = false;
		Log.d(TAG, "^ saveDrawableToFile() - Saving " +drawableId+ " to " + saveAs);

		if(drawableId != null && drawableId > 0){
			if(isExternalStorageReadable()){
				FileOutputStream outStream;

				Bitmap bm = BitmapFactory.decodeResource( context.getResources(), drawableId);
				File file = new File(saveAs);

				if(bm!= null){
					if(!file.getParentFile().exists()){file.mkdirs();}
					try {					
						outStream = new FileOutputStream(file);
						bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
						outStream.flush();
						outStream.close();
						res = true;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					bm.recycle();
				} else {
					Log.w(TAG, "^ saveDrawableToFile() - The bitmap for '" + drawableId + "' was null!");
				}
			} else {
				Log.w(TAG, "^ saveDrawableToFile() - Unable to save '" + saveAs + "' as the external storage is unavailable");
			}
		}
		return res;
	}

	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public void forceMediaScan(Context context){
		context.sendBroadcast (
				new Intent(Intent.ACTION_MEDIA_MOUNTED, 
						Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

}
