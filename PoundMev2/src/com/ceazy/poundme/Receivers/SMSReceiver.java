package com.ceazy.poundme.Receivers;

import com.ceazy.poundme.Services.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	String address, body;
	Long date;

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
		SmsMessage[] messages = new SmsMessage[pduArray.length];
		for (int i = 0; i < pduArray.length; i++) {
			messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
			address = messages[i].getOriginatingAddress();
			body = messages[i].getDisplayMessageBody();
			date = messages[i].getTimestampMillis();
		}
		Bundle msgInfo = new Bundle();
		msgInfo.putString("address", address);
		msgInfo.putString("body", body);
		msgInfo.putLong("date", date);
		Intent iNewMessage = new Intent(context, NotificationService.class);
		iNewMessage.putExtras(msgInfo);
		context.startService(iNewMessage);
	}

}
