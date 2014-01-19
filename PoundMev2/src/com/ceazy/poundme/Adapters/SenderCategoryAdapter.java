package com.ceazy.poundme.Adapters;

import java.util.List;

import com.ceazy.poundme.Category;
import com.ceazy.poundme.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SenderCategoryAdapter extends ArrayAdapter<Category> {

	private List<Category> categories;
	private TextView tvCategoryName;
	private ImageView ivCategoryIcon;
	Typeface tfBariol;

	public SenderCategoryAdapter(Context context, int textViewResourceId,
			List<Category> categories) {
		super(context, textViewResourceId, categories);
		this.categories = categories;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.sender_category_layout, parent, false);
			tfBariol = Typeface.createFromAsset(getContext().getAssets(), "fonts/Bariol_Regular.otf");
		}
		Category category = categories.get(position);
		if (category != null) {
			tvCategoryName = (TextView) v.findViewById(R.id.tvSenderCategoryName);
			tvCategoryName.setTypeface(tfBariol);
			ivCategoryIcon = (ImageView) v.findViewById(R.id.ivSenderCategoryIcon);
			tvCategoryName.setText(category.getName());
			ivCategoryIcon.setImageBitmap(category.getIcon());
		}
		return v;
	}
}
