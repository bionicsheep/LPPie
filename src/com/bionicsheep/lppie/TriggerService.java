package com.bionicsheep.lppie;

import android.app.Service;
import android.content.Intent;
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

	ImageView detectorArea, background, pieOutline, temp;
	WindowManager wm;
	WindowManager.LayoutParams tparams, bparams, pparams;
	int twidth, theight;
	int bwidth, bheight;
	int pwidth, pheight;
	Toast toast;
	boolean active = false;
	boolean scanning = true;
	Service currentActivity = this;
	int dragY;
	int shadow_threshold;
	Display display;

	Handler handler;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onCreate(){
		detectorArea = new ImageView(this);
		background = new ImageView(this);
		pieOutline = new ImageView(this);

		handler = new Handler();

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);		
		display = wm.getDefaultDisplay();

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
				active = true;
				Log.d("pie:", "bound");
				startBackground();
				startPie();
			}else if(event.getAction() == MotionEvent.ACTION_UP){
				active = false;
				wm.removeView(background);
				wm.removeView(pieOutline);
				scanning = true;
				Log.d("pie:", "release");
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
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

	public void fadeToDark(){

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

	@SuppressWarnings("deprecation")
	public void startPie(){
		pieOutline.setImageResource(R.drawable.pie);
		pieOutline.setScaleType(ImageView.ScaleType.FIT_XY);

		pwidth = display.getWidth();
		pheight = pwidth / 2;

		pparams = new WindowManager.LayoutParams(
				pwidth,
				pheight,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
				);

		pparams.gravity = Gravity.CENTER | Gravity.BOTTOM;
		wm.addView(pieOutline, pparams);
	}

	@SuppressWarnings("deprecation")
	public void startTrigger(){
		detectorArea.setImageResource(R.drawable.detector);
		detectorArea.setOnTouchListener(triggerTouchListener);
		detectorArea.setScaleType(ImageView.ScaleType.FIT_XY);
		twidth = display.getWidth() / 2;
		theight = 5;
		shadow_threshold = display.getHeight() / 3;

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

	@SuppressWarnings("deprecation")
	public void startBackground(){
		bwidth = display.getWidth();
		bheight = display.getHeight();

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