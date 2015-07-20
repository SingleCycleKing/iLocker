package com.avazu.applocker.view.activity;

import android.support.v4.view.ViewPager;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.LockPagerAdapter;
import com.avazu.applocker.view.fragment.KeyboardLock;
import com.avazu.applocker.view.fragment.PatternLock;

import butterknife.InjectView;

public class Lock extends BaseActivity {

    @InjectView(R.id.lock_pager)
    ViewPager mViewPager;

    private LockPagerAdapter mPagerAdapter;

    @Override
    protected String setTitle() {
        return "Input Your Password";
    }

    @Override
    protected void init() {
        mPagerAdapter = new LockPagerAdapter(getSupportFragmentManager());
        initPager();
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initPager() {
        KeyboardLock keyboardLock = new KeyboardLock();
        mPagerAdapter.addFragment(keyboardLock, "Keyboard");
        PatternLock patternLock = new PatternLock();
        mPagerAdapter.addFragment(patternLock, "Pattern");
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lock;
    }

}
