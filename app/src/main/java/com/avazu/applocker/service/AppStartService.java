package com.avazu.applocker.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.avazu.applocker.database.SelectedAppHelper;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.view.activity.Lock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class AppStartService extends Service {

    private SelectedAppHelper mAppHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Set<String> unlockedSet;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        mAppHelper = new SelectedAppHelper(this);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences(AppConstant.APP_SETTING, 0);
                editor = sharedPreferences.edit();
                unlockedSet = sharedPreferences.getStringSet(AppConstant.APP_UNLOCKED, new HashSet<String>());
                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (!sharedPreferences.getBoolean(AppConstant.APP_FIRST_OPEN, true)) {
                    List<ActivityManager.RunningAppProcessInfo> runningApps = activityManager.getRunningAppProcesses();
                    if (runningApps != null) {
                        for (ActivityManager.RunningAppProcessInfo runningApp : runningApps) {
                            if (runningApp.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                for (int i = 0; i < mAppHelper.queryAll().size(); i++) {
                                    if (mAppHelper.queryAll().get(i).getPackageName().equals(runningApp.processName)) {
                                        boolean unlocked = false;
                                        for (String name : unlockedSet) {
                                            if (name.equals(runningApp.processName)) {
                                                unlocked = true;
                                                break;
                                            }
                                        }

                                        if (!unlocked) {
                                            if (AppConstant.APP_LOCK_EVERY_TIME == sharedPreferences.getInt(AppConstant.APP_LOCK_OPTION, AppConstant.APP_LOCK_ONCE)) {
                                                unlockedSet.clear();
                                                editor.putStringSet(AppConstant.APP_UNLOCKED, unlockedSet);
                                                editor.apply();
                                            }
                                            Intent newIntent = new Intent();
                                            newIntent.setClass(AppStartService.this, Lock.class);
                                            newIntent.putExtra("packageName", runningApp.processName);
                                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(newIntent);
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }, 1000, 1000);
        return START_STICKY;
    }
}
