package com.bionicsheep.lppie;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TriggerService extends Service{
	
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	public void onStartCommand(){
		Log.d("calling start command","DEBUG");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
