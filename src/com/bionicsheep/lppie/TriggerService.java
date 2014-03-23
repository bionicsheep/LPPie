package com.bionicsheep.lppie;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class TriggerService extends AccessibilityService{

	ImageView detectorArea, background;
	WindowManager wm;
	WindowManager.LayoutParams tparams, bparams, pparams;
	int twidth, theight;
	
	DisplayMetrics metrics = new DisplayMetrics();
	int displayWidth, displayHeight;
	int pWidth, pHeight;

	boolean scanning = true;
	boolean triggered = false;
	int dragY;
	int shadow_threshold;

	Service currentActivity = this;
	Handler handler;
	Toast toast;

	Canvas canvas;
	PieControls pieView;
	Background backgroundView;
	SharedPreferences sp;
	
	Vibrator vibrate;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		
        Toast toast = Toast.makeText(this, "Pie AutoStarted", Toast.LENGTH_SHORT);
        toast.show();
		return START_STICKY;
	}

	@Override
	public void onCreate(){
		sp = getSharedPreferences("app_settings", MODE_PRIVATE);
		detectorArea = new ImageView(this);
		background = new ImageView(this);

		handler = new Handler();

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);		
		wm.getDefaultDisplay().getMetrics(metrics);

		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;
		pWidth = metrics.widthPixels;
		pHeight = metrics.heightPixels;

		pieView = new PieControls(this, sp, metrics.widthPixels);
		backgroundView = new Background(this);

		startTrigger();
		vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	}

	private OnTouchListener triggerTouchListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				triggered = true;
				startBackground();
				startPie();
				pieView.resetColor();
				vibrate.vibrate(5);
			}else if(event.getAction() == MotionEvent.ACTION_UP){				
				switch (pieView.checkForAction(event)){
				case 1:
					performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
					break;
				case 2:
					performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
					break;
				case 3:
					performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
					break;
				}
				vibrate.vibrate(5);
				pieView.resetColor();
				triggered = false;
				scanning = true;
				destruct(1);
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
				pieView.checkForAction(event);
				dragY = (int) -event.getY();
				if(scanning && dragY > shadow_threshold){
					Log.d("pie", "threshold hit");
					scanning = false;
					fadeToDark();
				}
			}
			return true;
		}
	};

	@Override
	public void onDestroy(){
		if(detectorArea != null) { 
			wm.removeView(detectorArea);
		}
		super.onDestroy();
	}

	private void fadeToDark(){
		(new Thread(){
			int n = 0;
			public void run(){

				for(n = 0; n < 200; n++){
					try{
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								background.setBackgroundColor(Color.argb(n, 0, 0, 0));
							}
						});
						sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void startPie(){		
		wm.addView(pieView, bparams);
	}

	private void startTrigger(){
		detectorArea.setOnTouchListener(triggerTouchListener);
		detectorArea.setScaleType(ImageView.ScaleType.FIT_XY);
		twidth = displayWidth / 2;
		theight = 10;
		shadow_threshold = displayHeight / 3;

		tparams = new WindowManager.LayoutParams(
				twidth,
				theight,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
				);

		tparams.gravity = Gravity.CENTER | Gravity.BOTTOM;

		wm.addView(detectorArea, tparams);
	}

	private void startBackground(){
		updateMetrics();

		bparams = new WindowManager.LayoutParams(
				displayWidth,
				displayHeight,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSPARENT
				);
		bparams.gravity = Gravity.CENTER | Gravity.BOTTOM;

		wm.addView(background, bparams);
		background.setBackgroundColor(Color.argb(0, 0, 0, 0));
	}

	private void runOnUiThread(Runnable runnable) {
		handler.post(runnable);
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}
	
	private void updateMetrics(){
		wm.getDefaultDisplay().getMetrics(metrics);
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;
		shadow_threshold = displayHeight / 2;
		pieView.updateDisplayParams(displayWidth, displayHeight);
	}
	
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		destruct(0);
	}
	
	private void destruct(int type){
		if(type == 0){
			if(background != null && triggered){
				wm.removeView(background);
			}
			if(pieView != null && triggered){
				wm.removeView(pieView);
			}
		}else{
			if(background != null){
				wm.removeView(background);
			}
			if(pieView != null){
				wm.removeView(pieView);
			}
		}
	}
}