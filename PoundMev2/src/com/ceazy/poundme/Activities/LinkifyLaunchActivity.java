package com.ceazy.poundme.Activities;

import com.ceazy.lib.SuperTag.SuperTextAnalyzer;
import com.ceazy.lib.SuperTag.Error.SuperError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

public class LinkifyLaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String data = getIntent().getData().toString();
		parsePhraseAndLaunch(data);
		super.onCreate(savedInstanceState);
	}
	
	protected void parsePhraseAndLaunch(String phrase) {
		SuperTextAnalyzer analyzer = new SuperTextAnalyzer(this);
		analyzer.getFirstTag(phrase).getIntent(this, new Integer[]{Intent.FLAG_ACTIVITY_NEW_TASK}).launch(this, 
				new Messenger(new LaunchHandler()));
	}
	
	class LaunchHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			SuperError error = msg.getData().getParcelable("error");
			if(error == null) {
				finish();
			} else {
				//Error..
			}
			super.handleMessage(msg);
		}
		
	}

}
