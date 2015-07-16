package com.avazu.applocker;


import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import com.avazu.applocker.service.StickyReceiver;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        StickyReceiver mReceiver = new StickyReceiver();
        registerReceiver(mReceiver, intentFilter);
    }


}
