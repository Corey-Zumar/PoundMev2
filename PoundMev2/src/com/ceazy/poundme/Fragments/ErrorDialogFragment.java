package com.ceazy.poundme.Fragments;

import com.ceazy.poundme.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

public class ErrorDialogFragment extends DialogFragment {
	
	public static ErrorDialogFragment newInstance(String msg) {
		Bundle arguments = new Bundle();
		arguments.putString("msg", msg);
		ErrorDialogFragment frag = new ErrorDialogFragment();
		frag.setArguments(arguments);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		.setCustomTitle(createTitleView())
		.setMessage(getArguments().getString("msg"))
		.setPositiveButton("OK", null);
		return builder.create();
	}
	
	private View createTitleView() {
		View titleView = getActivity().getLayoutInflater().inflate(R.layout.dialog_title, null);
		TextView titleText = (TextView) titleView.findViewById(R.id.tvDialogTitleLarge);
		titleText.setText("Oops!");
		return titleView;
	}
	
}
