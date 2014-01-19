package com.ceazy.poundme.Fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ceazy.lib.SuperTag.SuperInfoObtainer;
import com.ceazy.poundme.Category;
import com.ceazy.poundme.IconObtainer;
import com.ceazy.poundme.R;
import com.ceazy.poundme.Activities.MainActivity;
import com.ceazy.poundme.Adapters.SenderCategoryAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SenderCategoryFragment extends DialogFragment implements DialogInterface.OnClickListener,
	OnItemClickListener {
	
	Map<String, List<String>> funcsAndTags;
	List<Category> categoriesList;
	String address;
	
	public static SenderCategoryFragment newInstance(String address) {
		Bundle arguments = new Bundle();
		arguments.putString("address", address);
		SenderCategoryFragment frag = new SenderCategoryFragment();
		frag.setArguments(arguments);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setAddress(getArguments().getString("address"));
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View titleView = inflater.inflate(R.layout.dialog_title, null);
		TextView tvTitle = (TextView) titleView.findViewById(R.id.tvDialogTitle);
		tvTitle.setText("What would you like to share?");
		
		View mainView = inflater.inflate(R.layout.sender_dialog_layout, null);
		ListView listView = (ListView) mainView.findViewById(R.id.lvSenderDialog);
		listView.setAdapter(getAdapter());
		listView.setOnItemClickListener(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		.setCustomTitle(titleView)
		.setView(mainView)
		.setNegativeButton("Cancel", this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
	}
	
	private void setAddress(String address) {
		this.address = address;
	}
	
	private String getAddress() {
		return address;
	}
	
	private SenderCategoryAdapter getAdapter() {
		return new SenderCategoryAdapter(getActivity(), R.layout.sender_category_layout, getCategoriesList());
	}
	
	private Map<String, List<String>> getFunctionsAndTagsDictionary() {
		if(funcsAndTags == null) {
			SuperInfoObtainer infoObtainer = new SuperInfoObtainer(getActivity());
			funcsAndTags = infoObtainer.getFunctionsAndHashTagsDictionary();
		}
		return funcsAndTags;
	}
	
	private List<Category> getCategoriesList() {
		if(categoriesList == null) {
			categoriesList = createCategoriesList();
		}
		return categoriesList;
	}
	
	private List<Category> createCategoriesList() {
		SuperInfoObtainer infoObtainer = new SuperInfoObtainer(getActivity());
		IconObtainer iconObtainer = new IconObtainer(getActivity());
		Integer[] indices = new Integer[]{0, 4, 4, 2, 1, 0, 0, 2, 0, 5, 3, 1}; 
		Map<String, String> readableFunctionsDictionary = infoObtainer
				.createReadableFunctionsDictionary(indices);
		List<Category> categoriesList = new ArrayList<Category>();
		for(String function : getFunctionsAndTagsDictionary().keySet()) {
			Category category = new Category(function, readableFunctionsDictionary.get(function),
					null, BitmapFactory.decodeResource(getResources(), 
							iconObtainer.getLargeIconForFunction(function)));
			categoriesList.add(category);
		}
		return categoriesList;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Category selectedCategory = getCategoriesList().get(position);
		String function = selectedCategory.getFunction();
		List<String> hashTags = getFunctionsAndTagsDictionary().get(function);
		((MainActivity) getActivity()).loadHashTagFragment(selectedCategory.getName(), hashTags, getAddress());
	}
	
	

}
