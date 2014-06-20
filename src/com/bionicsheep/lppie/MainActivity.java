package com.bionicsheep.lppie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity {

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	Preference service_checkbox;
	Activity currentActivity = this;
	Toast toast;


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.main_preference);

		loadValues();
		initializeObjects();
		
		printShiz(); //delete source
	}
	
	private void printShiz(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Log.d("Lppie","xdpi: " + dm.xdpi);
		Log.d("Lppie","ydpi: " + dm.ydpi);
		Log.d("Lppie","densityDpi: " + dm.densityDpi);
	}

	@SuppressWarnings("deprecation")
	public void initializeObjects(){
		service_checkbox = (Preference)getPreferenceManager().findPreference("service_checkbox");
		service_checkbox.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivity(intent);
				return false;
			}
		});
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
