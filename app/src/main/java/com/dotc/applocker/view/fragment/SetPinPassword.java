package com.dotc.applocker.view.fragment;


import android.content.SharedPreferences;
import android.os.Handler;

import com.avazu.applocker.R;
import com.dotc.applocker.util.AppConstant;
import com.dotc.applocker.util.BasicUtil;
import com.dotc.applocker.view.widget.Indicator;
import com.dotc.applocker.view.widget.Keyboard;

import java.util.List;

import butterknife.InjectView;

public class SetPinPassword extends BaseFragment {

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;
    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    public enum STATUS {NORMAL, INPUT_COMPLETE, CONFIRM, CONFIRM_FAILED, CONFIRM_COMPLETED}

    private STATUS mStatus = STATUS.NORMAL;

    private OnStatusChangedListener onStatusChangedListener;

    private String inputPassword = "";
    private Handler handler;

    public void setOnStatusChangedListener(OnStatusChangedListener onStatusChangedListener) {
        this.onStatusChangedListener = onStatusChangedListener;
    }

    @Override
    protected void init() {
        handler = new Handler();
        mKeyboard.setVibratorEnable(getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));

        mIndicator.setKeyboard(mKeyboard);
        mIndicator.setInteractEnable(true);
        mIndicator.setOnPasswordInputCompleted(new Indicator.OnPasswordInputCompleted() {
            @Override
            public void onPasswordInputCompleted(List<Integer> password) {
                switch (mStatus) {
                    case NORMAL:
                        inputPassword = BasicUtil.passwordToString(password);
                        mStatus = STATUS.INPUT_COMPLETE;
                        onStatusChangedListener.onStatusChanged(mStatus);
                        mIndicator.setInteractEnable(false);
                        break;
                    case CONFIRM:
                        if (inputPassword.equals(BasicUtil.passwordToString(password))) {
                            mStatus = STATUS.CONFIRM_COMPLETED;
                            mIndicator.setInteractEnable(false);
                        } else {
                            setWrong();
                            mStatus = STATUS.CONFIRM_FAILED;
                        }
                        onStatusChangedListener.onStatusChanged(mStatus);
                        break;
                }
            }
        });
    }

    public void confirm() {
        if (mStatus == STATUS.INPUT_COMPLETE) {
            mIndicator.restore();
            mStatus = STATUS.CONFIRM;
            mIndicator.setInteractEnable(true);
            onStatusChangedListener.onStatusChanged(mStatus);
        } else if (mStatus == STATUS.CONFIRM_COMPLETED)
            savePassword();
    }

    private void savePassword() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).edit();
        editor.putString(AppConstant.APP_LOCK_PIN_PASSWORD, inputPassword);
        editor.putBoolean(AppConstant.APP_FIRST_OPEN, false);
        editor.putInt(AppConstant.APP_LOCK_TYPE, AppConstant.APP_LOCK_PIN);
        editor.apply();
        getActivity().setResult(AppConstant.APP_START_SUCCEED);
        getActivity().finish();
    }

    private void setWrong() {
        mIndicator.isWrong(true);
        handler.postDelayed(runnable, 1000);

    }

    public void clear() {
        mIndicator.restore();
        mIndicator.setInteractEnable(true);
        mStatus = STATUS.NORMAL;
        onStatusChangedListener.onStatusChanged(mStatus);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pin_password;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mIndicator.isWrong(false);
            mIndicator.restore();
            if (mStatus == STATUS.CONFIRM_FAILED) {
                mStatus = STATUS.CONFIRM;
                onStatusChangedListener.onStatusChanged(mStatus);
            }
        }
    };


    public interface OnStatusChangedListener {
        void onStatusChanged(STATUS status);
    }
}
