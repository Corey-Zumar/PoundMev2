package com.ceazy.poundme;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.util.DisplayMetrics;

public class ContactInfoObtainer {
	
	private final Context context;
	private ContentResolver cResolver;
	
	public ContactInfoObtainer(final Context context) {
		this.context = context;
		cResolver = context.getContentResolver();
	}
	
	private static String formatPhoneNumber(String number) { //Find the stackoverflow user to credit
		if (number.length() >= 12) {
			number = number.substring(0, number.length() - 4) + "-"
					+ number.substring(number.length() - 4, number.length());
		number = number.substring(0, number.length() - 8) + ")"
				+ number.substring(number.length() - 8, number.length());
		number = number.substring(0, number.length() - 12) + "("
				+ number.substring(number.length() - 12, number.length());
		}
		return number;
	}
	
	public String getContactForPhoneNumber(String rawnumber) {
		String processedNumber = null;
		rawnumber = formatPhoneNumber(rawnumber);
		Uri phonesuri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI,
				Uri.encode(rawnumber));
		String[] phonesProjection = new String[]{PhoneLookup.DISPLAY_NAME};
		Cursor phones = cResolver.query(phonesuri, phonesProjection, null, null, 
				null);
		if (phones.moveToFirst()) {
			if (phones.getString(phones
					.getColumnIndex(PhoneLookup.DISPLAY_NAME)) != null) {
				processedNumber = phones.getString(phones
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} else if(rawnumber.length() >= 15){
			processedNumber = rawnumber.substring(2, 7) + " "
					+ rawnumber.substring(7, 15);
		} else {
			processedNumber = rawnumber;
		}
		phones.close();
		return processedNumber;
	}
	
	public Bitmap getPhotoForPhoneNumber(String rawnumber) {
		Bitmap contactPhoto = null;
		int photoId = -1;
		rawnumber = formatPhoneNumber(rawnumber);
		Uri phonesuri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI,
				Uri.encode(rawnumber));
		String[] phonesProjection = new String[]{PhoneLookup.PHOTO_ID};
		Cursor phones = cResolver.query(phonesuri, phonesProjection, null, null, 
				null);
		if(phones.moveToFirst()) {
			if (phones.getString(phones
					.getColumnIndex(PhoneLookup.PHOTO_ID)) != null) {
				photoId = phones.getInt(phones
						.getColumnIndex(PhoneLookup.PHOTO_ID));
		}
	}
	if(photoId != -1) {
		contactPhoto = getBitmapForId(photoId);
	}
		phones.close();
		return contactPhoto;
}
	
	private Bitmap getBitmapForId(final int photoId) { //Credits - StackOverFlow user: Phil
		Uri bmpUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId);
		String[] bmpProjection = new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO};
		Cursor bmpCursor = cResolver.query(bmpUri, bmpProjection, null, null, null);
		try {
			Bitmap bmpPhoto = null;
			if(bmpCursor.moveToFirst()) {
				final byte[] bmpPhotoByte = bmpCursor.getBlob(0);
				if(bmpPhotoByte != null) {
					bmpPhoto = BitmapFactory.decodeByteArray(bmpPhotoByte, 0, bmpPhotoByte.length);
					bmpPhoto = scaleBitmap(bmpPhoto);
				}
				return bmpPhoto;
			}
		} finally {
			bmpCursor.close();
		}
		return null;
	}
	
	private Bitmap scaleBitmap(Bitmap originalBmp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float scaleFactor = metrics.scaledDensity / 3;
		int dimension = (int) (192 * scaleFactor);
		Bitmap finalBmp = Bitmap.createScaledBitmap(originalBmp, dimension, dimension, true);
		return finalBmp;
	}
}
