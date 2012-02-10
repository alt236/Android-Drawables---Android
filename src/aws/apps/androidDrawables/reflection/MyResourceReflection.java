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
import aws.apps.androidDrawables.adapters.ColourResourceAdaptor;
import aws.apps.androidDrawables.adapters.StringResourceAdaptor;

public class MyResourceReflection {
	private final String TAG = this.getClass().getName();
	public final static String TYPE_PUBLIC = "android.R";
	public final static String TYPE_INTERNAL = "com.android.internal.R";

	private ListView myList;
	private Context context;
	private String baseClass = "";

	private final static int SUB_COLOR = 0;
	private final static int SUB_DRAWABLE = 1;
	private final static int SUB_STRING = 2;

	public MyResourceReflection(ListView myList, Context context,
			String baseClass) {
		super();
		this.myList = myList;
		this.context = context;

		this.baseClass = baseClass;
		logSubClasses();
	}

	@SuppressWarnings("unchecked")
	public int getResourceColors() {
		String type = getType(baseClass, SUB_COLOR);
		Class<android.R.drawable> rColor = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
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
			myList.setAdapter(new ColourResourceAdaptor(context,
					R.layout.listitem_with_image, drInfo));

		} catch (Exception e) {
			Log.e(TAG, "^ getResourceColors() Error: ", e);
		}
		if (rColor != null) {
			return rColor.getFields().length;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public int getResourceDrawables() {
		String type = getType(baseClass, SUB_DRAWABLE);
		Class<android.R.drawable> rDrawable = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
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

			myList.setAdapter(new SimpleAdapter(context, drInfo,
					R.layout.listitem_with_image, new String[] { "image",
							"name", "type" }, new int[] { R.id.icon,
							R.id.string1, R.id.string2 }));

		} catch (Exception e) {
			Log.e(TAG, "^ getResourceDrawables() Error: ", e);
		}
		if (rDrawable != null) {
			return rDrawable.getFields().length;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public int getResourceStrings() {
		String type = getType(baseClass, SUB_STRING);
		Class<android.R.drawable> rString = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (type.equals(subclass.getCanonicalName())) {
					rString = (Class<drawable>) subclass;
					Field[] strings = rString.getFields();

					for (Field dr : strings) {
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

			myList.setAdapter(new StringResourceAdaptor(context,
					R.layout.listitem, drInfo));
		} catch (Exception e) {
			Log.e(TAG, "^ getResourceStrings() Error: ", e);
		}
		if (rString != null) {
			return rString.getFields().length;
		} else {
			return 0;
		}
	}
	private String getType(String baseClass, int subClass) {
		if (TYPE_INTERNAL.equals(baseClass)) {
			switch (subClass) {
			case SUB_COLOR:
				return context.getString(R.string.com_android_internal_r_color);
			case SUB_DRAWABLE:
				return context
						.getString(R.string.com_android_internal_r_drawable);
			case SUB_STRING:
				return context
						.getString(R.string.com_android_internal_r_string);
			}
		} else if (TYPE_PUBLIC.equals(baseClass)) {
			switch (subClass) {
			case SUB_COLOR:
				return context.getString(R.string.android_r_color);
			case SUB_DRAWABLE:
				return context.getString(R.string.android_r_drawable);
			case SUB_STRING:
				return context.getString(R.string.android_r_string);
			}
		}
		return "";
	}
	public void logSubClasses() {
		Log.i(TAG, "^ Listing subclasses for '" + baseClass + "'");

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			Log.i(TAG, "^\tCount '" + subClassTable.length + "'");
			for (Class<?> subclass : subClassTable) {
				Log.i(TAG, "^ Subclass: " + subclass.getCanonicalName());
			}

		} catch (Exception e) {
			Log.e(TAG, "^ logSubClasses() Error: ", e);
		}
	}

	private void sortList(List<Map<String, Object>> list) {
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
				return ((String) lhs.get("name"))
						.compareToIgnoreCase((String) rhs.get("name"));
			}
		});
	}

}
