package com.avazu.applocker.view.fragment;


import android.content.SharedPreferences;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AesCrypto;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.view.widget.Indicator;
import com.avazu.applocker.view.widget.Keyboard;

import butterknife.InjectView;

public class SetPinPassword extends BaseFragment {

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;
    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    private SharedPreferences.Editor editor;

    @Override
    protected void init() {

        editor = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).edit();

        mKeyboard.setVibratorEnable(getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));

        mIndicator.setKeyboard(mKeyboard);
        mIndicator.setOnPasswordInputCompleted(new Indicator.OnPasswordInputCompleted() {
            @Override
            public void onPasswordInputCompleted(String password) {
//                try {
//                    editor.putString(AppConstant.APP_LOCK_PIN_PASSWORD, AesCrypto.encrypt(AppConstant.APP_KEY, password));
//                    editor.apply();
//                    getActivity().finish();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pin_password;
    }


}
