package com.avazu.applocker.view.fragment;


import android.content.SharedPreferences;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.view.widget.passwd.pattern.PatternView;

import java.util.List;

import butterknife.InjectView;

public class SetPatternPassword extends BaseFragment {

    @InjectView(R.id.pattern_lock)
    PatternView mPatternView;

    private SharedPreferences.Editor editor;

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
                editor = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).edit();
                editor.putString(AppConstant.APP_LOCK_PATTERN_PASSWORD, BasicUtil.pattern2String(cells));
                editor.apply();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pattern_password;
    }
}
