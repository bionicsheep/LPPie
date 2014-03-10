package com.bionicsheep.lppie;

import com.bionicsheep.lppie.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class Pie extends Activity{
	
	RelativeLayout rl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pie_activity);
		
		rl = (RelativeLayout) findViewById(R.id.pie_container);
		rl.setOnTouchListener(triggerTouchListener);
	}
	
	
	private OnTouchListener triggerTouchListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.d("action: ", "" + event.getAction());
			return true;
		}

	};
	
}
