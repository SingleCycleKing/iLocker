package com.dotc.applocker.view.widget.pattern;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class ZoomerAndAlphaAnimation extends Animation {
    
    private int firstDuration ;
    private int secondDuration ;
    private Interpolator firstInterpolator ;
    private Interpolator secondInterpolator ;
    
    
    private float mFirstStartZommer ;
    private float mFirstStopZommer ;
    private float mFirstAlphaStart ;
    private float mFirstAlphaStop ;
    
    private float mSecoundStartZommer ;
    private float mSecoundStopZommer ;
    private float mSecoundAlphaStart ;
    private float mSecoundAlphaStop ;
    
    private float centerX ;
    private float centerY ;
    
    public ZoomerAndAlphaAnimation(float centerX,float centerY) {
        this.centerX = centerX ;
        this.centerY = centerY ;
        setFirstAnimation(0.6f, 1.2f, 0.6f, 0.9f);
        setSecoundAnimation(1.2f, 1, 0.9f, 1);
        setDuration(500, 100);
    }
    public void setFirstAnimation( float startZommer,float stopZommer,float alphaStart,float alphaStop) {
        mFirstStartZommer = startZommer ;
        mFirstStopZommer = stopZommer ;
        mFirstAlphaStart = alphaStart ;
        mFirstAlphaStop = alphaStop ;
        firstInterpolator = new CubicBezierInterpolator(0.355, 0, 0.76, 1) ;
    }
    
    public void setSecoundAnimation( float startZommer,float stopZommer,float alphaStart,float alphaStop) {
        mSecoundStartZommer = startZommer ;
        mSecoundStopZommer = stopZommer ;
        mSecoundAlphaStart = alphaStart ;
        mSecoundAlphaStop = alphaStop ;
        secondInterpolator = new LinearInterpolator();
    }
    
    public void setDuration( int firstDuration,int secondDuration ) {
        this.firstDuration  = firstDuration ;
        this.secondDuration = secondDuration ;
        setDuration( firstDuration + secondDuration ); 
    }
    
    private boolean isFirstAnimation(float interpolatedTime) {
       int time = (int) (interpolatedTime * getDuration()) ;
       if( time > firstDuration ) {
           return false ;
       } else {
           return true ;
       }
    }
    
    private float calculateInterpolatedTime(float interpolatedTime) {
        int time = (int) (interpolatedTime * getDuration()) ;
        if( time > firstDuration ) {
            return ( time-firstDuration ) / (float)secondDuration  ;
        } else {
            return time / (float) firstDuration ;
        }
    }
    
    private float getZommer(float interpolatedTime) {
        float startZommer = mFirstStartZommer ;
        float stopZommer = mFirstStopZommer ;
        
        
        Interpolator interpolator = firstInterpolator ;
        
        
        if( !isFirstAnimation(interpolatedTime) ) {
            startZommer = mSecoundStartZommer ;
            stopZommer = mSecoundStopZommer ;
            interpolator = secondInterpolator ;
        }
        
        return startZommer + (stopZommer - startZommer ) * interpolator.getInterpolation(calculateInterpolatedTime(interpolatedTime));
        
    }
    
    private float getAlpha( float interpolatedTime ) {

        float startAlpha = mFirstAlphaStart ;
        float stopAlpha = mFirstAlphaStop ;
        Interpolator interpolator = firstInterpolator ;
        if( !isFirstAnimation(interpolatedTime) ) {
            startAlpha = mSecoundAlphaStart ;
            stopAlpha = mSecoundAlphaStop ;
            interpolator = secondInterpolator ;
        }
        
        return startAlpha + (startAlpha - stopAlpha ) * interpolator.getInterpolation(calculateInterpolatedTime(interpolatedTime));
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        // TODO Auto-generated method stub
        super.applyTransformation(interpolatedTime, t);
        
        Matrix matrix = t.getMatrix() ;
        float zommer = getZommer(interpolatedTime);
        matrix.postScale(zommer, zommer);
        float alpha = getAlpha(interpolatedTime);
        t.setAlpha(alpha);
        
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
