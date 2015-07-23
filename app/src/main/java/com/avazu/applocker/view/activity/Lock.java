package com.avazu.applocker.view.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.PagerAdapter;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.util.UnlockBackground;
import com.avazu.applocker.view.fragment.KeyboardLock;
import com.avazu.applocker.view.fragment.PatternLock;

import butterknife.InjectView;

public class Lock extends BaseActivity {

    @InjectView(R.id.lock_pager)
    ViewPager mViewPager;

    @InjectView(R.id.lock_icon)
    ImageView icon;

    private PagerAdapter mPagerAdapter;
    private SharedPreferences settings;



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
        settings = getSharedPreferences(AppConstant.APP_SETTING, 0);
        initPager();
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initPager() {
        if (AppConstant.APP_LOCK_PATTERN == settings.getInt(AppConstant.APP_LOCK_TYPE, -1)) {
            PatternLock patternLock = new PatternLock();
            mPagerAdapter.addFragment(patternLock, "Pattern");
        } else if (AppConstant.APP_LOCK_PIN == settings.getInt(AppConstant.APP_LOCK_TYPE, -1)) {
            KeyboardLock keyboardLock = new KeyboardLock();
            mPagerAdapter.addFragment(keyboardLock, "Keyboard");
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lock;
    }

}
