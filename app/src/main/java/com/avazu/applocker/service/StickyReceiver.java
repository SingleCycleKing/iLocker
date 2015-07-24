package com.avazu.applocker.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.avazu.applocker.util.AppConstant;

import java.util.HashSet;

public class StickyReceiver extends BroadcastReceiver {
    public StickyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isServiceRunning = false;
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
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            SharedPreferences.Editor editor = context.getSharedPreferences(AppConstant.APP_SETTING, 0).edit();
            editor.putStringSet(AppConstant.APP_UNLOCKED, new HashSet<String>());
            editor.apply();
        }
    }
}
