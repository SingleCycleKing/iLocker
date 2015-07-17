package com.avazu.applocker.view.widget;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.avazu.applocker.R;
import com.avazu.applocker.adapter.KeyboardAdapter;
import com.avazu.applocker.listener.OnRecyclerItemClickListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Keyboard extends LinearLayout {

    private KeyboardAdapter mAdapter;
    private OnPasswordInput onPasswordInput;
    private Animation animation;
    private Animation.AnimationListener mListener;

    @InjectView(R.id.keyboard_list)
    RecyclerView mKeyboard;

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.keyboard, this, true);
        ButterKnife.inject(this, view);

        animation = AnimationUtils.loadAnimation(context, R.anim.shake);

        init(context);

    }
    public void setListener(Animation.AnimationListener mListener) {
        this.mListener = mListener;
        animation.setAnimationListener(mListener);
    }

    public void shake() {
        this.startAnimation(animation);

    }

    public void setOnPasswordInput(OnPasswordInput onPasswordInput) {
        this.onPasswordInput = onPasswordInput;
    }

    private void init(Context context) {
        mKeyboard.setLayoutManager(new GridLayoutManager(context, 3));
        mKeyboard.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new KeyboardAdapter(context);
        mKeyboard.setAdapter(mAdapter);
        mKeyboard.addOnItemTouchListener(new OnRecyclerItemClickListener(context, new OnRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onPasswordInput.onPasswordInput(mAdapter.getText(position));
            }
        }));
    }

    public interface OnPasswordInput {
        void onPasswordInput(String password);
    }
}
