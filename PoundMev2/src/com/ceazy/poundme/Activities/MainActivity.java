package com.ceazy.poundme.Activities;

import java.util.List;

import com.ceazy.lib.SuperTag.SuperTag;
import com.ceazy.poundme.Pound;
import com.ceazy.poundme.R;
import com.ceazy.poundme.Database.PoundsDB;
import com.ceazy.poundme.Fragments.CategoriesListFragment;
import com.ceazy.poundme.Fragments.SelectedCategoryFragment;
import com.ceazy.poundme.Fragments.SenderCategoryFragment;
import com.ceazy.poundme.Fragments.SenderHashtagFragment;
import com.ceazy.poundme.Services.BoundReceiverService;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends FragmentActivity {
	
	ServiceConnection poundsUpdaterConnection;
	ListFragment categoriesListFrag, selectedCategoryFrag;
	DialogFragment newPoundFrag, hashTagFrag;
	FragmentTransaction categoriesTransaction, selectedTransaction, newPoundTransaction, hashTagTransaction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startServices();
		checkFirstLaunch();
		if(savedInstanceState == null) {
			loadCategoriesListFragment();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_settings:
				if(android.os.Build.VERSION.SDK_INT >= 11) {
					Intent iSettings = new Intent(this, MainSettingsActivity.class);
					startActivity(iSettings);
				}
				break;
			case R.id.action_new_pound:
				loadNewPoundFragment(null);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void checkFirstLaunch() {
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("firstLaunch", true)) {
			performFirstLaunchActions();
		}
	}
	
	private void performFirstLaunchActions() {
		PoundsDB db = new PoundsDB(this);
		db.open();
		db.createEntry(new Pound("POUNDME", System.currentTimeMillis(), 
				new SuperTag("#application", "PoundMe", "application")));
		db.close();
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean("firstLaunch", false);
		editor.commit();
	}

	private void startServices() {
		startBoundService();
	} 
	
	private void startBoundService() {
		Messenger toMainMessenger = new Messenger(new NewPoundHandler());
		poundsUpdaterConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}

		};
		Intent iStartBoundService = new Intent(this, BoundReceiverService.class);
		iStartBoundService.putExtra("toMainMessenger", toMainMessenger);
		bindService(iStartBoundService, poundsUpdaterConnection, BIND_AUTO_CREATE);
	}
	
	private int getFragmentLayoutId() {
		return ((RelativeLayout) findViewById(R.id.main_view_group)).getId();
	}
	
	public void loadHashTagFragment(String readableFunction, List<String> hashTags, String address) {
		hashTagFrag = SenderHashtagFragment.newInstance(readableFunction, hashTags, address);
		hashTagTransaction = getSupportFragmentManager().beginTransaction();
		hashTagTransaction.add(hashTagFrag, "hashTagFrag");
		hashTagTransaction.commit();
	}
	
	public void loadSelectedCategoryListFragment(List<Pound> poundsList, String title) {
		selectedCategoryFrag = SelectedCategoryFragment.newInstance(poundsList, title);
		selectedTransaction = getSupportFragmentManager().beginTransaction();
		selectedTransaction.replace(getFragmentLayoutId(), 
				selectedCategoryFrag, "selectedFrag");
		selectedTransaction.addToBackStack(null);
		selectedTransaction.commit();
	}
	
	public void loadNewPoundFragment(String address) {
		newPoundFrag = SenderCategoryFragment.newInstance(address);
		newPoundTransaction = getSupportFragmentManager().beginTransaction();
		newPoundTransaction.add(newPoundFrag, "newPoundFrag");
		newPoundTransaction.commit();
	}
	
	private void loadCategoriesListFragment() {
		categoriesListFrag = CategoriesListFragment.newInstance();
		categoriesTransaction = getSupportFragmentManager().beginTransaction();
		categoriesTransaction.replace(getFragmentLayoutId(), 
				categoriesListFrag, "categoriesFrag");
		categoriesTransaction.commit();
	}
	
	private void updateSelectedListFrag(Pound pound) {
		SelectedCategoryFragment frag = (SelectedCategoryFragment) getSupportFragmentManager()
				.findFragmentByTag("selectedFrag");
		if(frag != null) {
			frag.updatePoundsList(pound);
		}
	}
	
	public void refreshCategoriesListFrag() {
		CategoriesListFragment frag = (CategoriesListFragment)
				getSupportFragmentManager().findFragmentByTag("categoriesFrag");
		frag.refreshCategoriesList();
	}
	
	private void updateCategoriesListFrag(Pound pound) {
		CategoriesListFragment frag = (CategoriesListFragment)
				getSupportFragmentManager().findFragmentByTag("categoriesFrag");
		frag.updateCategoriesList(pound);
	}
	
	public void handleOverflowClick(View v) {
		int position = (Integer) v.getTag();
		SelectedCategoryFragment frag = (SelectedCategoryFragment) getSupportFragmentManager()
				.findFragmentByTag("selectedFrag");
		if(frag != null) {
			frag.displayOverflowMenu(position, v);
		}
	}
	
	class NewPoundHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if(msg.obj == "new_pound") {
				Pound newPound = msg.getData().getParcelable("newPound");
				updateCategoriesListFrag(newPound);
				updateSelectedListFrag(newPound);
			} else if(msg.obj == "pound_accessed") {
				refreshCategoriesListFrag();
			}
			super.handleMessage(msg);
		}
		
	}

	@Override
	protected void onDestroy() {
		try {
			unbindService(poundsUpdaterConnection);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	

}
