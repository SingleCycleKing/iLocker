package com.avazu.applocker.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.AppListAdapter;
import com.avazu.applocker.adapter.SectionedGridRecyclerViewAdapter;
import com.avazu.applocker.database.SelectedAppHelper;
import com.avazu.applocker.database.model.AppModel;
import com.avazu.applocker.listener.OnRecyclerItemClickListener;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.util.CharacterParser;
import com.avazu.applocker.util.PhonemeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class AppList extends BaseActivity {

    private ArrayList<AppModel> mAppInfoList;
    private ArrayList<AppModel> mSelectedInfoList;
    private CharacterParser mCharacterParser;
    private PhonemeComparator mComparator;
    private SelectedAppHelper mAppHelper;
    private List<SectionedGridRecyclerViewAdapter.Section> mSections;
    private SharedPreferences.Editor editor;

    @InjectView(R.id.app_list)
    RecyclerView mAppList;

    @InjectView(R.id.toolbar_setting)
    ImageView setting;

    @InjectView(R.id.start_layout)
    RelativeLayout mStartLayout;

    @OnClick(R.id.start)
    void start() {
        startActivity(new Intent(this, SetPassword.class));
        finish();
        overridePendingTransition(R.anim.in_from_end, R.anim.hold);
    }

    @OnClick(R.id.toolbar_setting)
    void set() {
        startActivity(new Intent(this, Setting.class));
        overridePendingTransition(R.anim.in_from_end, R.anim.hold);
    }

    @Override
    protected String setTitle() {
        return "App Lock";
    }

    @Override
    protected void init() {
        setting.setVisibility(View.VISIBLE);

        initSetting();

        mAppHelper = new SelectedAppHelper(this);
        mSelectedInfoList = mAppHelper.queryAll();
        mAppInfoList = BasicUtil.getAllApplication(this, mSelectedInfoList);

        initAppList();
    }

    private void initSetting() {
        SharedPreferences mSettings = getSharedPreferences(AppConstant.APP_SETTING, 0);
        editor = mSettings.edit();
        if (mSettings.getBoolean(AppConstant.APP_FIRST_OPEN, true)) {
            setting.setVisibility(View.GONE);
            mStartLayout.setVisibility(View.VISIBLE);

        }
        editor.apply();
    }

    private void initAppList() {
        mAppList.setLayoutManager(new GridLayoutManager(this, 4));
        mAppList.setItemAnimator(new DefaultItemAnimator());
        mCharacterParser = CharacterParser.getInstance();
        mComparator = new PhonemeComparator();

        getSort(mAppInfoList);
        final AppListAdapter mAdapter = new AppListAdapter(this, mAppInfoList);
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
        mAppList.addOnItemTouchListener(new OnRecyclerItemClickListener(this, new OnRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (-1 != mSectionedAdapter.sectionedPositionToPosition(position)) {
                    boolean hasItem = false;
                    for (int i = 0; i < mSelectedInfoList.size(); i++) {
                        if (mSelectedInfoList.get(i).getPackageName().equals(mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)).getPackageName())) {
                            mSelectedInfoList.remove(i);
                            hasItem = true;
                            break;
                        }
                    }
                    mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)).setIsSelected(!hasItem);
                    if (!hasItem) {
                        mSelectedInfoList.add(mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)));
                    }
                    mAdapter.updateData(mAppInfoList);
                }
            }
        }));
        mAppList.setAdapter(mSectionedAdapter);
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
