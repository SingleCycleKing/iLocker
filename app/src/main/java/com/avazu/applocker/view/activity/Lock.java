package com.avazu.applocker.view.activity;

import android.view.animation.Animation;

import com.avazu.applocker.R;
import com.avazu.applocker.view.widget.Indicator;
import com.avazu.applocker.view.widget.Keyboard;

import butterknife.InjectView;

public class Lock extends BaseActivity {

    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;

    @InjectView(R.id.lock_indicator)
    Indicator mIndicator;


    @Override
    protected String setTitle() {
        return "Input Your Password";
    }

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

        mIndicator.setKeyboard(mKeyboard);
        mIndicator.setOnPasswordInputCompleted(new Indicator.OnPasswordInputCompleted() {
            @Override
            public void onPasswordInputCompleted(String password) {
                if (password.equals("1111")) finish();
                else mKeyboard.shake();
            }
        });

    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lock;
    }

}
