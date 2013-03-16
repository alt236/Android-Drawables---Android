package aws.apps.androidDrawables.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import aws.apps.androidDrawables.containers.ResourceInfo;

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
	public List<ResourceInfo> getDrawableList(String baseClass, String fullClass){
		ArrayList<ResourceInfo> list = new ArrayList<ResourceInfo>();
		Class<android.R.drawable> rDrawable = null;
		try {
			Class<?> rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			ResourceInfo resourceInfo;
			for (Class<?> subclass : subClassTable) {				
				if (fullClass.equals(subclass.getCanonicalName())) {
					rDrawable = (Class<drawable>) subclass;
					Field[] drawables = rDrawable.getFields();

					for (Field dr : drawables) {
						resourceInfo = new ResourceInfo(dr.getInt(null), dr.getName(), fullClass);
						list.add(resourceInfo);
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
	public ArrayList<ResourceInfo> getItemList(String baseClass, String fullClass, boolean bHasPrimitives){
		ArrayList<ResourceInfo> list = new ArrayList<ResourceInfo>();
		Class<android.R.drawable> rString = null;
		Class<?> rClass;
		try {
			rClass = Class.forName(baseClass);
			Class<?>[] subClassTable = rClass.getDeclaredClasses();
			ResourceInfo resourceInfo;
			
			for (Class<?> subclass : subClassTable) {		
				if (fullClass.equals(subclass.getCanonicalName())) {
					rString = (Class<drawable>) subclass;
					Field[] strings = rString.getFields();

					if(bHasPrimitives){
						for (Field dr : strings) {
							resourceInfo = new ResourceInfo(dr.getInt(null), dr.getName(), fullClass);
							list.add(resourceInfo);
						}
					}else{
						for (Field dr : strings) {
							resourceInfo = new ResourceInfo(dr.getName(), fullClass);
							list.add(resourceInfo);
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
		
		List<ResourceInfo> itemList = getItemList(baseClass, fullClass, true);
		sortList(itemList);

		mList.setAdapter(new BooleanResourceAdapter(
				mContext,
				R.layout.listitem, 
				itemList));

		return itemList.size();
	}


	public int populateResourceColors(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<ResourceInfo> itemList = getItemList(baseClass, fullClass, true);
		sortList(itemList);

		mList.setAdapter(new ColourResourceAdapter(
				mContext,
				R.layout.listitem_with_image, 
				itemList));

		return itemList.size();
	}

	public int populateResourceDrawables(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<ResourceInfo> itemList = getDrawableList(baseClass, fullClass);
		sortList(itemList);

		mList.setAdapter(new DrawableResourceAdapter(
				mContext,
				R.layout.listitem_with_image,
				itemList));

		return itemList.size();
	}

	public int populateResourceGeneric(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<ResourceInfo> itemList = getItemList(baseClass, fullClass, false);
		sortList(itemList);

		mList.setAdapter(new GenericResourceAdapter(mContext,
				R.layout.listitem,
				itemList));

		return itemList.size();
	}

	public int populateResourceInteger(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<ResourceInfo> itemList = getItemList(baseClass, fullClass, true);
		sortList(itemList);

		mList.setAdapter(new IntegerResourceAdapter(
				mContext,
				R.layout.listitem, 
				itemList));

		return itemList.size();
	}

	public int populateResourceStrings(String baseClass, String fullClass) {
		if(mList == null){return -1;}
		
		List<ResourceInfo> itemList = getItemList(baseClass, fullClass, true);
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

	private void sortList(List<ResourceInfo> list) {
		Collections.sort(list, new Comparator<ResourceInfo>() {
			@Override
			public int compare(ResourceInfo lhs, ResourceInfo rhs) {
				return lhs.getName().compareToIgnoreCase(rhs.getName());
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
