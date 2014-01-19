package com.ceazy.poundme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;

import com.ceazy.lib.SuperTag.SuperTag;

public class Pound implements Parcelable, Comparable<Pound> {
	
	SuperTag tag;
	long time;
	String address, date, hashTag, hashPhrase, function, sender;
	ContactInfoObtainer cObtainer;
	Bitmap photo;
	boolean seen = false;
	
	public Pound(String address, long time, SuperTag tag) {
		this.address = address;
		this.time = time;
		this.tag = tag;
	}
	
	public Pound(Parcel in) {
		this.address = in.readString();
		this.time = in.readLong();
		this.tag = in.readParcelable(SuperTag.class.getClassLoader());
	}
	
	public Pound(String address, long time, String hashTag, String hashPhrase,
			String function) {
		this.address = address;
		this.time = time;
		this.hashTag = hashTag;
		this.hashPhrase = hashPhrase;
		this.function = function;
	}
	
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setSeenStatus(boolean seen) {
		this.seen = seen;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getSender() {
		return sender;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getDate() {
		return date;
	}
	
	public SuperTag getTag() {
		if(tag == null) {
			tag = new SuperTag(hashTag, hashPhrase, function);
		}
		return tag;
	}
	
	public String getHashTag() {
		if(hashTag == null) {
			hashTag = tag.getHashTag();
		}
		return hashTag;
	}
	
	public String getHashPhrase() {
		if(hashPhrase == null) {
			hashPhrase = tag.getHashPhrase();
		}
		return hashPhrase;
	}
	
	public String getFunction() {
		if(function == null) {
			function = tag.getFunction();
		}
		return function;
	}
	
	public Bitmap getPhoto() {
		return photo;
	}
	
	public boolean getSeenStatus() {
		return seen;
	}
	
	public void launch(Context context, Messenger messenger, Integer[] flagsArray) {
		getTag().getIntent(context, new Integer[]{Intent.FLAG_ACTIVITY_NEW_TASK}).launch(context, messenger);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(getAddress());
		out.writeLong(getTime());
		out.writeParcelable(getTag(), 0);
	}
	
	public static final Creator<Pound> CREATOR = new Creator<Pound>() {

		@Override
		public Pound createFromParcel(Parcel in) {
			return new Pound(in);
		}

		@Override
		public Pound[] newArray(int size) {
			return new Pound[size];
		}
		
	};

	@Override
	public int compareTo(Pound other) {
		if(time < other.time) {
			return 0;
		}
		return 1;
	}

}
