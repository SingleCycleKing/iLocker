package com.avazu.applocker.view.fragment;

import android.os.Handler;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.util.DebugLog;
import com.avazu.applocker.view.widget.pattern.PatternView;

import java.util.List;

import butterknife.InjectView;

public class PatternLock extends BaseFragment {

    @InjectView(R.id.pattern_lock)
    PatternView mPatternView;

    @InjectView(R.id.lock_tip)
    TextView tip;

    private Handler handler;

    @Override
    protected void init() {
        handler = new Handler();

        mPatternView.setPathEnable(getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, false));
        mPatternView.setFeedBackEnable(getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));
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
                if (BasicUtil.pattern2String(cells).equals(getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getString(AppConstant.APP_LOCK_PATTERN_PASSWORD, null)))
                    getActivity().finish();
                else {
                    tip.setText(getResources().getString(R.string.pattern_error));
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
            tip.setText("");
            mPatternView.setWrongFlag(false);
            mPatternView.clearPattern();
        }
    };

}
