package com.avazu.applocker.view.activity;

import com.avazu.applocker.R;
import com.avazu.applocker.view.widget.Keyboard;

import butterknife.InjectView;

public class Lock extends BaseActivity {

    @InjectView(R.id.lock_keyboard)
    Keyboard mKeyboard;


    @Override
    protected void init() {
        if (null != getSupportActionBar())
            getSupportActionBar().setTitle("输入密码");

        mKeyboard.setCorrectPassword("1111");
        mKeyboard.setOnPasswordInput(new Keyboard.OnPasswordInput() {
            @Override
            public void onPasswordInput(boolean isCorrected) {
                if (isCorrected) {
                    finish();
                } else mKeyboard.shake();
            }
        });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lock;
    }

}
