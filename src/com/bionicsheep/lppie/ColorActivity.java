package com.bionicsheep.lppie;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

		this.getActionBar().setDisplayHomeAsUpEnabled(true);

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

		sp = getSharedPreferences("app_settings", MODE_PRIVATE);
		currentActivity = this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_reset:
			((ColorPickerPreference)findPreference("color1")).onColorChanged(0x73000000);
			((ColorPickerPreference)findPreference("color2")).onColorChanged(0xbfffffff);
			((ColorPickerPreference)findPreference("color3")).onColorChanged(0xbfffffff);
			break;

		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}