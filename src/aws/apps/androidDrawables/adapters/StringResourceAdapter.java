package aws.apps.androidDrawables.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import aws.apps.androidDrawables.R;

public class StringResourceAdapter extends ArrayAdapter<Map<String, Object>> {
	private final List<Map<String, Object>> items;
	private final Context context;

	public StringResourceAdapter(Context context, int textViewResourceId, List<Map<String, Object>> items) {
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

		Map<String, Object> o = items.get(position);
		if (o != null) {			
			TextView top = (TextView) v.findViewById(R.id.string1);
			TextView bottom = (TextView) v.findViewById(R.id.string2);

			top.setText((String) o.get("name"));
			bottom.setText(context.getString((Integer) o.get("id")));
		}
		return v;
	}
}