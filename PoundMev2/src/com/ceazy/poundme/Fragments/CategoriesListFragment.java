package com.ceazy.poundme.Fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.ceazy.lib.SuperTag.SuperInfoObtainer;
import com.ceazy.poundme.Category;
import com.ceazy.poundme.IconObtainer;
import com.ceazy.poundme.Pound;
import com.ceazy.poundme.R;
import com.ceazy.poundme.Activities.MainActivity;
import com.ceazy.poundme.Adapters.CategoryAdapter;
import com.ceazy.poundme.Database.PoundsDB;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CategoriesListFragment extends ListFragment {
	
	Map<String, String> readableFunctionsMap;
	IconObtainer iObtainer;
	List<Category> categoriesList;
	CategoryAdapter categoryAdapter;
	Map<String, List<Pound>> poundsMap;
	
	public static CategoriesListFragment newInstance() {
		return new CategoriesListFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		CategoryPopulator populator = new CategoryPopulator();
		populator.execute();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Category selectedCategory = getCategoriesList().get(position);
		((MainActivity) getActivity()).loadSelectedCategoryListFragment(
				getCategorizedPounds().get(selectedCategory.getFunction()), selectedCategory.getName());
		selectedCategory.setContainsUnseenStatus(false);
		getAdapter().notifyDataSetChanged();
		super.onListItemClick(l, v, position, id);
	}

	private CategoryAdapter getAdapter() {
		if(categoryAdapter == null) {
			categoryAdapter = createAdapter();
		}
		return categoryAdapter;
	}
	
	private CategoryAdapter createAdapter() {
		CategoryAdapter adapter = new CategoryAdapter(getActivity(), 
				R.layout.category_layout, getCategoriesList());
		return adapter;
	}
	
	private Map<String, String> getReadableFunctionsMap() {
		if(readableFunctionsMap == null) {
			Integer[] indices = {0, 4, 5, 3, 2, 1, 0, 1, 1, 6, 3, 2};
			SuperInfoObtainer obtainer = new SuperInfoObtainer(getActivity());
			readableFunctionsMap = obtainer.createReadableFunctionsDictionary(indices);
		}
		return readableFunctionsMap;
	}
	
	private Map<String, List<Pound>> getCategorizedPounds() {
		if(poundsMap == null) {
			PoundsDB db = new PoundsDB(getActivity());
			db.open();
			poundsMap = db.getCategorizedPounds();
			db.close();
		}
		return poundsMap;
	}
	
	private IconObtainer getIconObtainer() {
		if(iObtainer == null) {
			iObtainer = new IconObtainer(getActivity());
		}
		return iObtainer;
	}

	private List<Category> getCategoriesList() {
		if(categoriesList == null) {
			categoriesList = createCategoriesList();
		}
		return categoriesList;
	}
	
	private Category createCategory(Pound pound, IconObtainer obtainer,
			Map<String, String> readableFunctions) {
		String function = pound.getFunction();
		Bitmap icon = BitmapFactory.decodeResource(getResources(), 
				obtainer.getLargeIconForFunction(function));
		Category category = new Category(function, readableFunctions.get(function),
				pound.getHashTag() + " " + pound.getHashPhrase(), icon);
		return category;
	}
	
	private List<Category> createCategoriesList() {
		List<Category> categoriesList = new ArrayList<Category>();
		Map<String, String> readableFuncs = getReadableFunctionsMap();
		Map<String, List<Pound>> poundsMap = getCategorizedPounds();
		IconObtainer iconObtainer = getIconObtainer();
		for(String function : poundsMap.keySet()) {
			List<Pound> poundsList = poundsMap.get(function);
			Pound pound = poundsList.get(0);
			Category newCategory = createCategory(pound, iconObtainer, readableFuncs);
			if(containsUnseen(poundsList)) {
				newCategory.setContainsUnseenStatus(true);
			}
			categoriesList.add(newCategory);
		}
		return categoriesList;
	}
	
	public void updateImage(Bitmap icon) {
		getCategoriesList().get(0).setIcon(icon);
		getAdapter().notifyDataSetChanged();
	}
	
	public void refreshCategoriesList() {
		poundsMap = null;
		categoriesList = null;
		categoryAdapter = createAdapter();
		this.setListAdapter(categoryAdapter);
	}
	
	public void updateCategoriesList(Pound pound) {
		String function = pound.getFunction();
		int removeIndex = -1;
		Category newCategory = createCategory(pound, getIconObtainer(),
				getReadableFunctionsMap());
		if(!pound.getSeenStatus()) {
			newCategory.setContainsUnseenStatus(true);
		}
		List<Category> categoriesList = getCategoriesList();
		for(int i = 0; i < categoriesList.size(); i++) {
			if(categoriesList.get(i).getFunction().equals(function)) {
				removeIndex = i;
				break;
			}
		}
		if(removeIndex > -1) {
			categoriesList.remove(removeIndex);
		}
		categoriesList.add(0, newCategory);
		getAdapter().notifyDataSetChanged();
		try {
			getCategorizedPounds().get(function).add(0, pound);
		} catch(NullPointerException e) {
			List<Pound> newList = new ArrayList<Pound>();
			newList.add(pound);
			getCategorizedPounds().put(function, newList);
		}
	}
	
	private boolean containsUnseen(List<Pound> poundsList) {
		for(Pound pound : poundsList) {
			if(!pound.getSeenStatus()) {
				return true;
			}
		}
		return false;
	}
	
	class CategoryPopulator extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			categoryAdapter = getAdapter();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(categoryAdapter);
			super.onPostExecute(result);
		}
	}
	
}
