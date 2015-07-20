package com.avazu.applocker.view.fragment;

import android.content.SharedPreferences;
import android.view.animation.Animation;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AesCrypto;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.view.widget.Indicator;
import com.avazu.applocker.view.widget.Keyboard;

import butterknife.InjectView;

public class KeyboardLock extends BaseFragment {

    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;

    private SharedPreferences settings;

    @Override
    protected void init() {
        mKeyboard.setListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIndicator.restore();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        settings = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0);
        mKeyboard.setVibratorEnable(settings.getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));

        mIndicator.setKeyboard(mKeyboard);
        mIndicator.setOnPasswordInputCompleted(new Indicator.OnPasswordInputCompleted() {
            @Override
            public void onPasswordInputCompleted(String password) {
                try {
                    if (password.equals(AesCrypto.decrypt(AppConstant.APP_KEY, settings.getString(AppConstant.APP_LOCK_PASSWORD, "1111"))))
                        getActivity().finish();
                    else mKeyboard.shake();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_keyboard_lock;
    }


}
