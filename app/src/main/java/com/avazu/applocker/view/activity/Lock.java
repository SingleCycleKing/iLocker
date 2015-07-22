package com.avazu.applocker.view.activity;

import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.PagerAdapter;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.view.fragment.KeyboardLock;
import com.avazu.applocker.view.fragment.PatternLock;

import butterknife.InjectView;

public class Lock extends BaseActivity {

    @InjectView(R.id.lock_pager)
    ViewPager mViewPager;

    private PagerAdapter mPagerAdapter;
    private SharedPreferences settings;

    @Override
    protected String setTitle() {
        return "Input Your Password";
    }

    @Override
    protected void init() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        settings = getSharedPreferences(AppConstant.APP_SETTING, 0);
        initPager();
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initPager() {
        if (settings.getBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, false)) {
            PatternLock patternLock = new PatternLock();
            mPagerAdapter.addFragment(patternLock, "Pattern");
        } else {
            KeyboardLock keyboardLock = new KeyboardLock();
            mPagerAdapter.addFragment(keyboardLock, "Keyboard");
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lock;
    }

}
