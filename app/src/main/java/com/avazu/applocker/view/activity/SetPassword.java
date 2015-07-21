package com.avazu.applocker.view.activity;

import android.view.MenuItem;

import com.avazu.applocker.R;

public class SetPassword extends BaseActivity {




    @Override
    protected String setTitle() {
        return "Password";
    }

    @Override
    protected void init() {

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
}
