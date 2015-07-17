package com.avazu.applocker.view.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.SettingAdapter;

import butterknife.InjectView;

public class Setting extends BaseActivity {

    @InjectView(R.id.setting_list)
    RecyclerView mSettingList;

    @Override
    protected String setTitle() {
        return "App Lock Setting";
    }

    @Override
    protected void init() {
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSettingList.setLayoutManager(new LinearLayoutManager(this));
        mSettingList.setItemAnimator(new DefaultItemAnimator());
        mSettingList.setAdapter(new SettingAdapter(this));
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
        overridePendingTransition(R.anim.hold, R.anim.out_to_end);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_setting;
    }
}
