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
import aws.apps.androidDrawables.adapters.ColourResoureAdaptor;

public class MyReflection {
	private final String TAG =  this.getClass().getName();
	private ListView myList;
	private Context context;

	public MyReflection(ListView myList, Context context) {
		super();
		this.myList = myList;
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public int getColors(){
		String type = context.getString(R.string.android_r_color);
		Class <android.R.drawable> rColor = null;
		
		try {
			Class<?> rClass = Class.forName("android.R");
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (type.equals(subclass.getCanonicalName())) {
					rColor = (Class<drawable>) subclass;
					Field[] colors = rColor.getFields();

					for (Field dr : colors) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", type);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				} 
			}
			sortList(drInfo);
			myList.setAdapter(
					new ColourResoureAdaptor(context, R.layout.listitem, drInfo));

		} catch (Exception e) {
			Log.e(TAG, "^ Error: " + e.getMessage());
		}
		if(rColor != null){
			return rColor.getFields().length;
		}else{
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public int getDrawables(){
		String type = context.getString(R.string.android_r_drawable);
		Class <android.R.drawable> rDrawable = null;
		
		try {
			Class<?> rClass = Class.forName("android.R");
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (type.equals(subclass.getCanonicalName())) {
					rDrawable = (Class<drawable>) subclass;
					Field[] drawables = rDrawable.getFields();

					for (Field dr : drawables) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", type);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				} 
			}
			sortList(drInfo);
			myList.setAdapter(new SimpleAdapter(
					context, drInfo, R.layout.listitem,
					new String[] { "image", "name", "type"}, 
					new int[] { R.id.icon, R.id.string1, R.id.string2 }));

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
	public int getStrings(){
		String type = context.getString(R.string.android_r_string);
		Class <android.R.drawable> rString = null;
		
		try {
			Class<?> rClass = Class.forName("android.R");
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (type.equals(subclass.getCanonicalName())) {
					rString = (Class<drawable>) subclass;
					Field[] strings = rString.getFields();

					for (Field dr : strings) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", type);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				} 
			}
			sortList(drInfo);
			myList.setAdapter(new SimpleAdapter(
					context, drInfo, R.layout.listitem,
					new String[] { "image", "name", "type"}, 
					new int[] { R.id.icon, R.id.string1, R.id.string2 }));

		} catch (Exception e) {
			Log.e(TAG, "^ Error: " + e.getMessage());
		}
		if(rString != null){
			return rString.getFields().length;
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
