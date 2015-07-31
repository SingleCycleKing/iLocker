package com.dotc.applocker.view.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.avazu.applocker.R;
import com.dotc.applocker.adapter.PagerAdapter;
import com.dotc.applocker.util.AppConstant;
import com.dotc.applocker.util.BasicUtil;
import com.dotc.applocker.util.UnlockBackground;
import com.dotc.applocker.view.fragment.KeyboardLock;
import com.dotc.applocker.view.fragment.PatternLock;

import butterknife.InjectView;

public class Lock extends BaseActivity {

    @InjectView(R.id.lock_pager)
    ViewPager mViewPager;

    @InjectView(R.id.lock_icon)
    ImageView icon;

    private PagerAdapter mPagerAdapter;
    private SharedPreferences sharedPreferences;


    @Override
    protected void init() {
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

}
