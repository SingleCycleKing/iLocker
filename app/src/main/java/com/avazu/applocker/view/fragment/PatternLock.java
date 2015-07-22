package com.avazu.applocker.view.fragment;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.view.widget.pattern.PatternView;

import java.util.List;

import butterknife.InjectView;

public class PatternLock extends BaseFragment {

    @InjectView(R.id.pattern_lock)
    PatternView mPatternView;

    @Override
    protected void init() {
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
                if (BasicUtil.pattern2String(cells).equals(getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getString(AppConstant.APP_LOCK_PATTERN_PASSWORD, "1111")))
                    getActivity().finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pattern_lock;
    }


}
