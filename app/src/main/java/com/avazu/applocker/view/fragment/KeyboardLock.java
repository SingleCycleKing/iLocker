package com.avazu.applocker.view.fragment;

import android.content.SharedPreferences;
import android.os.Handler;
import android.view.animation.Animation;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.util.DebugLog;
import com.avazu.applocker.view.widget.Indicator;
import com.avazu.applocker.view.widget.Keyboard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.InjectView;

public class KeyboardLock extends BaseFragment {

    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;


    private SharedPreferences sharedPreferences;

    private Handler handler = new Handler();

    private Set<String> unlockedSet;

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
        sharedPreferences = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0);
        mKeyboard.setVibratorEnable(sharedPreferences.getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));

        mIndicator.setKeyboard(mKeyboard);
        mIndicator.setOnPasswordInputCompleted(new Indicator.OnPasswordInputCompleted() {
            @Override
            public void onPasswordInputCompleted(List<Integer> password) {
                try {
                    if (BasicUtil.passwordToString(password).equals(sharedPreferences.getString(AppConstant.APP_LOCK_PIN_PASSWORD, ""))) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        unlockedSet = sharedPreferences.getStringSet(AppConstant.APP_UNLOCKED, new HashSet<String>());
                        unlockedSet.add(getArguments().getString("packageName"));
                        editor.putStringSet(AppConstant.APP_UNLOCKED, unlockedSet);
                        editor.apply();

                        unlockedSet = sharedPreferences.getStringSet(AppConstant.APP_UNLOCKED, new HashSet<String>());
                        for (String s:unlockedSet){
                            DebugLog.e(s+"fuck");
                        }

                        getActivity().finish();
                    } else {
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
            mIndicator.restore();
            mIndicator.isWrong(false);
        }
    };

}
