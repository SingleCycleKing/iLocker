package com.avazu.applocker.view.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.AppListAdapter;
import com.avazu.applocker.adapter.SelectedAppAdapter;
import com.avazu.applocker.database.SelectedAppHelper;
import com.avazu.applocker.database.model.AppModel;
import com.avazu.applocker.listener.OnRecyclerItemClickListener;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.view.widget.SectionView;

import java.util.ArrayList;

import butterknife.InjectView;

public class AppList extends BaseActivity {

    @InjectView(R.id.app_list)
    RecyclerView mAppList;

    @InjectView(R.id.selected_app_list)
    RecyclerView mSelectedList;

    @InjectView(R.id.selected_section)
    SectionView mSelectedSection;


    private ArrayList<AppModel> mAppInfoList;
    private ArrayList<AppModel> mSelectedInfoList;
    private SelectedAppAdapter mSelectedAppAdapter;

    @Override
    protected void init() {
        mAppInfoList = BasicUtil.getAllApplication(this);
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SelectedAppHelper mAppHelper = new SelectedAppHelper(this);
        mSelectedSection.setTitle("Have Selected");
        mSelectedInfoList = mAppHelper.query();
        mSelectedAppAdapter = new SelectedAppAdapter(this, mSelectedInfoList);

        mAppList.setLayoutManager(new GridLayoutManager(this, 4));
        mAppList.setItemAnimator(new DefaultItemAnimator());
        mAppList.setAdapter(new AppListAdapter(this, mAppInfoList));
        mAppList.addOnItemTouchListener(new OnRecyclerItemClickListener(this, new OnRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!mSelectedInfoList.contains(mAppInfoList.get(position)))
                    mSelectedInfoList.add(mAppInfoList.get(position));
                else mSelectedInfoList.remove(mAppInfoList.get(position));
                mSelectedAppAdapter.updateData(mSelectedInfoList);
            }
        }));


        mSelectedList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSelectedList.setItemAnimator(new DefaultItemAnimator());
        mSelectedList.setAdapter(mSelectedAppAdapter);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_app_list;
    }

    @Override
    public void finish() {
        super.finish();

    }
}
