package com.sample.applocker.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.sample.applocker.database.SelectedAppHelper;
import com.sample.applocker.database.model.AppModel;
import com.sample.applocker.util.AppConstant;
import com.sample.applocker.view.activity.Lock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class AppStartService extends Service {

    private SelectedAppHelper mAppHelper;
    private SharedPreferences sharedPreferences;
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
                unlockedSet = sharedPreferences.getStringSet(AppConstant.APP_UNLOCKED, new HashSet<String>());
                if (!sharedPreferences.getBoolean(AppConstant.APP_FIRST_OPEN, true)) {
                    getRunningApps();
                }
            }
        }, 1000, 1000);
        return START_STICKY;
    }

    private void getRunningApps() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = activityManager.getRunningAppProcesses();
        if (runningApps != null) {
            for (ActivityManager.RunningAppProcessInfo runningApp : runningApps) {
                if (runningApp.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    getData(runningApp);
                    break;
                }
            }
        }
    }

    private void getData(ActivityManager.RunningAppProcessInfo runningApp) {
        if (!runningApp.processName.equals(sharedPreferences.getString(AppConstant.APP_CURRENT, "")) && AppConstant.APP_LOCK_EVERY_TIME == sharedPreferences.getInt(AppConstant.APP_LOCK_OPTION, AppConstant.APP_LOCK_ONCE)) {
            unlockedSet.clear();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(AppConstant.APP_UNLOCKED, unlockedSet);
            editor.apply();
        }
        ArrayList<AppModel> mModels = mAppHelper.queryAll();
        for (int i = 0; i < mModels.size(); i++) {
            if (mModels.get(i).getPackageName().equals(runningApp.processName)) {
                init(runningApp);
                break;
            }
        }
    }

    private void init(ActivityManager.RunningAppProcessInfo runningApp) {
        boolean unlocked = false;
        for (String name : unlockedSet) {
            if (name.equals(runningApp.processName)) {
                unlocked = true;
                break;
            }
        }
        if (!unlocked) {
            Intent newIntent = new Intent();
            newIntent.setClass(AppStartService.this, Lock.class);
            newIntent.putExtra("packageName", runningApp.processName);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AppConstant.APP_CURRENT, runningApp.processName);
            editor.apply();
            startActivity(newIntent);
        }
    }
}
