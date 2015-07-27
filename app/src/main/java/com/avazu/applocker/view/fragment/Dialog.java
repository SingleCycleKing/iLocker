package com.avazu.applocker.view.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.avazu.applocker.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Dialog extends DialogFragment {

    private OnDialogClickListener onDialogClickListener;

    private Context mContext;

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    @OnClick(R.id.dialog_positive)
    void positive() {
        onDialogClickListener.onPositive();
    }

    @OnClick(R.id.dialog_negative)
    void negative() {
        dismiss();
        onDialogClickListener.onNegative();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    public interface OnDialogClickListener {
        void onPositive();

        void onNegative();
    }
}
