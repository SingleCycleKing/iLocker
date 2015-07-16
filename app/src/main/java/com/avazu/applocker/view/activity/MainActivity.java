package com.avazu.applocker.view.activity;

import android.content.Intent;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.avazu.applocker.service.AppStartService;

import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @OnClick(R.id.choose)
    void choose() {
        startActivity(new Intent(MainActivity.this, AppList.class));
        finish();
    }

    @Override
    protected void init() {
        startService(new Intent(this, AppStartService.class));

    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

}
