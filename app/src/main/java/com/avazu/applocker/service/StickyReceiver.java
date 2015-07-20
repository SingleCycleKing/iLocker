package com.avazu.applocker.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.avazu.applocker.util.DebugLog;

public class StickyReceiver extends BroadcastReceiver {
    public StickyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DebugLog.e("Run Sticky Service");
        boolean isServiceRunning = false;
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.avazu.applocker.service.AppStartService".equals(service.service.getClassName())) {
                    isServiceRunning = true;
                    break;
                }
            }
            if (!isServiceRunning) {
                Intent newIntent = new Intent(context, AppStartService.class);
                context.startService(newIntent);
            }

        }
    }
}
