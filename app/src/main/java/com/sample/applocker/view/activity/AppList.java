package com.sample.applocker.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.avazu.applocker.R;
import com.sample.applocker.adapter.AppListAdapter;
import com.sample.applocker.adapter.SectionedGridRecyclerViewAdapter;
import com.sample.applocker.database.SelectedAppHelper;
import com.sample.applocker.database.model.AppModel;
import com.sample.applocker.listener.OnRecyclerItemClickListener;
import com.sample.applocker.service.AppStartService;
import com.sample.applocker.util.AppConstant;
import com.sample.applocker.util.BasicUtil;
import com.sample.applocker.util.CharacterParser;
import com.sample.applocker.util.PhonemeComparator;
import com.sample.applocker.view.fragment.Dialog;

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
    private Dialog dialog;

    @InjectView(R.id.app_list)
    RecyclerView mAppList;

    @InjectView(R.id.toolbar_setting)
    ImageView setting;

    @InjectView(R.id.start_layout)
    RelativeLayout mStartLayout;

    @OnClick(R.id.start)
    void start() {
        startActivityForResult(new Intent(this, SetPassword.class), AppConstant.APP_START_REQUEST);
    }

    @OnClick(R.id.toolbar_setting)
    void set() {
        startActivity(new Intent(this, Setting.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.APP_START_REQUEST && resultCode == AppConstant.APP_START_SUCCEED) {
            setting.setVisibility(View.VISIBLE);
            mStartLayout.setVisibility(View.GONE);
        }
    }


    @Override
    protected void init() {




        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
        }
        setting.setVisibility(View.VISIBLE);

        startService(new Intent(this, AppStartService.class));

        mAppHelper = new SelectedAppHelper(this);
        initSetting();

        mSelectedInfoList = mAppHelper.queryAll();
        try {
            mAppInfoList = BasicUtil.getAllApplication(this, mSelectedInfoList);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initAppList();

        dialog = new Dialog();
        dialog.setOnDialogClickListener(new Dialog.OnDialogClickListener() {
            @Override
            public void onPositive() {
                finish();
            }

            @Override
            public void onNegative() {
            }
        });
    }

    private void initSetting() {
        SharedPreferences mSettings = getSharedPreferences(AppConstant.APP_SETTING, 0);
        SharedPreferences.Editor editor = mSettings.edit();
        if (mSettings.getBoolean(AppConstant.APP_FIRST_OPEN, true)) {

            String[] strings = getResources().getStringArray(R.array.packageName);

            PackageManager manager = this.getPackageManager();
            ArrayList<AppModel> mModels = new ArrayList<>();
            List<ApplicationInfo> apps = manager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo mInfo : apps) {
                for (String string : strings) {
                    if (mInfo.packageName.equals(string)) {
                        AppModel mModel = new AppModel();
                        mModel.setPackageName(mInfo.packageName);
                        mModel.setLabel(mInfo.loadLabel(manager).toString());
                        mModels.add(mModel);
                    }
                }
            }
            for (AppModel model : mModels) {
                mAppHelper.insert(model);
            }

            setting.setVisibility(View.GONE);
            editor.putInt(AppConstant.APP_LOCK_OPTION, AppConstant.APP_LOCK_ONCE);
            editor.putBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, true);
            editor.putBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, true);
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
                            mAppHelper.delete(mSelectedInfoList.get(i).getPackageName());
                            mSelectedInfoList.remove(i);
                            hasItem = true;
                            break;
                        }
                    }
                    mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)).setIsSelected(!hasItem);
                    if (!hasItem) {
                        mSelectedInfoList.add(mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)));
                        mAppHelper.insert(mAppInfoList.get(mSectionedAdapter.sectionedPositionToPosition(position)));
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_FIRST_OPEN, true)) {
            dialog.show(getFragmentManager(), "Dialog");
        }
        return super.onKeyDown(keyCode, event);
    }


}
