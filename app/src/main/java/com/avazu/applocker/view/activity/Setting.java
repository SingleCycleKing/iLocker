package com.avazu.applocker.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.SettingAdapter;
import com.avazu.applocker.listener.OnRecyclerItemClickListener;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.view.widget.CheckButton;
import com.avazu.applocker.view.widget.DividerItemDecoration;

import butterknife.InjectView;

public class Setting extends BaseActivity {

    @InjectView(R.id.setting_list)
    RecyclerView mSettingList;

    @InjectView(R.id.item_once_check_box)
    CheckButton onceCheck;
    @InjectView(R.id.item_every_check_box)
    CheckButton everyCheck;

    @InjectView(R.id.item_once_check)
    RelativeLayout onceCheckLayout;
    @InjectView(R.id.item_every_check)
    RelativeLayout everyCheckLayout;

    private SharedPreferences settings;


    @Override
    protected void init() {
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingAdapter mAdapter = new SettingAdapter(this);

        mSettingList.setLayoutManager(new LinearLayoutManager(this));
        mSettingList.setItemAnimator(new DefaultItemAnimator());
        mSettingList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), true, true));
        mSettingList.setAdapter(mAdapter);
        mSettingList.addOnItemTouchListener(new OnRecyclerItemClickListener(this, new OnRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(Setting.this, SetPassword.class));
                        break;
                }
            }
        }));

        settings = getSharedPreferences(AppConstant.APP_SETTING, 0);

        if (settings.getInt(AppConstant.APP_LOCK_OPTION, 0) == AppConstant.APP_LOCK_ONCE)
            onceCheck.setIsChecked(true);
        else everyCheck.setIsChecked(true);

        onceCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                everyCheck.setIsChecked(false);
                onceCheck.setIsChecked(true);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(AppConstant.APP_LOCK_OPTION, AppConstant.APP_LOCK_ONCE);
                editor.apply();
            }
        });

        everyCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                everyCheck.setIsChecked(true);
                onceCheck.setIsChecked(false);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(AppConstant.APP_LOCK_OPTION, AppConstant.APP_LOCK_EVERY_TIME);
                editor.apply();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_setting;
    }
}
