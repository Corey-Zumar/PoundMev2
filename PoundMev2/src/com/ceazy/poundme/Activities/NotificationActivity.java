package com.ceazy.poundme.Activities;

import com.ceazy.lib.SuperTag.SuperIntent;
import com.ceazy.lib.SuperTag.Error.SuperError;
import com.ceazy.poundme.Database.PoundsDB;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;

public class NotificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		long time = intent.getLongExtra("time", 0);
		cancelNotification(time);
		setSeen(time);
		notifyAccessed();
		launch(intent);
	}
	
	private void launch(Intent intent) {
		SuperIntent launchIntent = intent.getParcelableExtra("launchIntent");
		launchIntent.launch(this, new Messenger(new FinishHandler()));
	}
	
	private void setSeen(long time) {
		PoundsDB db = new PoundsDB(this);
		db.open();
		db.setSeen(time);
		db.close();
	}
	
	private void notifyAccessed() {
		PendingIntent accessedPI = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(),
				new Intent("POUND_ACCESSED"), 0);
		try {
			accessedPI.send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	
	private void cancelNotification(long id) {
		if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("prefPersistentNotifsBOOL"
				, true)) {
			NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notifManager.cancel((int) id);
		}
	}
	
	class FinishHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			SuperError error = msg.getData().getParcelable("error");
			if(error == null) {
				finish();
			} else {
				
			}
		}
		
	}
}
