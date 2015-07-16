package com.avazu.applocker.view.activity;

import android.content.Intent;

import com.avazu.applocker.R;
import com.avazu.applocker.service.AppStartService;

public class MainActivity extends BaseActivity {


    @Override
    protected void init() {
        startService(new Intent(this, AppStartService.class));
        startActivity(new Intent(MainActivity.this, AppList.class));
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

}
