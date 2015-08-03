package com.sample.applocker.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.sample.applocker.listener.ViewPagerListener;

public class CustomViewPager extends ViewPager {

    private ViewPagerListener mListener;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setOnPageSelectedListener(ViewPagerListener.OnPageSelectedListener onPageSelectedListener) {
        mListener = new ViewPagerListener();
        mListener.setOnPageSelectedListener(onPageSelectedListener);
        this.addOnPageChangeListener(mListener);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        mListener.setIsScrollEnabled(true);
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mListener.isScrollEnabled()) {
            super.scrollTo(x, y);
        }
    }


}
