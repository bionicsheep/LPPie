package com.bionicsheep.lppie;

import android.app.Service;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class TriggerService extends Service{

	ImageView detectorArea, background;
	WindowManager wm;
	WindowManager.LayoutParams tparams, bparams, pparams;
	int twidth, theight;
	int pwidth, pheight;
	int bwidth, bheight;
	
	Display display;
	int displayWidth, displayHeight;
	
	boolean scanning = true;
	int dragY;
	int shadow_threshold;

	Service currentActivity = this;
	Handler handler;
	Toast toast;
	
	Canvas canvas;
	PieControls pieView;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(){
		detectorArea = new ImageView(this);
		background = new ImageView(this);

		handler = new Handler();

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);		
		display = wm.getDefaultDisplay();

		displayWidth = display.getWidth();
		displayHeight = display.getHeight();
		
		pieView = new PieControls(this);

		startTrigger();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private OnTouchListener triggerTouchListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				Log.d("pie:", "bound");
				startBackground();
				startPie();
			}else if(event.getAction() == MotionEvent.ACTION_UP){
				scanning = true;
				wm.removeView(background);
				wm.removeView(pieView);
				Log.d("pie:", "release");
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
				pieView.highLight(event);
				
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
		pwidth = displayWidth;
		pheight = displayHeight;

		pparams = new WindowManager.LayoutParams(
				pwidth,
				pheight,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
				);
		
		wm.addView(pieView, bparams);
	}

	private void startTrigger(){
		detectorArea.setImageResource(R.drawable.detector);
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
		bwidth = displayWidth;
		bheight = displayHeight;

		bparams = new WindowManager.LayoutParams(
				bwidth,
				bheight,
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
}