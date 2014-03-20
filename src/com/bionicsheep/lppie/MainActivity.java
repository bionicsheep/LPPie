package com.bionicsheep.lppie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity {

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	CheckBoxPreference service_checkbox;
	Activity currentActivity = this;
	Toast toast;


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.main_preference);

		loadValues();
		initializeObjects();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	public void initializeObjects(){
		service_checkbox = (CheckBoxPreference)getPreferenceManager().findPreference("service_checkbox");
		service_checkbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				startPie((Boolean) newValue);
				return true;
			}
		});
		startPie(sp.getBoolean("service_status", false));
	}

	public void startPie(boolean state){
		editor = sp.edit();

		if (state == true) {
			startService(new Intent(currentActivity, TriggerService.class));
			editor.putBoolean("service_status", true);
			toast = Toast.makeText(currentActivity, "Service Running", Toast.LENGTH_SHORT);
			toast.show();
		} else {
			stopService(new Intent(currentActivity, TriggerService.class));
			editor.putBoolean("service_status", false);
			toast = Toast.makeText(currentActivity, "Service Not Running", Toast.LENGTH_SHORT);
			toast.show();
		}

		editor.commit();
	}

	public void loadValues(){
		sp = getSharedPreferences("app_settings", MODE_PRIVATE);

		boolean firstRun = sp.getBoolean("firstrun", true);
		if(firstRun){
			accessServicePrompt();
			enterValues();
		}
	}

	public void enterValues(){
		editor = sp.edit();
		editor.putBoolean("firstrun", false);
		editor.putBoolean("service_status", false);
		editor.putString("p1", "#73000000");
		editor.putString("p2", "#73000000");
		editor.putString("p3", "#73000000");
		editor.putString("primary", "#BFFFFFFF");
		editor.putString("primary_reference", "#73000000");
		editor.putString("secondary_reference", "#BFFFFFFF");
		editor.putString("tertiary_reference", "#BFFFFFFF");
		editor.commit();
	}

	public void accessServicePrompt(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Enable Pie Accessibility");

		// set dialog message
		alertDialogBuilder
		.setMessage("In order for this app to work properly, accessibility services for it " +
				"must be turn on under your devices settings. Turn them to on to get navigation " +
				"functionality.")
				.setCancelable(false)
				.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
						startActivity(intent);
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
