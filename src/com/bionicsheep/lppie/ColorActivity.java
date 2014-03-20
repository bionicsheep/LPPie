package com.bionicsheep.lppie;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

public class ColorActivity extends PreferenceActivity{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.color_preference);
		
		setClickListener();
	}
	
	
	public void setClickListener(){
		@SuppressWarnings("deprecation")
		Preference myPref = (Preference) findPreference("mainColor");
		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				return true;
			}
		});
	}


}
