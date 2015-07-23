package com.avazu.applocker.view.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.avazu.applocker.util.BasicUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class BaseNoActionBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        getWindow().setBackgroundDrawable(BasicUtil.blur(this, BasicUtil.getWallpaper(this), 1));
        ButterKnife.inject(this);
        init();
    }

    protected abstract void init();

    protected abstract int getLayoutID();


}
