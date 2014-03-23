package com.bionicsheep.lppie;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class ColorActivity extends PreferenceActivity{

	PreferenceActivity currentActivity;
	String m_Text;

	SharedPreferences sp;
	SharedPreferences.Editor editor;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("app_settings", MODE_PRIVATE);
		currentActivity = this;
		addPreferencesFromResource(R.layout.settings);
		((ColorPickerPreference)findPreference("color1")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				editor = sp.edit();
				Log.d("new color","" + (ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)))));
				editor.putString("primary_reference", (ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)))));
				editor.commit();
				return true;
			}

		});

		((ColorPickerPreference)findPreference("color2")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				editor = sp.edit();
				editor.putString("secondary_reference", (ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)))));
				editor.commit();
				return true;
			}

		});

		((ColorPickerPreference)findPreference("color3")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				editor = sp.edit();
				editor.putString("tertiary_reference", (ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)))));
				editor.commit();
				return true;
			}

		});

		Preference myPref = (Preference) findPreference("color_reset");
		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				return true;
			}
		});

		sp = getSharedPreferences("app_settings", MODE_PRIVATE);
		currentActivity = this;
	}


}