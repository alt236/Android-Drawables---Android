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
import aws.apps.androidDrawables.adapters.BooleanResourceAdapter;
import aws.apps.androidDrawables.adapters.ColourResourceAdapter;
import aws.apps.androidDrawables.adapters.IntegerResourceAdapter;
import aws.apps.androidDrawables.adapters.StringResourceAdapter;

public class ResourceReflector {
	private final String TAG = this.getClass().getName();
	//	public final static String TYPE_PUBLIC = "android.R";
	//	public final static String TYPE_INTERNAL = "com.android.internal.R";

	private ListView myList;
	private Context context;

	public ResourceReflector(ListView myList, Context context) {
		super();
		this.myList = myList;
		this.context = context;

	}

	@SuppressWarnings("unchecked")
	public int getResourceBoolean(String baseClass, String fullClass) {
		Class<android.R.drawable> rColor = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (fullClass.equals(subclass.getCanonicalName())) {
					rColor = (Class<drawable>) subclass;
					Field[] colors = rColor.getFields();

					for (Field dr : colors) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", fullClass);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				}
			}
			sortList(drInfo);
			myList.setAdapter(new BooleanResourceAdapter(
					context,
					R.layout.listitem, 
					drInfo));

		} catch (Exception e) {
			Log.e(TAG, "^ getResourceBoolean() Error: ", e);
		}
		if (rColor != null) {
			return rColor.getFields().length;
		} else {
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int getResourceColors(String baseClass, String fullClass) {
		Class<android.R.drawable> rColor = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (fullClass.equals(subclass.getCanonicalName())) {
					rColor = (Class<drawable>) subclass;
					Field[] colors = rColor.getFields();

					for (Field dr : colors) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", fullClass);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				}
			}
			sortList(drInfo);
			myList.setAdapter(new ColourResourceAdapter(
					context,
					R.layout.listitem_with_image, 
					drInfo));

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
	public int getResourceDrawables(String baseClass, String fullClass) {
		Class<android.R.drawable> rDrawable = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (fullClass.equals(subclass.getCanonicalName())) {
					rDrawable = (Class<drawable>) subclass;
					Field[] drawables = rDrawable.getFields();

					for (Field dr : drawables) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", fullClass);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				}
			}
			sortList(drInfo);

			myList.setAdapter(new SimpleAdapter(context, drInfo,
					R.layout.listitem_with_image, 
					new String[] { "image", "name", "type" }, 
					new int[] { R.id.icon, R.id.string1, R.id.string2 }));

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
	public int getResourceGeneric(String baseClass, String fullClass) {
		Class<android.R.drawable> rString = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (fullClass.equals(subclass.getCanonicalName())) {
					rString = (Class<drawable>) subclass;
					Field[] strings = rString.getFields();

					for (Field dr : strings) {
						Map<String, Object> map = new HashMap<String, Object>();
						// Not pulling this in case its not a primitive.
						//map.put("id", dr.getInt(null)); 
						map.put("name", dr.getName());
						map.put("type", fullClass);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				}
			}
			sortList(drInfo);

			myList.setAdapter(new SimpleAdapter(context, drInfo,
					R.layout.listitem, 
					new String[] {"name", "type" }, 
					new int[] { R.id.string1, R.id.string2 }));
		} catch (Exception e) {
			Log.e(TAG, "^ getResourceGeneric() Error: ", e);
		}
		if (rString != null) {
			return rString.getFields().length;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public int getResourceInteger(String baseClass, String fullClass) {
		Class<android.R.drawable> rString = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (fullClass.equals(subclass.getCanonicalName())) {
					rString = (Class<drawable>) subclass;
					Field[] strings = rString.getFields();

					for (Field dr : strings) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", fullClass);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				}
			}
			sortList(drInfo);

			myList.setAdapter(new IntegerResourceAdapter(
					context,
					R.layout.listitem, 
					drInfo));
		} catch (Exception e) {
			Log.e(TAG, "^ getResourceInteger() Error: ", e);
		}
		if (rString != null) {
			return rString.getFields().length;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public int getResourceStrings(String baseClass, String fullClass) {
		Class<android.R.drawable> rString = null;

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();

			for (Class<?> subclass : subClassTable) {
				if (fullClass.equals(subclass.getCanonicalName())) {
					rString = (Class<drawable>) subclass;
					Field[] strings = rString.getFields();

					for (Field dr : strings) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", fullClass);
						drInfo.add(map);
					}
					break; // we are not interested in anything else atm.
				}
			}
			sortList(drInfo);

			myList.setAdapter(new StringResourceAdapter(
					context,
					R.layout.listitem, 
					drInfo));
		} catch (Exception e) {
			Log.e(TAG, "^ getResourceStrings() Error: ", e);
		}
		if (rString != null) {
			return rString.getFields().length;
		} else {
			return 0;
		}
	}

	public ArrayList<String> getSubClasses(String baseClass) {
		ArrayList<String> subClassList = new ArrayList<String>();

		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			for (Class<?> subclass : subClassTable) {
				subClassList.add(subclass.getCanonicalName());
			}

		} catch (Exception e) {
			Log.e(TAG, "^ logSubClasses() Error: ", e);
		}
		sortStringList(subClassList);
		return subClassList;
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

	private void sortStringList(List<String> list) {
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}
		});
	}
}
