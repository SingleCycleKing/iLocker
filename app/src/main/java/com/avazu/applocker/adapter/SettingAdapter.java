package com.avazu.applocker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avazu.applocker.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    public SettingAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ArrowViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_arrow_setting, parent, false));
            case 1:
                return new CheckViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_check_setting, parent, false));
            case 2:
                return new SwitchViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_switch_setting, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                ArrowViewHolder arrowHolder = (ArrowViewHolder) holder;
                arrowHolder.text.setText("TEST");
                break;
            case 1:
                CheckViewHolder checkHolder = (CheckViewHolder) holder;
                checkHolder.text.setText("TEST");
                break;
            case 2:
                SwitchViewHolder switchHolder = (SwitchViewHolder) holder;
                switchHolder.text.setText("TEST");
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ArrowViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_arrow_text)
        TextView text;

        public ArrowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_check_text)
        TextView text;

        public CheckViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public class SwitchViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_switch_text)
        TextView text;

        public SwitchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
