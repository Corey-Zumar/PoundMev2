package com.ceazy.poundme.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class BoundReceiverService extends Service {
	
	Messenger toMainMessenger;
	NewPoundReceiver poundReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		setMessenger((Messenger) intent.getParcelableExtra("toMainMessenger"));
		registerReceivers();
		return null;
	}
	
	private void registerReceivers() {
		poundReceiver = new NewPoundReceiver();
		IntentFilter poundFilter = new IntentFilter("NEW_POUND_RECEIVED");
		poundFilter.addAction("POUND_ACCESSED");
		registerReceiver(poundReceiver, poundFilter);
	}
	
	private void setMessenger(Messenger toMainMessenger) {
		this.toMainMessenger = toMainMessenger;
	}
	
	private Messenger getMessenger() {
		return toMainMessenger;
	}
	
	private void passMessage(Bundle data, String objString) {
		Message msg = Message.obtain();
		msg.obj = objString;
		msg.setData(data);
		try {
			getMessenger().send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class BoundReceiverServiceBinder {
		public BoundReceiverService getBoundReceiverServiceInstance() {
			return BoundReceiverService.this;
		}
	}
	
	class NewPoundReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals("NEW_POUND_RECEIVED")) {
				passMessage(intent.getExtras(), "new_pound");
			} else if(action.equals("POUND_ACCESSED")) {
				passMessage(null, "pound_accessed");
			}
		}
		
	}

	@Override
	public void onDestroy() {
		try {
			unregisterReceiver(poundReceiver);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	

}
