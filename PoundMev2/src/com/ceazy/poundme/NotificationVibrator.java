package com.ceazy.poundme;

import android.content.Context;
import android.os.Vibrator;

public class NotificationVibrator {
	
	Vibrator vibrator;
	Context context;
	int speed;
	long[] speed1 = new long[]{0, 1000, 500, 1000, 500, 1000};
	long[] speed2 = new long[]{0, 500, 500, 500, 500, 500};
	long[] speed3 = new long[]{0, 300, 100, 300, 100, 300};
	
	public NotificationVibrator(Context context, int speed) {
		this.context = context;
		this.speed = speed;
	}
	
	public void vibrate() {
		switch(getSpeed()) {
		case 1:
			getVibrator().vibrate(speed1, -1);
			break;
		case 2:
			getVibrator().vibrate(speed2, -1);
			break;
		case 3:
			getVibrator().vibrate(speed3, -1);
			break;
		}
	}
	
	private Context getContext() {
		return context;
	}
	
	private Integer getSpeed() {
		return speed;
	}
	
	private Vibrator getVibrator() {
		if(vibrator == null) {
			vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		}
		return vibrator;
	}

}
