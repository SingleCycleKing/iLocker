package com.dotc.applocker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.dotc.applocker.util.AppConstant;
import com.dotc.applocker.view.widget.CheckButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String[] titles;
    private SharedPreferences settings;


    public SettingAdapter(Context mContext) {
        this.mContext = mContext;
        settings = mContext.getSharedPreferences(AppConstant.APP_SETTING, 0);
        if (AppConstant.APP_LOCK_PATTERN == settings.getInt(AppConstant.APP_LOCK_TYPE, AppConstant.APP_LOCK_PATTERN))
            titles = mContext.getResources().getStringArray(R.array.setting);
        else titles = mContext.getResources().getStringArray(R.array.setting_pin);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ArrowViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_arrow_setting, parent, false));
            case 1:
                return new SwitchViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_switch_setting, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                ArrowViewHolder arrowHolder = (ArrowViewHolder) holder;
                arrowHolder.text.setText(titles[position]);
                break;
            case 1:
                final int mPosition = position;
                SwitchViewHolder switchHolder = (SwitchViewHolder) holder;
                switchHolder.text.setText(titles[position]);
                if (1 == position)
                    switchHolder.mSwitch.setChecked(settings.getBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, false));
                else if (2 == position)
                    switchHolder.mSwitch.setChecked(settings.getBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, false));
                switchHolder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = settings.edit();
                        if (1 == mPosition)
                            editor.putBoolean(AppConstant.APP_VIBRATE_ON_TOUCH, isChecked);
                        else if (2 == mPosition) {
                            editor.putBoolean(AppConstant.APP_LOCK_PATTERN_ENABLE, isChecked);
                        }
                        editor.apply();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) return 0;
        else return 1;
    }

    @Override
    public int getItemCount() {
        return null == titles ? 0 : titles.length;
    }

    public class ArrowViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_arrow_text)
        TextView text;

        public ArrowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public class SwitchViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_switch_text)
        TextView text;
        @InjectView(R.id.item_switch_button)
        SwitchCompat mSwitch;

        public SwitchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
