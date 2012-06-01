package aws.apps.androidDrawables.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.ArrayAdapter;
import aws.apps.androidDrawables.R;

public abstract class AbstractResourceAdapter extends ArrayAdapter<Map<String, Object>>{
	protected int mTextColour;
	
	public AbstractResourceAdapter(Context context, int textViewResourceId, List<Map<String, Object>> objects) {
		super(context, textViewResourceId, objects);
		mTextColour = context.getResources().getColor(R.color.default_text_color);
	}

	public void updateTextColor(int colour){
		mTextColour = colour;
		notifyDataSetChanged();
	}
}
