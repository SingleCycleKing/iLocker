package com.avazu.applocker.view.fragment;

import android.content.SharedPreferences;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.view.widget.Indicator;
import com.avazu.applocker.view.widget.Keyboard;

import java.util.List;

import butterknife.InjectView;

public class KeyboardLock extends BaseFragment {

    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;

    @InjectView(R.id.lock_tip)
    TextView tip;

    private SharedPreferences settings;

    private Handler handler = new Handler();

    @Override
    protected void init() {

        handler = new Handler();

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
            public void onPasswordInputCompleted(List<Integer> password) {
                try {
                    if (BasicUtil.passwordToString(password).equals(settings.getString(AppConstant.APP_LOCK_PIN_PASSWORD, "")))
                        getActivity().finish();
                    else {
                        tip.setText(getResources().getString(R.string.pin_error));
                        mIndicator.isWrong(true);
                        handler.postDelayed(runnable, 2000);
                    }
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

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tip.setText("");
            mIndicator.restore();
            mIndicator.isWrong(false);
        }
    };

}
