package com.sample.applocker.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avazu.applocker.R;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Libraries.CBLogging;
import com.chartboost.sdk.Model.CBError;
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
        Chartboost.startWithAppId(this, "557fddf804b0162ea2b37449", "329bddc67a53e9ecdd6f398579eadfdc1d8dae74");
        Chartboost.setLoggingLevel(CBLogging.Level.ALL);
        Chartboost.setDelegate(delegate);
        Chartboost.onCreate(this);



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

    @Override
    public void onStart() {
        super.onStart();
        Chartboost.onStart(this);
        Chartboost.showInterstitial(CBLocation.LOCATION_LEADERBOARD);
    }

    @Override
    public void onResume() {
        super.onResume();
        Chartboost.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Chartboost.onPause(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Chartboost.onDestroy(this);
    }

    @Override
    public void onBackPressed() {
        // If an interstitial is on screen, close it. Otherwise continue as normal.
        if (Chartboost.onBackPressed())
            return;
        else
            super.onBackPressed();
    }

    private ChartboostDelegate delegate = new ChartboostDelegate() {
        String TAG = "fuck";

        @Override
        public boolean shouldRequestInterstitial(String location) {
            Log.i(TAG, "SHOULD REQUEST INTERSTITIAL '" + (location != null ? location : "null"));
            return true;
        }

        @Override
        public boolean shouldDisplayInterstitial(String location) {
            Log.i(TAG, "SHOULD DISPLAY INTERSTITIAL '" + (location != null ? location : "null"));
            return true;
        }

        @Override
        public void didCacheInterstitial(String location) {
            Log.i(TAG, "DID CACHE INTERSTITIAL '" + (location != null ? location : "null"));
        }

        @Override
        public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {
            Log.i(TAG, "DID FAIL TO LOAD INTERSTITIAL '" + (location != null ? location : "null") + " Error: " + error.name());
            Toast.makeText(getApplicationContext(), "INTERSTITIAL '" + location + "' REQUEST FAILED - " + error.name(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didDismissInterstitial(String location) {
            Log.i(TAG, "DID DISMISS INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didCloseInterstitial(String location) {
            Log.i(TAG, "DID CLOSE INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didClickInterstitial(String location) {
            Log.i(TAG, "DID CLICK INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didDisplayInterstitial(String location) {
            Log.i(TAG, "DID DISPLAY INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public boolean shouldRequestMoreApps(String location) {
            Log.i(TAG, "SHOULD REQUEST MORE APPS: " + (location != null ? location : "null"));
            return true;
        }

        @Override
        public boolean shouldDisplayMoreApps(String location) {
            Log.i(TAG, "SHOULD DISPLAY MORE APPS: " + (location != null ? location : "null"));
            return true;
        }

        @Override
        public void didFailToLoadMoreApps(String location, CBError.CBImpressionError error) {
            Log.i(TAG, "DID FAIL TO LOAD MOREAPPS " + (location != null ? location : "null") + " Error: " + error.name());
            Toast.makeText(getApplicationContext(), "MORE APPS REQUEST FAILED - " + error.name(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didCacheMoreApps(String location) {
            Log.i(TAG, "DID CACHE MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didDismissMoreApps(String location) {
            Log.i(TAG, "DID DISMISS MORE APPS " + (location != null ? location : "null"));
        }

        @Override
        public void didCloseMoreApps(String location) {
            Log.i(TAG, "DID CLOSE MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didClickMoreApps(String location) {
            Log.i(TAG, "DID CLICK MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didDisplayMoreApps(String location) {
            Log.i(TAG, "DID DISPLAY MORE APPS: " + (location != null ? location : "null"));
        }

        @Override
        public void didFailToRecordClick(String uri, CBError.CBClickError error) {
            Log.i(TAG, "DID FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name());
            Toast.makeText(getApplicationContext(), "FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean shouldDisplayRewardedVideo(String location) {
            Log.i(TAG, String.format("SHOULD DISPLAY REWARDED VIDEO: '%s'", (location != null ? location : "null")));
            return true;
        }

        @Override
        public void didCacheRewardedVideo(String location) {
            Log.i(TAG, String.format("DID CACHE REWARDED VIDEO: '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didFailToLoadRewardedVideo(String location,
                                               CBError.CBImpressionError error) {
            Log.i(TAG, String.format("DID FAIL TO LOAD REWARDED VIDEO: '%s', Error:  %s", (location != null ? location : "null"), error.name()));
            Toast.makeText(getApplicationContext(), String.format("DID FAIL TO LOAD REWARDED VIDEO '%s' because %s", location, error.name()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didDismissRewardedVideo(String location) {
            Log.i(TAG, String.format("DID DISMISS REWARDED VIDEO '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didCloseRewardedVideo(String location) {
            Log.i(TAG, String.format("DID CLOSE REWARDED VIDEO '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didClickRewardedVideo(String location) {
            Log.i(TAG, String.format("DID CLICK REWARDED VIDEO '%s'", (location != null ? location : "null")));
        }

        @Override
        public void didCompleteRewardedVideo(String location, int reward) {
            Log.i(TAG, String.format("DID COMPLETE REWARDED VIDEO '%s' FOR REWARD %d", (location != null ? location : "null"), reward));
        }

        @Override
        public void didDisplayRewardedVideo(String location) {
            Log.i(TAG, String.format("DID DISPLAY REWARDED VIDEO '%s' FOR REWARD", location));
        }

        @Override
        public void willDisplayVideo(String location) {
            Log.i(TAG, String.format("WILL DISPLAY VIDEO '%s", location));
        }

    };
}
