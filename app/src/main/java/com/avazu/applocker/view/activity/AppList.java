package com.avazu.applocker.view.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.AppListAdapter;
import com.avazu.applocker.util.BasicUtil;

import butterknife.InjectView;

public class AppList extends BaseActivity {

    @InjectView(R.id.app_list)
    RecyclerView mRecyclerView;

    @Override
    protected void init() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new AppListAdapter(this, BasicUtil.getAllApplication(this)));
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_app_list;
    }
}
