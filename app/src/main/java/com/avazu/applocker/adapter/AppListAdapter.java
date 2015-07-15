package com.avazu.applocker.adapter;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avazu.applocker.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private Context mContext;
    private List<ResolveInfo> mData;

    public AppListAdapter(Context mContext, List<ResolveInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResolveInfo mInfo = mData.get(position);
        holder.icon.setImageResource(mInfo.getIconResource());
        holder.label.setText(mContext.getString(mInfo.labelRes));
    }

    @Override
    public int getItemCount() {
        return mData.size();
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
