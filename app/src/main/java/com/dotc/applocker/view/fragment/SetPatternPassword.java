package com.dotc.applocker.view.fragment;

import android.content.SharedPreferences;
import android.os.Handler;

import com.avazu.applocker.R;
import com.dotc.applocker.util.AppConstant;
import com.dotc.applocker.util.BasicUtil;
import com.dotc.applocker.view.widget.pattern.PatternView;

import java.util.List;

import butterknife.InjectView;

public class SetPatternPassword extends BaseFragment {

    @InjectView(R.id.pattern_lock)
    PatternView mPatternView;

    private String inputPassword = "";
    private Handler handler;

    public enum STATUS {NORMAL, INPUT_LESS, INPUT_COMPLETE, CONFIRM, CONFIRM_FAILED, CONFIRM_COMPLETED}

    private STATUS mStatus = STATUS.NORMAL;

    private OnStatusChangedListener onStatusChangedListener;


    @Override
    protected void init() {
        handler = new Handler();
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
                switch (mStatus) {
                    case NORMAL:
                        if (isNumberLess(cells)) {
                            mStatus = STATUS.INPUT_LESS;
                            return;
                        } else {
                            inputPassword = BasicUtil.pattern2String(cells);
                            mStatus = STATUS.INPUT_COMPLETE;
                            mPatternView.setInteractEnable(false);
                        }
                        onStatusChangedListener.onStatusChanged(mStatus);
                        break;
                    case CONFIRM:
                        if (inputPassword.equals(BasicUtil.pattern2String(cells))) {
                            mStatus = STATUS.CONFIRM_COMPLETED;
                            mPatternView.setInteractEnable(false);
                        } else {
                            setWrong();
                            mStatus = STATUS.CONFIRM_FAILED;
                        }
                        onStatusChangedListener.onStatusChanged(mStatus);
                        break;
                }
            }
        });
    }

    private void savePassword() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).edit();
        editor.putString(AppConstant.APP_LOCK_PATTERN_PASSWORD, inputPassword);
        editor.putBoolean(AppConstant.APP_FIRST_OPEN, false);
        editor.putInt(AppConstant.APP_LOCK_TYPE, AppConstant.APP_LOCK_PATTERN);
        editor.apply();
        getActivity().setResult(AppConstant.APP_START_SUCCEED);
        getActivity().finish();
    }

    public void setOnStatusChangedListener(OnStatusChangedListener onStatusChangedListener) {
        this.onStatusChangedListener = onStatusChangedListener;
    }

    public void clear() {
        mPatternView.clearPattern();
        mStatus = STATUS.NORMAL;
        onStatusChangedListener.onStatusChanged(mStatus);
        mPatternView.setInteractEnable(true);
    }

    private void setWrong() {
        mPatternView.setWrongFlag(true);
        handler.postDelayed(runnable, 1000);

    }

    public void confirm() {
        if (mStatus == STATUS.INPUT_COMPLETE) {
            mPatternView.setInteractEnable(true);
            mPatternView.clearPattern();
            mStatus = STATUS.CONFIRM;
            onStatusChangedListener.onStatusChanged(mStatus);
        }else if (mStatus==STATUS.CONFIRM_COMPLETED)
            savePassword();
    }

    private boolean isNumberLess(List<PatternView.Cell> cells) {
        if (cells.size() < 4) {
            setWrong();
            return true;
        } else return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_pattern_password;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mPatternView.setWrongFlag(false);
            mPatternView.clearPattern();
            if (mStatus == STATUS.CONFIRM_FAILED) {
                mStatus = STATUS.CONFIRM;
                onStatusChangedListener.onStatusChanged(mStatus);
            }
        }
    };

    public interface OnStatusChangedListener {
        void onStatusChanged(STATUS status);
    }

}
