package com.dotc.applocker.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avazu.applocker.R;
import com.dotc.applocker.view.widget.DeleteButton;
import com.dotc.applocker.view.widget.LockButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class KeyboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0};

    public KeyboardAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_keyboard, parent, false));
            case 1:
                return new Delete(LayoutInflater.from(mContext).inflate(R.layout.item_delete, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.lockButton.setText(numbers[position].toString());
                if (9 == position) viewHolder.lockButton.setVisibility(View.GONE);
                break;
        }

    }

    public Integer getText(int position) {
        return numbers[position];
    }

    @Override
    public int getItemViewType(int position) {
        if (11 == position)
            return 1;
        else return 0;
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class Delete extends RecyclerView.ViewHolder {
        @InjectView(R.id.delete)
        DeleteButton delete;

        public Delete(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.key_number)
        LockButton lockButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
