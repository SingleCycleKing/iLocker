package com.avazu.applocker.view.fragment;


import android.content.SharedPreferences;
import android.os.Handler;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.view.widget.Indicator;
import com.avazu.applocker.view.widget.Keyboard;

import java.util.List;

import butterknife.InjectView;

public class SetPinPassword extends BaseFragment {

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;
    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    private boolean inputCompleted = false;
    private String inputPassword = "";
    private boolean confirmCompleted = false;
    private Handler handler;
    private String string = "";
    private OnTipChangedListener onTipChangedListener;

    public void setOnTipChangedListener(OnTipChangedListener onTipChangedListener) {
        this.onTipChangedListener = onTipChangedListener;
    }

    @Override
    protected void init() {
        handler = new Handler();
        mKeyboard.setVibratorEnable(getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));

        mIndicator.setKeyboard(mKeyboard);
        mIndicator.setOnPasswordInputCompleted(new Indicator.OnPasswordInputCompleted() {
            @Override
            public void onPasswordInputCompleted(List<Integer> password) {
                if (!inputCompleted) {
                    inputPassword = BasicUtil.passwordToString(password);
                    inputCompleted = true;
                } else if (inputPassword.equals(BasicUtil.passwordToString(password)))
                    confirmCompleted = true;
                else if (!inputPassword.equals(BasicUtil.passwordToString(password))) {
                    mIndicator.isWrong(true);
                    handler.postDelayed(runnable, 2000);
                    string = getResources().getString(R.string.sorry);
                    onTipChangedListener.onTipChanged(string);
                }
            }
        });
    }

    public void confirm() {
        if (inputCompleted && !confirmCompleted) {
            mIndicator.restore();
            string = getResources().getString(R.string.confirm_pin);
            onTipChangedListener.onTipChanged(string);
        } else if (inputCompleted && confirmCompleted) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).edit();
            editor.putString(AppConstant.APP_LOCK_PIN_PASSWORD, inputPassword);
            editor.putBoolean(AppConstant.APP_FIRST_OPEN, false);
            editor.putInt(AppConstant.APP_LOCK_TYPE, AppConstant.APP_LOCK_PIN);
            editor.apply();
            getActivity().setResult(AppConstant.APP_START_SUCCEED);
            getActivity().finish();
        } else {
            string = getString(R.string.pin_less);
            onTipChangedListener.onTipChanged(string);
            mIndicator.isWrong(true);
            handler.postDelayed(runnable, 2000);
        }
    }

    public void clear(){
        mIndicator.restore();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pin_password;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mIndicator.restore();
            mIndicator.isWrong(false);
        }
    };

    public interface OnTipChangedListener {
        void onTipChanged(String tip);
    }
}
