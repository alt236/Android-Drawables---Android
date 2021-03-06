package aws.apps.androidDrawables.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.containers.ResourceInfo;

public class BooleanResourceAdapter extends AbstractResourceAdapter {
	private final List<ResourceInfo> items;
	private final Context context;

	public BooleanResourceAdapter(Context context, int textViewResourceId, List<ResourceInfo> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listitem, null);
		}

		ResourceInfo o = items.get(position);
		if (o != null) {			
			TextView top = (TextView) v.findViewById(R.id.string1);
			TextView bottom = (TextView) v.findViewById(R.id.string2);
			Boolean value = context.getResources().getBoolean(o.getId());
			
			top.setText(o.getName());
			bottom.setText(String.valueOf(value));
			
			top.setTextColor(mTextColour);
			bottom.setTextColor(mTextColour);
		}
		return v;
	}

}