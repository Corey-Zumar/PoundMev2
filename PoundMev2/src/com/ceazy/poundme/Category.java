package com.ceazy.poundme;

import android.graphics.Bitmap;

public class Category {
	
	String function, name, mostRecent;
	Bitmap icon;
	boolean containsUnseen = false;
	
	public Category(String function, String name, String mostRecent, Bitmap icon) {
		this.function = function;
		this.name = name;
		this.mostRecent = mostRecent;
		this.icon = icon;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMostRecent(String mostRecent) {
		this.mostRecent = mostRecent;
	}
	
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	
	public void setContainsUnseenStatus(boolean containsUnseen) {
		this.containsUnseen = containsUnseen;
	}
	
	public String getFunction() {
		return function;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMostRecent() {
		return mostRecent;
	}
	
	public Bitmap getIcon() {
		return icon;
	}
	
	public boolean getContainsUnseenStatus() {
		return containsUnseen;
	}

}
