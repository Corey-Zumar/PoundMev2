package com.ceazy.poundme;

import com.ceazy.lib.SuperTag.SuperTag;
import com.ceazy.poundme.Activities.CopyURLActivity;

import android.content.Context;
import android.content.Intent;

public class ActionIntentCreator {
	
	Context context;
	
	public ActionIntentCreator(Context context) {
		this.context = context;
	}
	
	private Context getContext() {
		return context;
	}
	
	public Intent createCopyURLActionIntent(SuperTag tag) {
		Intent iCopyURL = new Intent(getContext(), CopyURLActivity.class);
		iCopyURL.putExtra("tag", tag);
		return iCopyURL;
	}
	
	public Intent createShareActionIntent(String sharePhrase) {
		Intent iShare = new Intent(Intent.ACTION_SEND);
		iShare.setType("text/plain");
		iShare.putExtra(Intent.EXTRA_TEXT, sharePhrase);
		return Intent.createChooser(iShare, 
				"Choose an app to share this pound");
		
	}

}
