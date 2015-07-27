package com.avazu.applocker.view.fragment;

import android.content.SharedPreferences;
import android.os.Handler;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.util.BasicUtil;
import com.avazu.applocker.util.DebugLog;
import com.avazu.applocker.view.widget.pattern.PatternView;

import java.util.List;

import butterknife.InjectView;

public class SetPatternPassword extends BaseFragment {

    @InjectView(R.id.pattern_lock)
    PatternView mPatternView;

    private boolean inputCompleted = false;
    private String inputPassword = "";
    private boolean confirmCompleted = false;
    private Handler handler;
    private String string = "";
    private OnTipChangedListener onTipChangedListener;


    public void setOnTipChangedListener(OnTipChangedListener onTipChangedListener) {
        this.onTipChangedListener = onTipChangedListener;
    }

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
                if (cells.size() < 4) {
                    mPatternView.setWrongFlag(true);
                    handler.postDelayed(runnable, 1000);
                    string = getResources().getString(R.string.pattern_less);
                    onTipChangedListener.onTipChanged(string);
                } else {
                    DebugLog.e("input");
                    if (!inputCompleted) {
                        inputCompleted = true;
                        inputPassword = BasicUtil.pattern2String(cells);
                    } else if (inputPassword.equals(BasicUtil.pattern2String(cells)))
                        confirmCompleted = true;
                    else if (!inputPassword.equals(BasicUtil.pattern2String(cells))) {
                        mPatternView.setWrongFlag(true);
                        handler.postDelayed(runnable, 1000);
                        string=getResources().getString(R.string.sorry);
                        onTipChangedListener.onTipChanged(string);
                    }
                }
            }
        });
    }

    public void confirm() {
        if (inputCompleted && !confirmCompleted) {
            mPatternView.clearPattern();
            string = getResources().getString(R.string.confirm_pattern);
            onTipChangedListener.onTipChanged(string);
        } else if (inputCompleted && confirmCompleted) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(AppConstant.APP_SETTING, 0).edit();
            editor.putString(AppConstant.APP_LOCK_PATTERN_PASSWORD, inputPassword);
            editor.putBoolean(AppConstant.APP_FIRST_OPEN, false);
            editor.putInt(AppConstant.APP_LOCK_TYPE, AppConstant.APP_LOCK_PATTERN);
            editor.apply();
            getActivity().setResult(AppConstant.APP_START_SUCCEED);
            getActivity().finish();
        }
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
        }
    };

    public interface OnTipChangedListener{
        void onTipChanged(String tip);
    }
}
