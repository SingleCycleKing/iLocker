package com.avazu.applocker.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.view.activity.AppList;
import com.avazu.applocker.view.widget.pattern.PatternView;

import java.util.List;

import butterknife.InjectView;

public class SetPatternPassword extends BaseFragment {

    @InjectView(R.id.pattern_lock)
    PatternView mPatternView;

    private boolean inputCompleted = false;
    private String inputPassword = "";
    private boolean confirmCompleted = false;

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
                if (!inputCompleted) {
                    inputCompleted = true;
                    inputPassword = BasicUtil.pattern2String(cells);
                } else if (inputPassword.equals(BasicUtil.pattern2String(cells)))
                    confirmCompleted = true;

            }
        });
    }

    public void confirm() {
        if (inputCompleted && !confirmCompleted) {
            mPatternView.clearPattern();
        } else if (inputCompleted && confirmCompleted) {
            if (getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).getBoolean(AppConstant.APP_FIRST_OPEN, true)) {
                startActivity(new Intent(getActivity(), AppList.class));
            }
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).edit();
            editor.putString(AppConstant.APP_LOCK_PATTERN_PASSWORD, inputPassword);
            editor.putBoolean(AppConstant.APP_FIRST_OPEN, false);
            editor.apply();
            getActivity().finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pattern_password;
    }
}
