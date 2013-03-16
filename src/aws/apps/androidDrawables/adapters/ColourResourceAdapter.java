package aws.apps.androidDrawables.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.containers.ResourceInfo;

public class ColourResourceAdapter extends AbstractResourceAdapter {
	private final List<ResourceInfo> items;
	private final Context context;

	public ColourResourceAdapter(Context context, int textViewResourceId, List<ResourceInfo> items) {
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
			ImageView image = (ImageView)v.findViewById(R.id.icon);
			TextView top = (TextView) v.findViewById(R.id.string1);
			TextView bottom = (TextView) v.findViewById(R.id.string2);
			int color= context.getResources().getColor(o.getId());
			
			image.setBackgroundColor(color);
			top.setText(o.getName());
			bottom.setText("#" + Integer.toHexString(color).toUpperCase());
			
			top.setTextColor(mTextColour);
			bottom.setTextColor(mTextColour);
		}
		return v;
	}
}