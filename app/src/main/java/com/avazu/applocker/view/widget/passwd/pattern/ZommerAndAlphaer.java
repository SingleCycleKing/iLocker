package com.avazu.applocker.view.widget.passwd.pattern;

import android.content.Context;
import android.os.SystemClock;
import android.view.animation.Interpolator;


public class ZommerAndAlphaer {

    private Interpolator mInterpolator;
    private Context mCtx;

    private int mDuration;

    private float mStartZommer;
    private float mEndZommer;

    private float mStartAlpha;
    private float mEndAlpha;

    private float mCurrentZommer;
    private float mCurrentAlpha;

    private boolean mIsFinished;

    private long mStartRTC;

    public ZommerAndAlphaer(Context ctx, Interpolator interpolator) {
        this.mCtx = ctx;
        this.mInterpolator = interpolator;
        mIsFinished = true;
    }

    public void init(float zommerStart, float alphaStart, float deltaZommer, float deltaAlpha, int duration) {
        this.mStartZommer = zommerStart;
        this.mStartAlpha = alphaStart;

        this.mEndZommer = zommerStart + deltaZommer;
        this.mEndAlpha = alphaStart + deltaAlpha;
        this.mDuration = duration;
    }

    public void startChange(long startRTC) {
        mStartRTC = startRTC;
        mIsFinished = false;
    }
    
    public void startChange() {
        startChange(SystemClock.elapsedRealtime());
    }

    public float getCurrentZommer() {
        return mCurrentZommer;
    }

    public float getCurrentAlpha() {
        return mCurrentAlpha;
    }

    public float getFinalZommer() {
        return mEndZommer;
    }

    public float getFinalAlpha() {
        return mEndAlpha;
    }

    public boolean computeChange(long currentRTC) {
        if (this.mIsFinished) {
            return false;
        }

        int deltaRTC = (int) (currentRTC - mStartRTC);
        float input = (deltaRTC * 1f) / mDuration;
        if (input >= 1) {
            this.mIsFinished = true;
            mCurrentZommer = mEndZommer;
            mCurrentAlpha = mEndAlpha;
        } else {
            this.mIsFinished = false;
            float factor = mInterpolator.getInterpolation(input);
            mCurrentZommer = mStartZommer + (mEndZommer - mStartZommer) * factor;
            mCurrentAlpha = mStartAlpha + (mEndAlpha - mStartAlpha) * factor;
        }
        return true;

    }
    
    public boolean computeChange() {
       return  computeChange(SystemClock.elapsedRealtime());
    }

    public int getDuration() {
        return mDuration;
    }

    public void forceFinish(boolean isFinished) {
        this.mIsFinished = isFinished;
    }

    public boolean isFinished() {
        return this.mIsFinished;
    }

    public void abortAnimation() {
        mIsFinished = true;
        mCurrentZommer = mEndZommer;
        mCurrentAlpha = mEndAlpha;
    }

}
