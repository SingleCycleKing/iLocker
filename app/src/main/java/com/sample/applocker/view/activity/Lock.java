package com.sample.applocker.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avazu.applocker.R;
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

}
