package com.sample.applocker.view.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.sample.applocker.util.AppBackground;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class BaseActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.toolbar_title)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(AppBackground.getInstance(this));
        setContentView(getLayoutID());
        ButterKnife.inject(this);
        mTitle.setText(getResources().getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        init();
    }


    protected abstract void init();

    protected abstract int getLayoutID();




}
