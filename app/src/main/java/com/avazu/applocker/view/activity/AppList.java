package com.avazu.applocker.view.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.AppListAdapter;
import com.avazu.applocker.adapter.SectionedGridRecyclerViewAdapter;
import com.avazu.applocker.adapter.SelectedAppAdapter;
import com.avazu.applocker.database.SelectedAppHelper;
import com.avazu.applocker.database.model.AppModel;
import com.avazu.applocker.database.model.SortModel;
import com.avazu.applocker.listener.OnRecyclerItemClickListener;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.util.CharacterParser;
import com.avazu.applocker.util.DebugLog;
import com.avazu.applocker.util.PhonemeComparator;
import com.avazu.applocker.view.widget.SectionView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private CharacterParser mCharacterParser;
    private PhonemeComparator mComparator;
    private ArrayList<SortModel> mDataSortList;
    private List<SectionedGridRecyclerViewAdapter.Section> mSections;

    @Override
    protected void init() {
        mAppInfoList = BasicUtil.getAllApplication(this);
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SelectedAppHelper mAppHelper = new SelectedAppHelper(this);
        mSelectedSection.setTitle("Have Selected");
        mSelectedInfoList = mAppHelper.query();
        mSelectedAppAdapter = new SelectedAppAdapter(this, mSelectedInfoList);

        initAppList();

        mSelectedList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSelectedList.setItemAnimator(new DefaultItemAnimator());
        mSelectedList.setAdapter(mSelectedAppAdapter);
    }

    private void initAppList() {
        mAppList.setLayoutManager(new GridLayoutManager(this, 4));
        mAppList.setItemAnimator(new DefaultItemAnimator());
        mCharacterParser = CharacterParser.getInstance();
        mComparator = new PhonemeComparator();

        getSort(mAppInfoList);
        AppListAdapter mAdapter = new AppListAdapter(this, mDataSortList);
        mSections = new ArrayList<>();



        for (int i = 0; i < mDataSortList.size(); i++) {
            if (0 == i)
                mSections.add(new SectionedGridRecyclerViewAdapter.Section(0, mDataSortList.get(i).getSort()));
            else {
                if (!(mDataSortList.get(i).getSort().equals(mDataSortList.get(i - 1).getSort())))
                    mSections.add(new SectionedGridRecyclerViewAdapter.Section(i, mDataSortList.get(i).getSort()));
            }
        }

        SectionedGridRecyclerViewAdapter.Section[] mSortSectionList = new SectionedGridRecyclerViewAdapter.Section[mSections.size()];
        SectionedGridRecyclerViewAdapter mSectionedAdapter = new SectionedGridRecyclerViewAdapter(this, R.layout.section_view, R.id.section_title, mAppList, mAdapter);
        mSectionedAdapter.setSections(mSections.toArray(mSortSectionList));
        mAppList.setAdapter(mSectionedAdapter);

        mAppList.addOnItemTouchListener(new OnRecyclerItemClickListener(this, new OnRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!mSelectedInfoList.contains(mAppInfoList.get(position)))
                    mSelectedInfoList.add(mAppInfoList.get(position));
                else mSelectedInfoList.remove(mAppInfoList.get(position));
                mSelectedAppAdapter.updateData(mSelectedInfoList);
            }
        }));
    }

    private void getSort(ArrayList<AppModel> mData) {
        mDataSortList = new ArrayList<>();
        mSections = new ArrayList<>();
        for (AppModel mModel : mData) {
            SortModel mSort = new SortModel();
            mSort.setValues(mModel.getLabel());
            mSort.setPackageName(mModel.getPackageName());
            String label = mModel.getLabel().replace(" ", "");
            String phoneme;
            if (BasicUtil.isChinese(label.charAt(0)))
                phoneme = mCharacterParser.getSelling(mModel.getLabel().replace(" ",""));
            else phoneme = mModel.getLabel().replace(" ","");

            String mSortString = phoneme.substring(0, 1).toUpperCase();

            if (mSortString.matches("[A-Z]")) {
                mSort.setSort(mSortString.toUpperCase());
            } else {
                mSort.setSort("#");
            }
            mDataSortList.add(mSort);
        }
        Collections.sort(mDataSortList, mComparator);
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
