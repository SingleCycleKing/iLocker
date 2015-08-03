package com.sample.applocker.view.fragment;

import android.content.SharedPreferences;
import android.os.Handler;

import com.avazu.applocker.R;
import com.sample.applocker.util.AppConstant;
import com.sample.applocker.util.BasicUtil;
import com.sample.applocker.view.widget.pattern.PatternView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.InjectView;

public class PatternLock extends BaseFragment {

    @InjectView(R.id.pattern_lock)
    PatternView mPatternView;

    private Set<String> unlockedSet;

    private Handler handler;
    private SharedPreferences sharedPreferences;

    @Override
    protected void init() {
        handler = new Handler();
        sharedPreferences = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0);

        mPatternView.setPathEnable(sharedPreferences.getBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, false));
        mPatternView.setFeedBackEnable(sharedPreferences.getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));
        mPatternView.setInteractEnable(true);
        mPatternView.setOnPatternListener(new PatternView.OnPatterListener() {
            @Override
            public void onPatterStart() {

            }

            @Override
            public void onPatterCellClear() {

            }

            @Override
            public void onPatterCellAdd(List<PatternView.Cell> cells) {

            }

            @Override
            public void onPatterDetected(List<PatternView.Cell> cells) {
                if (BasicUtil.pattern2String(cells).equals(sharedPreferences.getString(AppConstant.APP_LOCK_PATTERN_PASSWORD, null))) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    unlockedSet = sharedPreferences.getStringSet(AppConstant.APP_UNLOCKED, new HashSet<String>());
                    unlockedSet.add(getArguments().getString("packageName"));
                    editor.putStringSet(AppConstant.APP_UNLOCKED, unlockedSet);
                    editor.apply();
                    getActivity().finish();
                } else {
                    mPatternView.setWrongFlag(true);
                    handler.postDelayed(runnable, 2000);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pattern_lock;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mPatternView.setWrongFlag(false);
            mPatternView.clearPattern();
        }
    };

}
