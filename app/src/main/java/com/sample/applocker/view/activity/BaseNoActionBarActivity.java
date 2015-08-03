package com.sample.applocker.view.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sample.applocker.util.AppBackground;

import butterknife.ButterKnife;

public abstract class BaseNoActionBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        getWindow().setBackgroundDrawable(AppBackground.getInstance(this));
        ButterKnife.inject(this);
        init();
    }

    protected abstract void init();

    protected abstract int getLayoutID();


}
