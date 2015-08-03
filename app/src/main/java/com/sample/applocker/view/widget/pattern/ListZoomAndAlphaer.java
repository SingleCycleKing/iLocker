package com.sample.applocker.view.widget.pattern;

import android.os.SystemClock;


public class ListZoomAndAlphaer {

    private ZommerAndAlphaer mChanger1;
    private ZommerAndAlphaer mChanger2;

    private int durationChanger1;
    private int durationChanger2;

    private boolean mIsFinished;

    private long mStartRTC;

    private float mCurrentZommer;
    private float mCurrentAlpha;

    private ZommerAndAlphaer mCurrentChanger;

    public void startChange() {

        mStartRTC = SystemClock.elapsedRealtime();
        mChanger1.startChange(mStartRTC);
        mIsFinished = false;

    }

    public void init(ZommerAndAlphaer changer1, ZommerAndAlphaer changer2) {
        this.mChanger1 = changer1;
        this.mChanger2 = changer2;
        durationChanger1 = this.mChanger1.getDuration();
        durationChanger2 = this.mChanger2.getDuration();
    }

    public boolean computeChange() {
        if (mIsFinished) {
            mChanger1.forceFinish(true);
            mChanger2.forceFinish(true);
            return false;
        }

        long currentRTC = SystemClock.elapsedRealtime();

        int spendTime = (int) (currentRTC - mStartRTC);
        if (spendTime <= this.durationChanger1) {
            mCurrentChanger = this.mChanger1;
            mIsFinished = false;
        } else if (spendTime > this.durationChanger1 && spendTime <= (this.durationChanger1 + this.durationChanger2)) {
            mCurrentChanger = this.mChanger2;
            boolean isFinished = mCurrentChanger.isFinished();
            if (isFinished) {
                mCurrentChanger.startChange(currentRTC);
            }
            mIsFinished = false;
        } else {
            mCurrentChanger = mChanger2;
            mIsFinished = true;

        }

        mCurrentChanger.computeChange(currentRTC);
        mCurrentZommer = mCurrentChanger.getCurrentZommer();
        mCurrentAlpha = mCurrentChanger.getCurrentAlpha();
        return true;
    }

    public float getCurrentZommer() {
        return mCurrentZommer;
    }

    public float getCurrentAlpha() {
        return mCurrentAlpha;
    }

    public float getFinalZommer() {
        return mChanger2.getFinalZommer();
    }

    public float getFinalAlpha() {
        return mChanger2.getFinalAlpha();
    }

    public void forceFinish(boolean isFinished) {
        mIsFinished = isFinished;
        mChanger1.forceFinish(true);
        mChanger2.forceFinish(true);
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    public void abortAnimation() {
        forceFinish(true);
        mCurrentZommer = getFinalZommer();
        mCurrentAlpha = getFinalAlpha();
    }

}
