package com.avazu.applocker.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Indicator extends View {


    private Paint circlePaint;

    private Paint stuffPaint;

    private float childWidth;

    private float height;

    private Keyboard mKeyboard;

    private String mPassword = "";

    private OnPasswordInputCompleted onPasswordInputCompleted;

    private int count = 0;

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);

        stuffPaint = new Paint();
        stuffPaint.setAntiAlias(true);
        stuffPaint.setColor(Color.parseColor("#9Dffffff"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        childWidth = MeasureSpec.getSize(widthMeasureSpec) / 4;
        childWidth = childWidth > height ? height : childWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas, circlePaint, 4);
        drawCircle(canvas, stuffPaint, count);

    }

    private void drawCircle(Canvas canvas, Paint paint, int j) {
        for (int i = 0; i < j; i++) {
            float centerX = i * childWidth + childWidth / 2;
            float centerY =  height / 2;
            canvas.drawCircle(centerX, centerY, childWidth / 4, paint);
        }
    }

    public Keyboard getKeyboard() {
        return mKeyboard;
    }

    public void setKeyboard(Keyboard mKeyboard) {
        this.mKeyboard = mKeyboard;
        mKeyboard.setOnPasswordInput(new Keyboard.OnPasswordInput() {
            @Override
            public void onPasswordInput(String password) {
                count++;
                mPassword += password;
                if (4 == count) {
                    onPasswordInputCompleted.onPasswordInputCompleted(mPassword);
                }
                invalidate();
            }
        });
    }

    public void restore() {
        count = 0;
        mPassword = "";
        invalidate();
    }

    public interface OnPasswordInputCompleted {
        void onPasswordInputCompleted(String password);
    }

    public void setOnPasswordInputCompleted(OnPasswordInputCompleted onPasswordInputCompleted) {
        this.onPasswordInputCompleted = onPasswordInputCompleted;
    }
}