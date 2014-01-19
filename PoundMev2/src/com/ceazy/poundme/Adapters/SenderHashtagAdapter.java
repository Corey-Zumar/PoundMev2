package com.ceazy.poundme.Adapters;

import java.util.List;

import com.ceazy.poundme.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SenderHashtagAdapter extends ArrayAdapter<String> {

	private List<String> hashTags;
	private TextView tvHashTag;
	Typeface tfBariol;

	public SenderHashtagAdapter(Context context, int textViewResourceId,
			List<String> hashTags) {
		super(context, textViewResourceId, hashTags);
		this.hashTags = hashTags;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.sender_hashtag_layout, parent, false);
			tfBariol = Typeface.createFromAsset(getContext().getAssets(), "fonts/Bariol_Regular.otf");
		}
		String hashTag = hashTags.get(position);
		if (hashTag != null) {
			tvHashTag = (TextView) v.findViewById(R.id.tvSenderHashtag);
			tvHashTag.setTypeface(tfBariol);
			tvHashTag.setText(hashTag);
		}
		return v;
	}
}
