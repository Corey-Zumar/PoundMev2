package com.ceazy.poundme.Fragments;

import java.util.ArrayList;
import java.util.List;

import com.ceazy.poundme.R;
import com.ceazy.poundme.Adapters.SenderHashtagAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SenderHashtagFragment extends DialogFragment implements DialogInterface.OnClickListener,
	OnItemClickListener {
	
	List<String> hashTags;
	String address;
	
	public static SenderHashtagFragment newInstance(String readableFunction, List<String> hashTags, String address) {
		Bundle arguments = new Bundle();
		arguments.putString("readableFunction", readableFunction);
		arguments.putStringArrayList("hashTags", (ArrayList<String>) hashTags);
		arguments.putString("address", address);
		SenderHashtagFragment frag = new SenderHashtagFragment();
		frag.setArguments(arguments);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		setAddress(arguments.getString("address"));
		setHashTagsList(arguments.getStringArrayList("hashTags"));
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		.setCustomTitle(createTitleView())
		.setView(createMainView())
		.setNegativeButton("Back", this);
		return builder.create();
	}
	
	private void setAddress(String address) {
		this.address = address;
	}
	
	private String getAddress() {
		return address;
	}
	
	private void setHashTagsList(List<String> hashTags) {
		this.hashTags = hashTags;
	}
	
	private List<String> getHashTagsList() {
		return hashTags;
	}
	
	public View createMainView() {
		View mainView = getActivity().getLayoutInflater().inflate(R.layout.sender_dialog_layout, null);
		ListView listView = (ListView) mainView.findViewById(R.id.lvSenderDialog);
		listView.setAdapter(getAdapter(getHashTagsList()));
		listView.setOnItemClickListener(this);
		return mainView;
	}
	
	public View createTitleView() {
		View titleView = getActivity().getLayoutInflater().inflate(R.layout.dialog_title, null);
		TextView tvTitleText = (TextView) titleView.findViewById(R.id.tvDialogTitleLarge);
		tvTitleText.setText("Choose a hashtag");
		return titleView;
	}
	
	public SenderHashtagAdapter getAdapter(List<String> hashTags) {
		return new SenderHashtagAdapter(getActivity(), R.layout.sender_hashtag_layout, hashTags);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent iNewMessage = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
		iNewMessage.putExtra("sms_body", getHashTagsList().get(position) + " ");
		String address = getAddress();
		if(address != null) {
			iNewMessage.setData(Uri.parse("sms:" + address));
		}
		startActivity(iNewMessage);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
	}

}
