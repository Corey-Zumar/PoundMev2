package com.ceazy.poundme.Adapters;

import java.util.List;

import com.ceazy.lib.SuperTag.SuperLinkifier;
import com.ceazy.poundme.Pound;
import com.ceazy.poundme.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class PoundAdapter extends ArrayAdapter<Pound> {

	private List<Pound> pounds;
	private TextView tvSenderName, tvTagAndPhrase, tvDate;
	private ImageButton ibOverflow;
	private QuickContactBadge qcbContactPhoto;
	Typeface tfBariol;
	SuperLinkifier linkifier;

	public PoundAdapter(Context context, int textViewResourceId,
			List<Pound> pounds) {
		super(context, textViewResourceId, pounds);
		this.pounds = pounds;
		this.linkifier = new SuperLinkifier(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.pound_layout, parent, false);
			tfBariol = Typeface.createFromAsset(getContext().getAssets(), "fonts/Bariol_Regular.otf");
		}
		Pound pound = pounds.get(position);
		if (pound != null) {
			tvSenderName = (TextView) v.findViewById(R.id.tvSender);
			tvSenderName.setTypeface(tfBariol);
			tvTagAndPhrase = (TextView) v.findViewById(R.id.tvTagAndPhrase);
			ibOverflow = (ImageButton) v.findViewById(R.id.ibOverflow);
			ibOverflow.setTag(position);
			qcbContactPhoto = (QuickContactBadge) v.findViewById(R.id.qcbContactPhoto);
			tvDate = (TextView) v.findViewById(R.id.tvDate);
			tvDate.setTypeface(tfBariol);
			tvSenderName.setText(pound.getSender());
			tvTagAndPhrase.setText(getTagAndPhraseText(pound));
			linkifier.Linkify(tvTagAndPhrase, null, null);
			tvDate.setText(pound.getDate());
			Bitmap photo = pound.getPhoto();
			if(photo != null) {
				qcbContactPhoto.setImageBitmap(photo);
			}
		}
		return v;
	}
	
	private String getTagAndPhraseText(Pound pound) {
		String hashTag = pound.getHashTag();
		if(pound.getFunction().equals("genericSearch")) {
			hashTag = "#";
		}
		StringBuilder builder = new StringBuilder(hashTag);
		builder.append(" " + pound.getHashPhrase());
		return builder.toString();
	}

}