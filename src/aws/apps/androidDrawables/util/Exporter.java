package aws.apps.androidDrawables.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import aws.apps.androidDrawables.containers.ResourceInfo;

public class Exporter {
	public static final String EXPORTABLE_TYPE_STRING = "string";
	public static final String EXPORTABLE_TYPE_COLOR = "color";
	public static final String EXPORTABLE_TYPE_DRAWABLE = "drawable";
	private final String TAG =  this.getClass().getName();

	public void forceMediaScan(Context context){
		context.sendBroadcast (
				new Intent(Intent.ACTION_MEDIA_MOUNTED, 
						Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	public String getXmlString(Context context, List<ResourceInfo> resources){
		StringBuffer sb = new StringBuffer();

		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("\n");
		sb.append("<resources>");
		sb.append("\n");


		String tmp;
		for(ResourceInfo resource : resources){
			if(resource.getName() != null && resource.getId() > 0){
				if(resource.getType().endsWith(EXPORTABLE_TYPE_STRING)){
					tmp = context.getString(resource.getId());
					sb.append("<string name=\"" + resource.getName() + "\">" + tmp + "</string>");
					sb.append("\n");
				} else if (resource.getType().endsWith(EXPORTABLE_TYPE_COLOR)){

					int color= context.getResources().getColor(resource.getId());
					tmp = "#" + Integer.toHexString(color).toUpperCase();
					sb.append("<color name=\"" + resource.getName() + "\">" + tmp + "</color>");
					sb.append("\n");
				}
			}
		}

		sb.append("</resources>");
		return sb.toString();
	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public boolean saveDrawableToFile(Context context, Integer drawableId, String saveAs){
		boolean res = false;
		//Log.d(TAG, "^ saveDrawableToFile() - Saving " +drawableId+ " to " + saveAs);

		if(drawableId != null && drawableId > 0){
			if(isExternalStorageWritable()){
				FileOutputStream outStream;

				Bitmap bm = BitmapFactory.decodeResource( context.getResources(), drawableId);
				File file = new File(saveAs);

				if(bm!= null){
					createParentDirs(file);
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

	private void createParentDirs(File f){
		if(!f.getParentFile().exists()){f.getParentFile().mkdirs();}	
	}
	
	public boolean writeStringToExternalStorage(String content, String saveAs){
		boolean res = false;
		
		if(isExternalStorageWritable()){
			OutputStreamWriter out = null;
			File file = new File(saveAs);
			createParentDirs(file);
			
			try{
				out = new OutputStreamWriter(new FileOutputStream(file));
				out.write(content); 
				out.flush(); 
				out.close();
				res = true; 
			}catch(Exception e){;
				Log.e(TAG, "^ writeStringToExternalStorage() - Error: " + e.getMessage());
				e.printStackTrace(); 
			}
			
		}else {
			Log.w(TAG, "^ saveDrawableToFile() - Unable to save '" + saveAs + "' as the external storage is unavailable");
		}
		return res;
	}
}
