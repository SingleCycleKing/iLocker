package com.avazu.applocker.view.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.avazu.applocker.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class BaseActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        init();
    }

    protected abstract void init();

    protected abstract int getLayoutID();
}
