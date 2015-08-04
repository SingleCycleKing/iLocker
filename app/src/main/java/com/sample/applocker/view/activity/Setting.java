package com.sample.applocker.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.sample.applocker.util.AppConstant;
import com.sample.applocker.view.widget.CheckButton;

import butterknife.InjectView;
import butterknife.OnClick;

public class Setting extends BaseActivity implements View.OnClickListener {


    @InjectView(R.id.item_once_check_box)
    CheckButton onceCheck;
    @InjectView(R.id.item_every_check_box)
    CheckButton everyCheck;
    @InjectView(R.id.item_visible)
    CheckBox visibleBox;
    @InjectView(R.id.item_vibrate)
    CheckBox vibrateBox;
    @InjectView(R.id.toolbar_title)
    TextView title;

    @OnClick(R.id.item_arrow)
    void setPassword() {
        startActivityForResult(new Intent(Setting.this, SetPassword.class), 300);
    }

    @InjectView(R.id.item_once_check)
    RelativeLayout onceCheckLayout;
    @InjectView(R.id.item_every_check)
    RelativeLayout everyCheckLayout;
    @InjectView(R.id.item_vibrate_check)
    RelativeLayout vibrate;
    @InjectView(R.id.item_visible_check)
    RelativeLayout visible;
    @InjectView(R.id.hint_divider)
    View line;

    private SharedPreferences settings;


    @Override
    protected void init() {
        if (null != getSupportActionBar()) {
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        title.setText(getResources().getString(R.string.setting_title));

        settings = getSharedPreferences(AppConstant.APP_SETTING, 0);

        initList();

        onceCheckLayout.setOnClickListener(this);
        everyCheckLayout.setOnClickListener(this);
        vibrate.setOnClickListener(this);
        visible.setOnClickListener(this);

        if (settings.getInt(AppConstant.APP_LOCK_OPTION, 0) == AppConstant.APP_LOCK_ONCE)
            onceCheck.setIsChecked(true);
        else everyCheck.setIsChecked(true);


        vibrateBox.setChecked(settings.getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));
        visibleBox.setChecked(settings.getBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, false));


    }

    private void initList() {
        if (AppConstant.APP_LOCK_PATTERN == settings.getInt(AppConstant.APP_LOCK_TYPE, AppConstant.APP_LOCK_PATTERN)) {
            visible.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        } else {
            visible.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {
            initList();
        }
    }


    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = settings.edit();
        switch (v.getId()) {
            case R.id.item_vibrate_check:
                vibrateBox.setChecked(!settings.getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));
                editor.putBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, !settings.getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));
                break;
            case R.id.item_visible_check:
                visibleBox.setChecked(!settings.getBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, false));
                editor.putBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, !settings.getBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, false));
                break;
            case R.id.item_once_check:
                everyCheck.setIsChecked(false);
                onceCheck.setIsChecked(true);
                editor.putInt(AppConstant.APP_LOCK_OPTION, AppConstant.APP_LOCK_ONCE);
                break;
            case R.id.item_every_check:
                everyCheck.setIsChecked(true);
                onceCheck.setIsChecked(false);
                editor.putInt(AppConstant.APP_LOCK_OPTION, AppConstant.APP_LOCK_EVERY_TIME);
                break;

        }
        editor.apply();
    }
}
