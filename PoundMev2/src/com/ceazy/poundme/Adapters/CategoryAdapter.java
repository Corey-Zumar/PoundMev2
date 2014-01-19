package com.ceazy.poundme.Adapters;

import java.util.List;

import com.ceazy.lib.SuperTag.SuperLinkifier;
import com.ceazy.poundme.Category;
import com.ceazy.poundme.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<Category> {

	private List<Category> categories;
	private TextView tvCategoryName, tvMostRecent;
	private ImageView ivCategoryIcon;
	SuperLinkifier linkifier;

	public CategoryAdapter(Context context, int textViewResourceId,
			List<Category> categories) {
		super(context, textViewResourceId, categories);
		this.categories = categories;
		this.linkifier = new SuperLinkifier(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.category_layout, parent, false);
		}
		Category category = categories.get(position);
		if (category != null) {
			tvCategoryName = (TextView) v.findViewById(R.id.tvCategoryName);
			ivCategoryIcon = (ImageView) v.findViewById(R.id.ivCategoryIcon);
			tvMostRecent = (TextView) v.findViewById(R.id.tvMostRecent);
			tvCategoryName.setText(category.getName());
			if(category.getContainsUnseenStatus()) {
				tvCategoryName.setTextColor(getContext().getResources().getColor(getRedColor()));
			} else {
				tvCategoryName.setTextColor(Color.parseColor("#000000"));
			}
			ivCategoryIcon.setImageBitmap(category.getIcon());
			tvMostRecent.setText(category.getMostRecent());
			linkifier.Linkify(tvMostRecent, null, null);
		}
		return v;
	}
	
	@SuppressLint("InlinedApi")
	private int getRedColor() {
		if(android.os.Build.VERSION.SDK_INT >= 14) {
			return android.R.color.holo_red_light;
		}
		return Color.RED;
	}

}
