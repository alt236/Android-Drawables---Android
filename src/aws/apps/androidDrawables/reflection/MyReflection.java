package aws.apps.androidDrawables.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.drawable;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import aws.apps.androidDrawables.R;

public class MyReflection {
	private final String TAG =  this.getClass().getName();
	public static final String CLASS_ANDROID_R_DRAWABLE = "android.R.drawable";
	public static final String CLASS_ANDROID_R_STRING = "android.R.string";

	private ListView myList;
	private Context context;

	public MyReflection(ListView myList, Context context) {
		super();
		this.myList = myList;
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public int getStrings(){
		Class <android.R.drawable> rDrawable = null;
		try {
			Class<?> rClass = Class.forName("android.R");
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (CLASS_ANDROID_R_STRING.equals(subclass.getCanonicalName())) {
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
				} 
			}
			sortList(drInfo);
			myList.setAdapter(new SimpleAdapter(
					context, drInfo, R.layout.listitem,
					new String[] { "image", "name", "type"}, 
					new int[] { R.id.icon, R.id.name, R.id.type }));

		} catch (Exception e) {
			Log.e(TAG, "^ Error: " + e.getMessage());
		}
		if(rDrawable != null){
			return rDrawable.getFields().length;
		}else{
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public int getDrawables(){
		Class <android.R.drawable> rDrawable = null;
		try {
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
				} 
			}
			sortList(drInfo);
			myList.setAdapter(new SimpleAdapter(
					context, drInfo, R.layout.listitem,
					new String[] { "image", "name", "type"}, 
					new int[] { R.id.icon, R.id.name, R.id.type }));

		} catch (Exception e) {
			Log.e(TAG, "^ Error: " + e.getMessage());
		}
		if(rDrawable != null){
			return rDrawable.getFields().length;
		}else{
			return 0;
		}
	}

	private void sortList(List<Map<String, Object>> list){
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
				return ((String)lhs.get("name")).compareToIgnoreCase((String)rhs.get("name"));
			}
		});
	}
}
