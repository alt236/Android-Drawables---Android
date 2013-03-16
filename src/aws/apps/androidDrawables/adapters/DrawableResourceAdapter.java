package aws.apps.androidDrawables.adapters;

import java.util.List;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.containers.ResourceInfo;

public class DrawableResourceAdapter extends AbstractResourceAdapter {
	private final String TAG =  this.getClass().getName();
	private final List<ResourceInfo> items;
	private final Context context;

	TextView top;
	TextView bottom;
	ImageView image;
	Drawable drawable;
	
	public DrawableResourceAdapter(Context context, int textViewResourceId, List<ResourceInfo> items) {
		super(context, textViewResourceId, items);
		
		this.items = items;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listitem_with_image, null);
		}

		ResourceInfo o = items.get(position);
		if (o != null) {			
			top = (TextView) v.findViewById(R.id.string1);
			bottom = (TextView) v.findViewById(R.id.string2);
			image = (ImageView) v.findViewById(R.id.icon);
			
			try{
				drawable = context.getResources().getDrawable(o.getId());
			} catch (NotFoundException e){
				Log.e(TAG, "^ Exception: " + e.getMessage(), e);
				
				drawable = null;
			}
			image.setImageDrawable(drawable);
			
			top.setText(o.getName());
			bottom.setText(o.getType());
			
			top.setTextColor(mTextColour);
			bottom.setTextColor(mTextColour);
		}
		return v;
	}
}