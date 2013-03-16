package aws.apps.androidDrawables.adapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import aws.apps.androidDrawables.R;
import aws.apps.androidDrawables.containers.ResourceInfo;

public abstract class AbstractResourceAdapter extends ArrayAdapter<ResourceInfo>{
	protected int mTextColour;
	
	public AbstractResourceAdapter(Context context, int textViewResourceId, List<ResourceInfo> objects) {
		super(context, textViewResourceId, objects);
		mTextColour = context.getResources().getColor(R.color.default_text_color);
	}

	public void updateTextColor(int colour){
		mTextColour = colour;
		notifyDataSetChanged();
	}
}
