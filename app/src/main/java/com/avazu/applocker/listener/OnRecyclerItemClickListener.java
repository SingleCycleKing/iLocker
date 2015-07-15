package com.avazu.applocker.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.avazu.applocker.util.BasicUtil;


public class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private int color;
    private boolean mBoolean = false;

    public OnRecyclerItemClickListener(Context context, OnItemClickListener listener, int color) {
        mListener = listener;
        this.color = color;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mBoolean = true;
    }


    public OnRecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (null != childView && null != mListener && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildLayoutPosition(childView));
        }
        if (null != childView && mBoolean) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    childView.setBackgroundColor(BasicUtil.makePressColor(color));
                    break;
                case MotionEvent.ACTION_UP:
                    childView.setBackgroundColor(color);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    childView.setBackgroundColor(color);
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    childView.setBackgroundColor(color);
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


}
