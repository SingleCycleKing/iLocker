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
import com.avazu.applocker.listener.OnRecyclerItemClickListener;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.util.CharacterParser;
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
    private SelectedAppHelper mAppHelper;
    private List<SectionedGridRecyclerViewAdapter.Section> mSections;

    @Override
    protected void init() {
        mAppInfoList = BasicUtil.getAllApplication(this);
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppHelper = new SelectedAppHelper(this);
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
        AppListAdapter mAdapter = new AppListAdapter(this, mAppInfoList);
        mSections = new ArrayList<>();


        for (int i = 0; i < mAppInfoList.size(); i++) {
            if (0 == i)
                mSections.add(new SectionedGridRecyclerViewAdapter.Section(0, mAppInfoList.get(i).getSort()));
            else {
                if (!(mAppInfoList.get(i).getSort().equals(mAppInfoList.get(i - 1).getSort())))
                    mSections.add(new SectionedGridRecyclerViewAdapter.Section(i, mAppInfoList.get(i).getSort()));
            }
        }

        SectionedGridRecyclerViewAdapter.Section[] mSortSectionList = new SectionedGridRecyclerViewAdapter.Section[mSections.size()];
        final SectionedGridRecyclerViewAdapter mSectionedAdapter = new SectionedGridRecyclerViewAdapter(this, R.layout.section_view, R.id.section_title, mAppList, mAdapter);
        mSectionedAdapter.setSections(mSections.toArray(mSortSectionList));
        mAppList.setAdapter(mSectionedAdapter);

        mAppList.addOnItemTouchListener(new OnRecyclerItemClickListener(this, new OnRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!mSelectedInfoList.contains(mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position))))
                    mSelectedInfoList.add(mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)));
                else
                    mSelectedInfoList.remove(mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)));
                mSelectedAppAdapter.updateData(mSelectedInfoList);
            }
        }));
    }

    private void getSort(ArrayList<AppModel> mData) {
        mSections = new ArrayList<>();
        for (AppModel mModel : mData) {
            String label = mModel.getLabel().replace(" ", "");
            String phoneme;
            if (BasicUtil.isChinese(label.charAt(0)))
                phoneme = mCharacterParser.getSelling(mModel.getLabel().replace(" ", ""));
            else phoneme = mModel.getLabel().replace(" ", "");

            String mSortString = phoneme.substring(0, 1).toUpperCase();

            if (mSortString.matches("[A-Z]")) {
                mModel.setSort(mSortString.toUpperCase());
            } else {
                mModel.setSort("#");
            }
        }
        Collections.sort(mData, mComparator);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_app_list;
    }

    @Override
    public void finish() {
        super.finish();
        mAppHelper.updateAllData(mSelectedInfoList);
    }
}
