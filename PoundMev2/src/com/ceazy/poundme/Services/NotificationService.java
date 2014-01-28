package com.ceazy.poundme.Services;

import java.util.List;
import java.util.Map;

import com.ceazy.lib.SuperTag.SuperInfoObtainer;
import com.ceazy.lib.SuperTag.SuperIntent;
import com.ceazy.lib.SuperTag.SuperTag;
import com.ceazy.lib.SuperTag.SuperTextAnalyzer;
import com.ceazy.poundme.ActionIntentCreator;
import com.ceazy.poundme.ContactInfoObtainer;
import com.ceazy.poundme.IconObtainer;
import com.ceazy.poundme.NotificationVibrator;
import com.ceazy.poundme.Pound;
import com.ceazy.poundme.Activities.NotificationActivity;
import com.ceazy.poundme.Database.PoundsDB;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends IntentService {
	
	ContactInfoObtainer cObtainer;
	IconObtainer iObtainer;
	String contactName;
	Bitmap contactPhoto;
	Map<String, String> readableMap;
	PoundsDB poundsDB;
	SharedPreferences sharedPrefs;
	NotificationVibrator notifVibrator;
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle msgInfo = intent.getExtras();
		analyzeAndPostNotification(msgInfo);
	}
	
	public NotificationService() {
		super("com.ceazy.poundme.Services.NotificationService");
	}

	private ContactInfoObtainer getContactInfoObtainer() {
		if(cObtainer == null) {
			cObtainer = new ContactInfoObtainer(this);
		}
		return cObtainer;
	}
	
	private void setContactName(String address) {
		this.contactName = getContactInfoObtainer().getContactForPhoneNumber(address);
	}
	
	private String getContactName() {
		return contactName;
	}
	
	private IconObtainer getIconObtainer() {
		if(iObtainer == null) {
			iObtainer = new IconObtainer(this);
		}
		return iObtainer;
	}
	
	private void analyzeAndPostNotification(Bundle msgInfo) {
		String body = msgInfo.getString("body");
		SuperTextAnalyzer analyzer = new SuperTextAnalyzer(this);
		if(analyzer.containsHashTag(body)) {
			String address = msgInfo.getString("address");
			setContactName(address);
			List<SuperTag> tagsList = analyzer.getAllTags(body);
			postNotifications(tagsList, address);
		}
	}
	
	private void postNotifications(List<SuperTag> tagsList, String address) {
		NotificationManager notifManager = (NotificationManager) 
				getSystemService(NOTIFICATION_SERVICE);
		for(SuperTag tag : tagsList) {
			long time = System.currentTimeMillis();
			addPoundToDatabaseAndNotify(tag, address, time);
			notifManager.notify((int) time, createNotification(tag, address, time));
			vibrate();
			playRingtone();
		}
	}
	
	private Notification createNotification(SuperTag tag, String address, long time) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
		.setSmallIcon(getNotificationSmallIcon(tag))
		.setLargeIcon(getNotificationLargeIcon(address, tag))
		.setContentTitle(buildNotificationTitle(tag))
		.setTicker(buildNotificationTicker(tag))
		.setContentText(buildNotificationMessage(tag))
		.setContentIntent(createNotificationIntent(tag.getIntent(this, 
				new Integer[]{0}), time));
		addActions(builder, tag);
		return builder.build();
	}
	
	private void addActions(NotificationCompat.Builder builder, SuperTag tag) {
		ActionIntentCreator actionCreator = new ActionIntentCreator(this);
		PendingIntent sharePI = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
				actionCreator.createShareActionIntent(tag.getHashTag() +
						" " + tag.getHashPhrase()), 0);
		builder.addAction(0, "Share", sharePI);
		PendingIntent copyURLPI = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
				actionCreator.createCopyURLActionIntent(tag), 0);
		builder.addAction(0, "Get link", copyURLPI);
	}
	
	private Bitmap getNotificationLargeIcon(String address, SuperTag tag) {
		if(contactPhoto == null) {
			Bitmap photoFromAddress = contactPhoto = getContactInfoObtainer()
					.getPhotoForPhoneNumber(address);
			if(photoFromAddress != null) {
				contactPhoto = photoFromAddress;
			} else {
				contactPhoto = BitmapFactory.decodeResource(getResources(), 
						getIconObtainer().getLargeIconForFunction(tag.getFunction()));
			}
		}
		return contactPhoto;
	}
	
	private int getNotificationSmallIcon(SuperTag tag) {
		return getIconObtainer().getSmallIconForFunction(tag.getFunction());
	}
	
	private Map<String, String> getReadableMap(Integer[] indices) {
		if(readableMap == null) {
			SuperInfoObtainer infoObtainer = new SuperInfoObtainer(this);
			readableMap = infoObtainer.createReadableFunctionsDictionary(indices);
		}
		return readableMap;
	}
	
	private String buildNotificationTitle(SuperTag tag) {
		StringBuilder builder = new StringBuilder("# from ");
		builder.append(getContactName());
		return builder.toString();
	}
	
	
	private String buildNotificationMessage(SuperTag tag) {
		String function = tag.getFunction();
		String hashTag = tag.getHashTag();
		if(function.equals("genericSearch")) {
			hashTag = "#";
		}
		StringBuilder builder = new StringBuilder(hashTag);
		builder.append(": ");
		builder.append(tag.getHashPhrase());
		return builder.toString();
	}
	
	@SuppressLint("DefaultLocale")
	private String buildNotificationTicker(SuperTag tag) {
		Integer[] readableIndices = new Integer[]{0, 4, 2, 2, 1, 0, 0, 0, 0, 5, 3, 1};
		StringBuilder builder = new StringBuilder("New " + 
				getReadableMap(readableIndices).get(tag.getFunction()).toLowerCase());
		builder.append(" shared with you by " + getContactName());
		return builder.toString();
	}
	
	private PendingIntent createNotificationIntent(SuperIntent intent, long time) {
		Intent notificationIntent = new Intent(this, NotificationActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.putExtra("launchIntent", intent);
		notificationIntent.putExtra("time", time);
		PendingIntent notificationPI = PendingIntent.getActivity(this, 
				(int) System.currentTimeMillis(), notificationIntent, 0);
		return notificationPI;
	}
	
	private void addPoundToDatabaseAndNotify(SuperTag tag, String address, long time) {
		Pound newPound  = new Pound(address, time, tag);
		newPound.setSeenStatus(false);
		sendNewPoundBroadcast(newPound);
		addPoundToDatabase(newPound);
	}
	
	private void sendNewPoundBroadcast(Pound pound) {
		Bundle extras = new Bundle();
		extras.putParcelable("newPound", pound);
		Intent iNewPound = new Intent("NEW_POUND_RECEIVED");
		iNewPound.putExtras(extras);
		PendingIntent newPoundPI = PendingIntent.getBroadcast(this, 
				(int) System.currentTimeMillis(), iNewPound, 0);
		try {
			newPoundPI.send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	
	private PoundsDB getPoundsDB() {
		if(poundsDB == null) {
			poundsDB = new PoundsDB(this);
		}
		return poundsDB;
	}
	
	private void addPoundToDatabase(Pound pound) {
		PoundsDB database = getPoundsDB();
		database.open();
		database.createEntry(pound);
		database.close();
	}
	
	private SharedPreferences getSharedPreferences() {
		if(sharedPrefs == null) {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		}
		return sharedPrefs;
	}
	
	private NotificationVibrator getNotificationVibrator(int speed) {
		if(notifVibrator == null) {
			notifVibrator = new NotificationVibrator(this, speed);
		}
		return notifVibrator;
	}
	
	private void playRingtone() {
		SharedPreferences prefs = getSharedPreferences();
		if(prefs.getBoolean("prefNotifSoundBOOL", false)) {
			Uri ringToneUri = Uri.parse(prefs.getString("ringtoneUriString", "null"));
			RingtoneManager.getRingtone(this, ringToneUri).play();
		}
	}
	
	private void vibrate() {
		SharedPreferences prefs = getSharedPreferences();
		if(prefs.getBoolean("prefNotifVibrationBOOL", false)) {
			getNotificationVibrator(Integer.valueOf(prefs.getString("prefSetPattern", "2"))).vibrate();
		}
	}
	
}

