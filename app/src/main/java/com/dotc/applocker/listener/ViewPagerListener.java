package com.dotc.applocker.listener;

import android.support.v4.view.ViewPager;

public class ViewPagerListener implements ViewPager.OnPageChangeListener {

    private OnPageSelectedListener onPageSelectedListener;
    private boolean isScrollEnabled;

    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        this.onPageSelectedListener = onPageSelectedListener;
    }

    public boolean isScrollEnabled() {
        return isScrollEnabled;
    }

    public void setIsScrollEnabled(boolean isScrollEnabled) {
        this.isScrollEnabled = isScrollEnabled;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        isScrollEnabled = false;
        onPageSelectedListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface OnPageSelectedListener {
        void onPageSelected(int position);
    }
}