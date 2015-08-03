package com.sample.applocker.view.widget;

import android.content.Context;
import android.os.Vibrator;
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
import com.sample.applocker.adapter.KeyboardAdapter;
import com.sample.applocker.listener.OnRecyclerItemClickListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Keyboard extends LinearLayout {

    private KeyboardAdapter mAdapter;
    private OnPasswordInput onPasswordInput;
    private OnPasswordDelete onPasswordDelete;
    private Animation animation;
    private boolean hasVibrator = false;
    private Vibrator vibrator;
    private boolean mInteractEnable = false;
    private Context mContext;
    @InjectView(R.id.keyboard_list)
    RecyclerView mKeyboard;

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.keyboard, this, true);
        ButterKnife.inject(this, view);

        animation = AnimationUtils.loadAnimation(context, R.anim.shake);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        mContext = context;
        init();

    }

    public void setInteractEnable(boolean mInteractEnable) {
        this.mInteractEnable = mInteractEnable;
    }

    public void setVibratorEnable(boolean hasVibrator) {
        this.hasVibrator = hasVibrator;
    }

    public void setListener(Animation.AnimationListener mListener) {
        animation.setAnimationListener(mListener);
    }

    public void shake() {
        this.startAnimation(animation);

    }

    public void setOnPasswordInput(OnPasswordInput onPasswordInput) {
        this.onPasswordInput = onPasswordInput;
    }

    public void setOnPasswordDelete(OnPasswordDelete onPasswordDelete) {
        this.onPasswordDelete = onPasswordDelete;
    }

    private void init() {
        mKeyboard.setLayoutManager(new GridLayoutManager(mContext, 3));
        mKeyboard.setVerticalScrollBarEnabled(false);
        mKeyboard.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new KeyboardAdapter(mContext);
        mKeyboard.setAdapter(mAdapter);
        mKeyboard.addOnItemTouchListener(new OnRecyclerItemClickListener(mContext, new OnRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (9 != position && mInteractEnable) {
                    if (11 != position)
                        onPasswordInput.onPasswordInput(mAdapter.getText(position));
                    else onPasswordDelete.onPasswordDelete();
                    if (hasVibrator) vibrator.vibrate(400);
                }
            }
        }));
    }

    public interface OnPasswordInput {
        void onPasswordInput(int password);
    }

    public interface OnPasswordDelete {
        void onPasswordDelete();
    }
}
