package com.sample.applocker.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.sample.applocker.util.AppConstant;
import com.sample.applocker.view.fragment.SetPatternPassword;
import com.sample.applocker.view.fragment.SetPinPassword;

import java.util.ArrayList;

import butterknife.InjectView;

public class SetPassword extends BaseNoActionBarActivity implements View.OnClickListener {

    private ArrayList<Fragment> mFragments;
    private int mCurrentPage = 0;

    private enum PASSWORD_STATUS {
        INPUT, CONFIRM
    }

    private PASSWORD_STATUS mStatus = PASSWORD_STATUS.INPUT;

    @InjectView(R.id.cancel_set)
    TextView cancel;

    @InjectView(R.id.continue_set)
    TextView confirm;

    @InjectView(R.id.set_password_tip)
    TextView modeTip;

    @InjectView(R.id.password_tip)
    TextView inputTip;

    @Override
    protected void init() {
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragments = new ArrayList<>();

        initPager();


        if (AppConstant.APP_LOCK_PATTERN == getSharedPreferences(AppConstant.APP_SETTING, 0).getInt(AppConstant.APP_LOCK_TYPE, AppConstant.APP_LOCK_PATTERN))
            mCurrentPage = 0;
        else mCurrentPage = 1;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.set_content, mFragments.get(mCurrentPage));
        transaction.commit();
        if (0 == mCurrentPage) {
            inputTip.setText(getResources().getString(R.string.pattern_tip));
            modeTip.setText(getResources().getString(R.string.use_pin_tip));
        } else {
            inputTip.setText(getResources().getString(R.string.pin_tip));
            modeTip.setText(getResources().getString(R.string.use_pattern_tip));
        }
        cancel.setOnClickListener(this);
        modeTip.setOnClickListener(this);
    }

    private void initPager() {
        final SetPatternPassword patternPassword = new SetPatternPassword();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(AppConstant.APP_START_FAILED);
                finish();
            }
        });

        patternPassword.setOnStatusChangedListener(new SetPatternPassword.OnStatusChangedListener() {
            @Override
            public void onStatusChanged(SetPatternPassword.STATUS status) {
                switch (status) {
                    case NORMAL:
                        inputTip.setText(getResources().getString(R.string.pattern_tip));
                        mStatus = PASSWORD_STATUS.INPUT;
                        confirm.setBackgroundResource(R.drawable.background_disable);
                        confirm.setOnClickListener(null);
                        confirm.setText(getResources().getString(R.string.go_on));
                        cancel.setText(getResources().getString(R.string.cancel));
                        break;
                    case INPUT_LESS:
                        inputTip.setText(getResources().getString(R.string.pattern_less));
                        mStatus = PASSWORD_STATUS.INPUT;
                        break;
                    case INPUT_COMPLETE:
                        confirm.setBackgroundResource(R.drawable.background_enable);
                        confirm.setOnClickListener(SetPassword.this);
                        cancel.setText(R.string.retry);
                        break;
                    case CONFIRM:
                        inputTip.setText(getResources().getString(R.string.confirm_pattern));
                        mStatus = PASSWORD_STATUS.CONFIRM;
                        break;
                    case CONFIRM_FAILED:
                        inputTip.setText(getResources().getString(R.string.pattern_error));
                        mStatus = PASSWORD_STATUS.CONFIRM;
                        break;

                }
            }
        });

        final SetPinPassword pinPassword = new SetPinPassword();
        pinPassword.setOnStatusChangedListener(new SetPinPassword.OnStatusChangedListener() {
            @Override
            public void onStatusChanged(SetPinPassword.STATUS status) {
                switch (status) {
                    case NORMAL:
                        inputTip.setText(getResources().getString(R.string.pin_tip));
                        mStatus = PASSWORD_STATUS.INPUT;
                        confirm.setBackgroundResource(R.drawable.background_disable);
                        confirm.setOnClickListener(null);
                        confirm.setText(getResources().getString(R.string.go_on));
                        cancel.setText(getResources().getString(R.string.cancel));
                        break;
                    case INPUT_COMPLETE:
                        confirm.setBackgroundResource(R.drawable.background_enable);
                        confirm.setOnClickListener(SetPassword.this);
                        cancel.setText(R.string.retry);
                        break;
                    case CONFIRM:
                        inputTip.setText(getResources().getString(R.string.confirm_pin));
                        mStatus = PASSWORD_STATUS.CONFIRM;
                        break;
                    case CONFIRM_FAILED:
                        inputTip.setText(getResources().getString(R.string.pin_error));
                        mStatus = PASSWORD_STATUS.CONFIRM;
                        break;

                }
            }
        });

        mFragments.add(patternPassword);
        mFragments.add(pinPassword);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_set_password;
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void transaction(int position, int transactionType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (transactionType) {
            case 0:
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_top);
                break;
            case 1:
                transaction.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
                break;
        }
        transaction.replace(R.id.set_content, mFragments.get(position));
        transaction.commit();
    }


    private void switchMode() {
        switch (mCurrentPage) {
            case 0:
                transaction(1, mCurrentPage);
                mCurrentPage = 1;
                inputTip.setText(getResources().getString(R.string.pin_tip));
                modeTip.setText(getResources().getString(R.string.use_pattern_tip));
                break;
            case 1:
                transaction(0, mCurrentPage);
                mCurrentPage = 0;
                inputTip.setText(getResources().getString(R.string.pattern_tip));
                modeTip.setText(getResources().getString(R.string.use_pin_tip));
                break;
        }
    }

    private void restore() {
        if (0 == mCurrentPage) {
            SetPatternPassword patternPassword = (SetPatternPassword) mFragments.get(0);
            patternPassword.clear();
        } else {
            SetPinPassword pinPassword = (SetPinPassword) mFragments.get(1);
            pinPassword.clear();
        }
    }

    private void confirm() {
        if (0 == mCurrentPage) {
            SetPatternPassword patternPassword = (SetPatternPassword) mFragments.get(0);
            patternPassword.confirm();
        } else {
            SetPinPassword pinPassword = (SetPinPassword) mFragments.get(1);
            pinPassword.confirm();
        }
        confirm.setText(getString(R.string.confirm));
        cancel.setText(getResources().getString(R.string.cancel));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_set:
                switch (mStatus) {
                    case INPUT:
                        if (cancel.getText().equals(getResources().getString(R.string.cancel)))
                            finish();
                        else restore();
                        break;
                    case CONFIRM:
                        restore();
                        break;
                }
                break;
            case R.id.continue_set:
                confirm();
                break;
            case R.id.set_password_tip:
                switchMode();
                break;
        }
    }
}
