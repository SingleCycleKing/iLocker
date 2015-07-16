package com.avazu.applocker.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.avazu.applocker.database.SelectedAppHelper;
import com.avazu.applocker.database.model.AppModel;
import com.avazu.applocker.util.DebugLog;
import com.avazu.applocker.view.activity.MainActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppStartService extends Service {

    private SelectedAppHelper mAppHelper;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAppHelper = new SelectedAppHelper(this);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningApps = activityManager.getRunningAppProcesses();
                if (runningApps != null) {
                    for (ActivityManager.RunningAppProcessInfo runningApp : runningApps) {
                        if (runningApp.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            for (int i = 0; i < mAppHelper.query().size(); i++) {
                                if (mAppHelper.query().get(i).getPackageName().equals(runningApp.processName)) {
                                    Intent newIntent = new Intent();
                                    newIntent.setClass(AppStartService.this, MainActivity.class);
                                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(newIntent);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }, 1000, 5000);

        return START_STICKY;
    }
}
