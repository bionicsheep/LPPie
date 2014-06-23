package com.bionicsheep.lppie;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class PieService extends AccessibilityService{

	private SharedPreferences sp;
	private SharedPreferences cp;
	private WindowManager wm;
	private Resources mResources;

	private DisplayMetrics dm;
	private int mDisplayWidth;
	private int mDisplayHeight;

	private PieView mPieView;

	private View mTriggerView;
	private int mTriggerWidth;
	private int mTriggerHeight;
	private float mTriggerHeightFactor;
	private float mTriggerWidthFactor;

	private Vibrator mVibrator;

	private boolean mPieTriggered;
	private WindowManager.LayoutParams mPieParams;


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast toast = Toast.makeText(this, "Pie AutoStarted", Toast.LENGTH_SHORT);
		toast.show();
		return START_STICKY;
	}

	@Override
	public void onCreate(){
		sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		cp = getSharedPreferences("app_settings", MODE_PRIVATE);
		
		initializePie();
	}
	
	private void initializePie(){
		getDimensions();
		initializePieResources();
		initializeTrigger();
	}

	private void initializePieResources(){
		mPieView = new PieView(this, this);
		mPieView.setSharedPrefs(sp);
		mPieView.setColorPrefs(cp);

		mPieParams = new WindowManager.LayoutParams(
				mDisplayWidth,
				mDisplayHeight,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
				);
	}

	private void initializeTrigger(){
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mTriggerView = new View(this);

		WindowManager.LayoutParams mTriggerParams;
		mTriggerParams = new WindowManager.LayoutParams(
				mTriggerWidth,
				mTriggerHeight,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
				);

		mTriggerParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
		
		wm.addView(mTriggerView, mTriggerParams);
		mTriggerView.setOnTouchListener(mTriggerTouchListener);
	}

	private OnTouchListener mTriggerTouchListener = new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				activatePie();
			}
			else  if(event.getAction() == MotionEvent.ACTION_MOVE){
				mPieView.checkForAction(event);	
			}
			else if(event.getAction() == MotionEvent.ACTION_UP){
				switch(mPieView.checkForAction(event)){
				case 1:
					Log.d("lppie","back");
					performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
					break;
				case 2:
					Log.d("lppie","home");
					performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
					break;
				case 3:
					Log.d("lppie","recents");
					performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
					break;
				}
				deactivatePie();
				mVibrator.vibrate(5);
			}
			return false;
		}

	};

	private void activatePie(){
		wm.addView(mPieView, mPieParams);
		mPieTriggered = true;
	}

	private void deactivatePie(){
		if(mPieView != null && mPieTriggered){
			wm.removeView(mPieView);
		}
		mPieTriggered = false;
	}

	private void getDimensions(){
		dm = new DisplayMetrics();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		mResources = getResources();
		mDisplayWidth = dm.widthPixels;
		mDisplayHeight = dm.heightPixels;

		mTriggerHeightFactor = Float.parseFloat(sp.getString("trigger_height", "1"));
		mTriggerWidthFactor = Float.parseFloat(sp.getString("trigger_width", "1"));

		mTriggerHeight = (int) (mResources.getDimensionPixelSize(R.dimen.trigger_height) * mTriggerHeightFactor);
		mTriggerWidth = (int) (mResources.getDimensionPixelSize(R.dimen.trigger_width) * mTriggerWidthFactor);

		mPieTriggered = false;
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}	

	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		destruct();
	}


	private void destruct(){
		if(mTriggerView != null && mPieTriggered){
			wm.removeView(mTriggerView);
		}
		if(mPieView != null && mPieTriggered){
			wm.removeView(mPieView);
		}
		initializePie();
	}
	
	public void tickSound(){
		mPieView.playSoundEffect(android.view.SoundEffectConstants.CLICK);
	}
}
