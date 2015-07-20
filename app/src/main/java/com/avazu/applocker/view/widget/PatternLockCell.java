package com.avazu.applocker.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PatternLockCell extends View {

    private int width;
    private int height;
    private int radius;
    private Paint fillPaint;
    private int centerRadius = 5;
    private boolean isSelected = false;

    public PatternLockCell(Context context, AttributeSet attrs) {
        super(context, attrs);

        fillPaint = new Paint();
        fillPaint.setColor(Color.parseColor("#50FFFFFF"));
        fillPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width / 2, height / 2, centerRadius, fillPaint);
        if (isSelected) centerRadius += 10;
        if (centerRadius > radius) centerRadius = radius;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        radius = width > height ? height / 2 : width / 2;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSelected = true;
                break;
        }
        return super.onTouchEvent(event);

    }


}
