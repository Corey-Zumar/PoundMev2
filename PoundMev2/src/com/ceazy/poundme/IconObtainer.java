package com.ceazy.poundme;

import android.content.Context;

public class IconObtainer {
	
	Context context;
	
	public IconObtainer(Context context) {
		this.context = context;
	}
	
	private Context getContext() {
		return context;
	}
	
	public int getLargeIconForFunction(String function) {
		return getIconForFunction(function, "_large");
	}
	
	public int getSmallIconForFunction(String function) {
		return getIconForFunction(function, "_small");
	}
	
	private int getIconForFunction(String function, String suffix) {
		String strIdentifier = function.toLowerCase() + suffix;
		return getContext().getResources()
				.getIdentifier(strIdentifier, "drawable", getContext().getPackageName());
	}

}
