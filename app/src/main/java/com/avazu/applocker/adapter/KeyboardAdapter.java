package com.avazu.applocker.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avazu.applocker.R;
import com.avazu.applocker.view.widget.LockButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.ViewHolder> {

    private Context mContext;
    private int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

    public KeyboardAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_keyboard, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.lockButton.setText(String.valueOf(numbers[position]));
    }

    public String getText(int position){
        return String.valueOf(numbers[position]);
    }

    @Override
    public int getItemCount() {
        return numbers.length;
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
