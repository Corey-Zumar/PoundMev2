package com.ceazy.poundme.Activities;

import com.ceazy.poundme.R;
import com.ceazy.poundme.Activities.MainSettingsActivity.PreferenceFrag.UpdateUriStringHandler;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class MainSettingsActivity extends PreferenceActivity {

	static CheckBoxPreference persistentNotifs, prefNotifSound,
			prefNotifVibration;
	static Preference prefSetRingtone, prefContactDeveloper, prefViewTutorial;
	static ListPreference prefSetVibration;
	UpdateUriStringHandler uriStringHandler;
	static SharedPreferences sharedPrefs;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PreferenceFrag()).commit();
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	public static class PreferenceFrag extends PreferenceFragment implements
			OnPreferenceClickListener, OnPreferenceChangeListener {

		String uriString;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setUriStringHandler(new UpdateUriStringHandler());
			initializePreferences();
		}
		
		private void initializePreferences() {
			addPreferencesFromResource(R.xml.main_preferences);
			persistentNotifs = (CheckBoxPreference) findPreference("prefPersistentNotifsBOOL");
			persistentNotifs.setOnPreferenceChangeListener(this);
			prefNotifSound = (CheckBoxPreference) findPreference("prefNotifSoundBOOL");
			prefNotifSound.setOnPreferenceChangeListener(this);
			prefNotifVibration = (CheckBoxPreference) findPreference("prefNotifVibrationBOOL");
			prefNotifVibration.setOnPreferenceChangeListener(this);
			prefSetRingtone = (Preference) findPreference("prefSetRingtone");
			prefSetRingtone.setOnPreferenceClickListener(this);
			prefContactDeveloper = (Preference) findPreference("prefContactDeveloper");
			prefContactDeveloper.setOnPreferenceClickListener(this);
			prefSetVibration = (ListPreference) findPreference("prefSetPattern");
			prefSetVibration.setOnPreferenceChangeListener(this);
			setPatternPrefs(prefSetVibration);
			prefViewTutorial = (Preference) findPreference("prefViewTutorial");
			prefViewTutorial.setOnPreferenceClickListener(this);
		}

		@Override
		public boolean onPreferenceClick(Preference pref) {
			String key = pref.getKey();
			if (key.equals("prefSetRingtone")) {
				launchSetRingtoneAction();
			} else if(key.equals("prefContactDeveloper")) {
				contactDeveloper();
			} else if(key.equals("prefViewTutorial")) {
				
			}
			return true;
		}

		@Override
		public boolean onPreferenceChange(Preference pref, Object obj) {
			String key = pref.getKey();
			if (key.contains("BOOL")) {
				setBooleanPreference(key, (Boolean) obj);
			} else if (key.equals("prefSetPattern")) {
				Editor editor = getSharedPreferences().edit();
				switch (Integer.valueOf(obj.toString())) {
					case 1:
						editor.putString("prefSetPattern", "1");
						break;
					case 2:
						editor.putString("prefSetPattern", "2");
						break;
					case 3:
						editor.putString("prefSetPattern", "3");
						break;
				}
				editor.commit();
			}
			return true;
		}
		
		private SharedPreferences getSharedPreferences() {
			if(sharedPrefs == null) {
				sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			}
			return sharedPrefs;
		}
		
		private void setPatternPrefs(ListPreference patternPref) {
			patternPref.setEntries(new String[]{"Slow", "Medium", "Rapid"});
			patternPref.setEntryValues(new String[]{"1", "2", "3"});
			patternPref.setValueIndex(Integer.valueOf(getSharedPreferences()
					.getString("prefSetPattern", "2")) - 1);
		}

		private void setUriStringHandler(UpdateUriStringHandler uriStringHandler) {
			((MainSettingsActivity) getActivity()).uriStringHandler = uriStringHandler;
		}

		private void setRingtoneUriString(String uriString) {
			this.uriString = uriString;
		}

		private String getRingtoneUriString() {
			return uriString;
		}

		private void setBooleanPreference(String key, boolean value) {
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(getActivity()).edit();
			editor.putBoolean(key, value);
			editor.commit();
		}
		
		private void contactDeveloper() {
			try {
				Intent iContactDev = new Intent(Intent.ACTION_SENDTO, Uri
					.fromParts("mailto", "dev.poundme@gmail.com", null));
				startActivity(iContactDev);
			} catch(ActivityNotFoundException e) {
				//No email client installed error
				e.printStackTrace();
			}
		}

		private void launchSetRingtoneAction() {
			String defaultRingtoneUriString = getRingtoneUriString();
			if (defaultRingtoneUriString == null) {
				String defaultUri = PreferenceManager
						.getDefaultSharedPreferences(getActivity()).getString(
								"ringtoneUriString", "null");
				setRingtoneUriString(defaultUri);
				defaultRingtoneUriString = defaultUri;
			}
			Intent iShowTones = new Intent(
					RingtoneManager.ACTION_RINGTONE_PICKER);
			iShowTones.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
					"Select a ringtone");
			iShowTones.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
					RingtoneManager.TYPE_NOTIFICATION);
			iShowTones.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
					false);
			iShowTones.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
					Uri.parse(defaultRingtoneUriString));
			getActivity().startActivityForResult(iShowTones, 062);
		}

		class UpdateUriStringHandler extends Handler {

			@Override
			public void handleMessage(Message msg) {
				setRingtoneUriString(msg.obj.toString());
				super.handleMessage(msg);
			}

		}

	}

	private UpdateUriStringHandler getUriStringHandler() {
		return uriStringHandler;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			Uri uri = data
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			String uriString = uri.toString();
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			Editor editor = sharedPrefs.edit();
			editor.putString("ringtoneUriString", uriString);
			editor.commit();
			prefSetRingtone.setSummary(" "
					+ RingtoneManager.getRingtone(this, uri).getTitle(this));
			Message msg = Message.obtain();
			msg.obj = uriString;
			getUriStringHandler().sendMessage(msg);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
