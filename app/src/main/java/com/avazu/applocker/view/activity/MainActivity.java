package com.avazu.applocker.view.activity;

import android.content.Intent;

import com.avazu.applocker.R;

public class MainActivity extends BaseActivity {


    @Override
    protected void init() {
        startActivity(new Intent(MainActivity.this, AppList.class));
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

}
