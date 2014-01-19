package com.ceazy.poundme.Activities;

import com.ceazy.lib.SuperTag.SuperTag;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.Toast;

public class CopyURLActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		copyToClipboard((SuperTag) getIntent().getParcelableExtra("tag"));
		finish();
		super.onCreate(savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	private void copyToClipboard(SuperTag tag) {
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("Pound URL", 
				tag.getIntent(this, new Integer[0]).getBrowserURI(this));
			manager.setPrimaryClip(clip);
		}
		Toast.makeText(this, "Pound link copied to clipboard!", Toast.LENGTH_LONG).show();
	}
	
	

}
