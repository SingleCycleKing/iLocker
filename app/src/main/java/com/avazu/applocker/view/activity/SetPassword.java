package com.avazu.applocker.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.avazu.applocker.util.AppConstant;
import com.avazu.applocker.view.fragment.SetPatternPassword;
import com.avazu.applocker.view.fragment.SetPinPassword;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class SetPassword extends BaseNoActionBarActivity {

    private ArrayList<Fragment> mFragments;
    private int mCurrentPage = 0;

    @OnClick(R.id.cancel_set)
    void Cancel() {
        setResult(AppConstant.APP_START_FAILED);
        finish();
    }

    @OnClick(R.id.continue_set)
    void Continue() {
        if (0 == mCurrentPage) {
            SetPatternPassword patternPassword = (SetPatternPassword) mFragments.get(0);
            patternPassword.confirm();

        } else {
            SetPinPassword pinPassword = (SetPinPassword) mFragments.get(1);
            pinPassword.confirm();

        }
    }

    @InjectView(R.id.set_password_tip)
    TextView modeTip;

    @OnClick(R.id.set_password_tip)
    void switchMode() {
        switch (mCurrentPage) {
            case 0:
                transaction(1, mCurrentPage);
                mCurrentPage = 1;
                inputTip.setText(getResources().getString(R.string.pattern_tip));
                modeTip.setText(getResources().getString(R.string.use_pin_tip));
                break;
            case 1:
                transaction(0, mCurrentPage);
                mCurrentPage = 0;
                inputTip.setText(getResources().getString(R.string.pin_tip));
                modeTip.setText(getResources().getString(R.string.use_pattern_tip));
                break;
        }
    }

    @InjectView(R.id.password_tip)
    TextView inputTip;

    @Override
    protected void init() {
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragments = new ArrayList<>();

        initPager();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.set_content, mFragments.get(0));
        transaction.commit();
        mCurrentPage = 0;
        inputTip.setText(getResources().getString(R.string.pin_tip));
        modeTip.setText(getResources().getString(R.string.use_pattern_tip));
    }

    private void initPager() {
        SetPatternPassword patternPassword = new SetPatternPassword();
        patternPassword.setOnTipChangedListener(new SetPatternPassword.OnTipChangedListener() {
            @Override
            public void onTipChanged(String tip) {
                inputTip.setText(tip);
            }
        });
        SetPinPassword pinPassword = new SetPinPassword();
        pinPassword.setOnTipChangedListener(new SetPinPassword.OnTipChangedListener() {
            @Override
            public void onTipChanged(String tip) {
                inputTip.setText(tip);
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
        overridePendingTransition(R.anim.hold, R.anim.out_to_end);
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


}
