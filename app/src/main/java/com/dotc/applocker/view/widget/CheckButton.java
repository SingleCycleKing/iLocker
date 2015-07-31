package com.dotc.applocker.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CheckButton extends View {

    private int height;
    private int width;
    private int radius;

    private Paint strokePaint;
    private Paint fillPaint;

    private boolean isChecked = false;

    public CheckButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3);
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.WHITE);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(Color.WHITE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(width / 2, height / 2, radius, strokePaint);
        if (isChecked) canvas.drawCircle(width / 2, height / 2, radius - 10, fillPaint);
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        radius = height > width ? width / 2 - 5 : height / 2 - 5;
    }

}
