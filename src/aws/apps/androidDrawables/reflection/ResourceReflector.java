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
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.adapters.BooleanResourceAdapter;
import aws.apps.androidDrawables.adapters.ColourResourceAdapter;
import aws.apps.androidDrawables.adapters.DrawableResourceAdapter;
import aws.apps.androidDrawables.adapters.GenericResourceAdapter;
import aws.apps.androidDrawables.adapters.IntegerResourceAdapter;
import aws.apps.androidDrawables.adapters.StringResourceAdapter;

public class ResourceReflector {
	private final String TAG = this.getClass().getName();
	private ListView mList;
	private Context mContext;

	public ResourceReflector(ListView myList, Context context) {
		super();
		this.mList = myList;
		this.mContext = context;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDrawableList(String baseClass, String fullClass){
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Class<android.R.drawable> rDrawable = null;
		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();

			for (Class<?> subclass : subClassTable) {
				//Log.d(TAG, "SUBCLASS! " + subclass);
				
				if (fullClass.equals(subclass.getCanonicalName())) {
					rDrawable = (Class<drawable>) subclass;
					Field[] drawables = rDrawable.getFields();

					for (Field dr : drawables) {
						//Log.d(TAG, "DRAWABLE! " + dr);
						
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", dr.getInt(null));
						map.put("name", dr.getName());
						map.put("type", fullClass);
						list.add(map);
					}
					break; // we are not interested in anything else atm.
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "^ getDrawableList() Error while parsing (" + baseClass + "/" +  fullClass + "): ", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, Object>> getItemList(String baseClass, String fullClass, boolean bHasPrimitives){
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Class<android.R.drawable> rString = null;
		Class<?> rClass;
		try {
			rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();

			for (Class<?> subclass : subClassTable) {		
				if (fullClass.equals(subclass.getCanonicalName())) {
					rString = (Class<drawable>) subclass;
					Field[] strings = rString.getFields();

					if(bHasPrimitives){
						for (Field dr : strings) {
							Map<String, Object> map = new HashMap<String, Object>();
							// Not pulling this in case its not a primitive.
							map.put("id", dr.getInt(null)); 
							map.put("name", dr.getName());
							map.put("type", fullClass);
							list.add(map);
						}
					}else{
						for (Field dr : strings) {
							Map<String, Object> map = new HashMap<String, Object>();
							// Not pulling this in case its not a primitive.
							//map.put("id", dr.getInt(null)); 
							map.put("name", dr.getName());
							map.put("type", fullClass);
							list.add(map);
						}
					}
					break; // we are not interested in anything else atm.
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "^ getItemList() Error while parsing (" + baseClass + "/" +  fullClass + "): ", e);
		}
		return list;
	}

	public int populateResourceBoolean(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<Map<String, Object>> itemList = getItemList(baseClass, fullClass, true);
		sortList(itemList);

		mList.setAdapter(new BooleanResourceAdapter(
				mContext,
				R.layout.listitem, 
				itemList));

		return itemList.size();
	}


	public int populateResourceColors(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<Map<String, Object>> itemList = getItemList(baseClass, fullClass, true);
		sortList(itemList);

		mList.setAdapter(new ColourResourceAdapter(
				mContext,
				R.layout.listitem_with_image, 
				itemList));

		return itemList.size();
	}

	public int populateResourceDrawables(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<Map<String, Object>> itemList = getDrawableList(baseClass, fullClass);
		sortList(itemList);

		mList.setAdapter(new DrawableResourceAdapter(
				mContext,
				R.layout.listitem_with_image,
				itemList));

		return itemList.size();
	}

	public int populateResourceGeneric(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<Map<String, Object>> itemList = getItemList(baseClass, fullClass, false);
		sortList(itemList);

		mList.setAdapter(new GenericResourceAdapter(mContext,
				R.layout.listitem,
				itemList));

		return itemList.size();
	}

	public int populateResourceInteger(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<Map<String, Object>> itemList = getItemList(baseClass, fullClass, true);
		sortList(itemList);

		mList.setAdapter(new IntegerResourceAdapter(
				mContext,
				R.layout.listitem, 
				itemList));

		return itemList.size();
	}

	public int populateResourceStrings(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<Map<String, Object>> itemList = getItemList(baseClass, fullClass, true);
		sortList(itemList);

		mList.setAdapter(new StringResourceAdapter(
				mContext,
				R.layout.listitem, 
				itemList));
		return itemList.size();
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
