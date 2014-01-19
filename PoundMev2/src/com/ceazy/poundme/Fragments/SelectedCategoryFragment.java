package com.ceazy.poundme.Fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.ceazy.poundme.ActionIntentCreator;
import com.ceazy.poundme.ContactInfoObtainer;
import com.ceazy.poundme.DateFormatter;
import com.ceazy.poundme.Pound;
import com.ceazy.poundme.R;
import com.ceazy.poundme.Activities.MainActivity;
import com.ceazy.poundme.Adapters.PoundAdapter;
import com.ceazy.poundme.Database.PoundsDB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class SelectedCategoryFragment extends ListFragment {
	
	PoundAdapter adapter;
	List<Pound> poundsList;
	ContactInfoObtainer cObtainer;
	DateFormatter dFormatter;
	PoundsDB poundsDB;
	
	public static SelectedCategoryFragment newInstance(List<Pound> poundsList, String title) {
		Bundle arguments = new Bundle();
		arguments.putString("title", title);
		arguments.putParcelableArrayList("poundsList", (ArrayList<? extends Parcelable>) poundsList);
		SelectedCategoryFragment frag = new SelectedCategoryFragment();
		frag.setArguments(arguments);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		List<Pound> pounds = arguments.getParcelableArrayList("poundsList");
		ListPopulator populator = new ListPopulator();
		populator.execute(pounds);
		setActionBarTitle(arguments.getString("title"));
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {
				PriorityQueue<Pound> deleteQueue = new PriorityQueue<Pound>();
				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch(item.getItemId()) {
						case R.id.action_delete:
							PoundsDB db = getPoundsDB();
							db.open();
							while(deleteQueue.size() > 0) {
								deletePound(poundsDB, deleteQueue.poll());
							}
							getAdapter().notifyDataSetChanged();
							((MainActivity) getActivity()).refreshCategoriesListFrag();
							db.close();
							mode.finish();
							if(getPoundsList().size() == 0) {
								getActivity().getSupportFragmentManager().popBackStack();
							}
							return true;
						
						default:
							return false;
					}
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.delete_menu, menu);
					return true;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					deleteQueue.clear();
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
					Pound pound = getPoundsList().get(position);
					if (checked) {
						deleteQueue.add(pound);
					} else if (!checked) {
						deleteQueue.remove(pound);

					}
				}
				
			});
		}
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void deletePound(PoundsDB db, Pound pound) {
		db.deleteEntry(pound.getTime());
		getPoundsList().remove(pound);
	}
	
	@SuppressLint("NewApi")
	private void setActionBarTitle(String title) {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			((FragmentActivity) getActivity()).getActionBar().setTitle(title);
		} else {
			
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		getPoundsList().get(position).launch(getActivity(), 
				new Messenger(new LaunchHandler()), new Integer[]{Intent.FLAG_ACTIVITY_NEW_TASK});
		super.onListItemClick(l, v, position, id);
	}
	
	private ContactInfoObtainer getContactInfoObtainer() {
		if(cObtainer == null) {
			cObtainer = new ContactInfoObtainer(getActivity());
		}
		return cObtainer;
	}
	
	private PoundsDB getPoundsDB() {
		if(poundsDB == null) {
			poundsDB = new PoundsDB(getActivity());
		}
		return poundsDB;
	}
	
	private DateFormatter getDateFormatter() {
		if(dFormatter == null) {
			dFormatter = new DateFormatter();
		}
		return dFormatter;
	}

	private void setPoundsList(List<Pound> poundsList) {
		PoundsDB poundsDB = getPoundsDB();
		poundsDB.open();
		DateFormatter formatter = getDateFormatter();
		Map<String, String> cache = new HashMap<String, String>();
		Map<String, Bitmap> photoCache = new HashMap<String, Bitmap>();
		ContactInfoObtainer obtainer = getContactInfoObtainer();
		for(Pound pound : poundsList) {
			updateSeenStatus(pound, poundsDB);
			pound.setDate(formatter.getTimePassed(pound.getTime()));
			String address = pound.getAddress();
			if(!cache.containsKey(address)) {
				String sender = obtainer.getContactForPhoneNumber(address);
				pound.setSender(sender);
				cache.put(address, sender);
				Bitmap photo = obtainer.getPhotoForPhoneNumber(address);
				pound.setPhoto(photo);
				photoCache.put(address, photo);
			} else {
				pound.setSender(cache.get(address));
				pound.setPhoto(photoCache.get(address));
			}
		}
		poundsDB.close();
		this.poundsList = poundsList;
	}
	
	private void updateSeenStatus(Pound pound, PoundsDB db) {
		pound.setSeenStatus(true);
		db.setSeen(pound.getTime());
	}
	
	private List<Pound> getPoundsList() {
		return poundsList;
	}
	
	private PoundAdapter getAdapter() {
		if(adapter == null) {
			adapter = createAdapter();
		}
		return adapter;
	}
	
	private PoundAdapter createAdapter() {
		return new PoundAdapter(getActivity(), R.layout.pound_layout, getPoundsList());
	}
	
	public void updatePoundsList(Pound pound) {
		PoundsDB poundsDB = getPoundsDB();
		poundsDB.open();
		updateSeenStatus(pound, poundsDB);
		poundsDB.close();
		String address = pound.getAddress();
		ContactInfoObtainer obtainer = getContactInfoObtainer();
		pound.setSender(obtainer.getContactForPhoneNumber(address));
		Bitmap photo = obtainer.getPhotoForPhoneNumber(address);
		if(photo != null) {
			pound.setPhoto(photo);
		}
		pound.setDate(getDateFormatter().getTimePassed(pound.getTime()));
		getAdapter().notifyDataSetChanged();
	}
	
	@SuppressLint("NewApi")
	public void displayOverflowMenu(final int position, View v) {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			final ActionIntentCreator actionCreator = new ActionIntentCreator(getActivity());
			PopupMenu pMenu = new PopupMenu(getActivity(), v);
			pMenu.inflate(R.menu.pound_overflow);
			pMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Pound selectedPound = getPoundsList().get(position);
					switch(item.getItemId()) {
						case R.id.overflow_share:
							startActivity(actionCreator.createShareActionIntent(selectedPound.getHashTag() + " "
									+ selectedPound.getHashPhrase()));
							break;
						case R.id.overflow_getURL:
							startActivity(actionCreator.createCopyURLActionIntent(selectedPound.getTag()));
							break;
						case R.id.overflow_reply: 
							((MainActivity) getActivity()).loadNewPoundFragment(getPoundsList().get(position)
									.getAddress());
							break;
					}
					return false;
				}
				
			});
			pMenu.show();
		}
	}
	
	class LaunchHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
		}
		
	}
	
	class ListPopulator extends AsyncTask<List<Pound>, Integer, Void> {

		@Override
		protected Void doInBackground(List<Pound>... params) {
			try {
				setPoundsList(params[0]);
				adapter = getAdapter();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(adapter);
			super.onPostExecute(result);
		}
		
		
	}

	@Override
	public void onDestroyView() {
		setActionBarTitle("PoundMe");
		super.onDestroyView();
	}
	
	
	
	

}
