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

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AppModel> mData;

    public AppListAdapter(Context mContext, ArrayList<AppModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
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
            if (mInfo.isSelected()) holder.selected.setVisibility(View.VISIBLE);
            else holder.selected.setVisibility(View.GONE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public void updateData(ArrayList<AppModel> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_app_icon)
        ImageView icon;
        @InjectView(R.id.item_app_label)
        TextView label;
        @InjectView(R.id.item_app_selected)
        ImageView selected;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
