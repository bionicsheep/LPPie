package com.bionicsheep.lppie;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class TriggerService extends Service{

	ImageView detectorArea;
	WindowManager wm;
	WindowManager.LayoutParams params;
	int dwidth;
	int dheight;
	Toast toast;
	Service currentActivity = this;

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
		detectorArea.setImageResource(R.drawable.detector);
		detectorArea.setOnTouchListener(triggerTouchListener);
		detectorArea.setScaleType(ImageView.ScaleType.FIT_XY);

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);		
		Display display = wm.getDefaultDisplay();
		dwidth = display.getWidth() / 2;
		dheight = 5;

		params = new WindowManager.LayoutParams(
				dwidth,
				dheight,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
				);

		params.gravity = Gravity.CENTER | Gravity.BOTTOM;

		wm.addView(detectorArea, params);
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
				Intent i = new Intent(getBaseContext(), Pie.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				getApplication().startActivity(i);
			}
			return true;
		}
	};

	@Override
	public void onDestroy(){
		if(detectorArea != null) { 
			if(detectorArea != null){
				wm.removeView(detectorArea);
			}
		}
		super.onDestroy();
	}
}