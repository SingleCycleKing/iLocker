package com.avazu.applocker.view.activity;

import android.content.SharedPreferences;
import android.view.MenuItem;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AesCrypto;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.view.widget.Indicator;
import com.avazu.applocker.view.widget.Keyboard;

import butterknife.InjectView;

public class SetPassword extends BaseActivity {

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;
    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    private SharedPreferences.Editor editor;

    @Override
    protected String setTitle() {
        return "Password";
    }

    @Override
    protected void init() {
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = getSharedPreferences(AppConstant.APP_SETTING, 0).edit();

        mKeyboard.setVibratorEnable(getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));

        mIndicator.setKeyboard(mKeyboard);
        mIndicator.setOnPasswordInputCompleted(new Indicator.OnPasswordInputCompleted() {
            @Override
            public void onPasswordInputCompleted(String password) {
                try {
                    editor.putString(AppConstant.APP_LOCK_PASSWORD, AesCrypto.encrypt(AppConstant.APP_KEY, password));
                    editor.apply();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_set_password;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.out_to_end);
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
}
