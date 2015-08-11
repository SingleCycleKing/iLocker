package com.sample.applocker.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avazu.applocker.R;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Libraries.CBLogging;
import com.chartboost.sdk.Model.CBError;
import com.sample.applocker.adapter.PagerAdapter;
import com.sample.applocker.util.AppConstant;
import com.sample.applocker.util.BasicUtil;
import com.sample.applocker.util.UnlockBackground;
import com.sample.applocker.view.fragment.KeyboardLock;
import com.sample.applocker.view.fragment.PatternLock;

import butterknife.InjectView;

public class Lock extends BaseActivity {

    @InjectView(R.id.lock_pager)
    ViewPager mViewPager;

    @InjectView(R.id.lock_icon)
    ImageView icon;

    @InjectView(R.id.ic_launcher)
    ImageView appIcon;

    @InjectView(R.id.toolbar_title)
    TextView title;

    private PagerAdapter mPagerAdapter;
    private SharedPreferences sharedPreferences;


    @Override
    protected void init() {
        Chartboost.startWithAppId(this, "557fddf804b0162ea2b37449", "329bddc67a53e9ecdd6f398579eadfdc1d8dae74");
        Chartboost.setLoggingLevel(CBLogging.Level.ALL);
        Chartboost.setDelegate(delegate);
        Chartboost.onCreate(this);


        appIcon.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.lock_title));

        try {
            String appName = getIntent().getStringExtra("packageName");
            icon.setImageDrawable(this.getPackageManager().getApplicationIcon(appName));
            UnlockBackground unlockBackground = new UnlockBackground(BasicUtil.drawableToBitmap(this.getPackageManager().getApplicationIcon(appName)), this);
            getWindow().setBackgroundDrawable(unlockBackground.getBackground());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        sharedPreferences = getSharedPreferences(AppConstant.APP_SETTING, 0);
        initPager();
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initPager() {
        if (AppConstant.APP_LOCK_PATTERN == sharedPreferences.getInt(AppConstant.APP_LOCK_TYPE, -1)) {
            PatternLock patternLock = new PatternLock();
            Bundle bundle = new Bundle();
            bundle.putString("packageName", getIntent().getStringExtra("packageName"));
            patternLock.setArguments(bundle);
            mPagerAdapter.addFragment(patternLock, "Pattern");
        } else if (AppConstant.APP_LOCK_PIN == sharedPreferences.getInt(AppConstant.APP_LOCK_TYPE, -1)) {
            KeyboardLock keyboardLock = new KeyboardLock();
            Bundle bundle = new Bundle();
            bundle.putString("packageName", getIntent().getStringExtra("packageName"));
            keyboardLock.setArguments(bundle);
            mPagerAdapter.addFragment(keyboardLock, "Keyboard");
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lock;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                return true;
            case KeyEvent.KEYCODE_HOME:
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            finish();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onStart() {
        super.onStart();
        Chartboost.onStart(this);
        Chartboost.showInterstitial(CBLocation.LOCATION_LEADERBOARD);
    }

    @Override
    public void onResume() {
        super.onResume();
        Chartboost.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Chartboost.onPause(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Chartboost.onDestroy(this);
    }

    @Override
    public void onBackPressed() {
        if (Chartboost.onBackPressed())
            return;
        else
            super.onBackPressed();
    }

    private ChartboostDelegate delegate = new ChartboostDelegate() {
        String TAG = "fuck";

        @Override
        public boolean shouldRequestInterstitial(String location) {
            Log.i(TAG, "SHOULD REQUEST INTERSTITIAL '" + (location != null ? location : "null"));
            return true;
        }

        @Override
        public boolean shouldDisplayInterstitial(String location) {
            Log.i(TAG, "SHOULD DISPLAY INTERSTITIAL '" + (location != null ? location : "null"));
            return true;
        }

        @Override
        public void didCacheInterstitial(String location) {
            Log.i(TAG, "DID CACHE INTERSTITIAL '" + (location != null ? location : "null"));
        }

        @Override
        public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {
            Log.i(TAG, "DID FAIL TO LOAD INTERSTITIAL '" + (location != null ? location : "null") + " Error: " + error.name());
            Toast.makeText(getApplicationContext(), "INTERSTITIAL '" + location + "' REQUEST FAILED - " + error.name(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didDismissInterstitial(String location) {
            Log.i(TAG, "DID DISMISS INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didCloseInterstitial(String location) {
            Log.i(TAG, "DID CLOSE INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didClickInterstitial(String location) {
            Log.i(TAG, "DID CLICK INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didDisplayInterstitial(String location) {
            Log.i(TAG, "DID DISPLAY INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public boolean shouldRequestMoreApps(String location) {
            Log.i(TAG, "SHOULD REQUEST MORE APPS: " + (location != null ? location : "null"));
            return true;
        }

        @Override
        public boolean shouldDisplayMoreApps(String location) {
            Log.i(TAG, "SHOULD DISPLAY MORE APPS: " + (location != null ? location : "null"));
            return true;
        }

        @Override
        public void didFailToLoadMoreApps(String location, CBError.CBImpressionError error) {
            Log.i(TAG, "DID FAIL TO LOAD MOREAPPS " + (location != null ? location : "null") + " Error: " + error.name());
            Toast.makeText(getApplicationContext(), "MORE APPS REQUEST FAILED - " + error.name(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didCacheMoreApps(String location) {
            Log.i(TAG, "DID CACHE MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didDismissMoreApps(String location) {
            Log.i(TAG, "DID DISMISS MORE APPS " + (location != null ? location : "null"));
        }

        @Override
        public void didCloseMoreApps(String location) {
            Log.i(TAG, "DID CLOSE MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didClickMoreApps(String location) {
            Log.i(TAG, "DID CLICK MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didDisplayMoreApps(String location) {
            Log.i(TAG, "DID DISPLAY MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didFailToRecordClick(String uri, CBError.CBClickError error) {
            Log.i(TAG, "DID FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name());
            Toast.makeText(getApplicationContext(), "FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean shouldDisplayRewardedVideo(String location) {
            Log.i(TAG, String.format("SHOULD DISPLAY REWARDED VIDEO: '%s'", (location != null ? location : "null")));
            return true;
        }

        @Override
        public void didCacheRewardedVideo(String location) {
            Log.i(TAG, String.format("DID CACHE REWARDED VIDEO: '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didFailToLoadRewardedVideo(String location,
                                               CBError.CBImpressionError error) {
            Log.i(TAG, String.format("DID FAIL TO LOAD REWARDED VIDEO: '%s', Error:  %s", (location != null ? location : "null"), error.name()));
            Toast.makeText(getApplicationContext(), String.format("DID FAIL TO LOAD REWARDED VIDEO '%s' because %s", location, error.name()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didDismissRewardedVideo(String location) {
            Log.i(TAG, String.format("DID DISMISS REWARDED VIDEO '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didCloseRewardedVideo(String location) {
            Log.i(TAG, String.format("DID CLOSE REWARDED VIDEO '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didClickRewardedVideo(String location) {
            Log.i(TAG, String.format("DID CLICK REWARDED VIDEO '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didCompleteRewardedVideo(String location, int reward) {
            Log.i(TAG, String.format("DID COMPLETE REWARDED VIDEO '%s' FOR REWARD %d", (location != null ? location : "null"), reward));
        }

        @Override
        public void didDisplayRewardedVideo(String location) {
            Log.i(TAG, String.format("DID DISPLAY REWARDED VIDEO '%s' FOR REWARD", location));
        }

        @Override
        public void willDisplayVideo(String location) {
            Log.i(TAG, String.format("WILL DISPLAY VIDEO '%s", location));
        }

    };
}
