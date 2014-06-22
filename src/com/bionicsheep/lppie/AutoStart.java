package com.bionicsheep.lppie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver 
{
    public void onReceive(Context arg0, Intent arg1) 
    {
        Intent intent = new Intent(arg0, PieService.class);
        arg0.startService(intent);
    }
}