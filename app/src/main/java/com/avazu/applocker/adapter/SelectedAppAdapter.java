package com.avazu.applocker.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.avazu.applocker.database.model.AppModel;
import com.avazu.applocker.util.DebugLog;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectedAppAdapter extends RecyclerView.Adapter<SelectedAppAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AppModel> mData;

    public SelectedAppAdapter(Context mContext, ArrayList<AppModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public void updateData(ArrayList<AppModel> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppModel mInfo = mData.get(position);
        try {
            holder.icon.setImageDrawable(mContext.getPackageManager().getApplicationIcon(mInfo.getPackageName()));
            holder.label.setText(mInfo.getLabel());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_app_icon)
        ImageView icon;
        @InjectView(R.id.item_app_label)
        TextView label;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}