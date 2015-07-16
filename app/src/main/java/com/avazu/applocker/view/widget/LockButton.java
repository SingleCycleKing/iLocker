package com.avazu.applocker.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avazu.applocker.R;
import com.avazu.applocker.util.BasicUtil;


public class LockButton extends RelativeLayout {
    public int minWidth;
    public int minHeight;
    private float rippleSpeed;
    private int rippleSize;
    private int rippleColor;
    private Paint paint;
    private TextView textView;
    private String text;
    private float x = -1, y = -1, radius = -1;


    public LockButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultProperties();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LockButton);

        try {
            rippleSpeed = array.getFloat(R.styleable.LockButton_lock_rippleSpeed, 10f);
            rippleSize = array.getInt(R.styleable.LockButton_lock_rippleSize, 5);
            rippleColor = array.getColor(R.styleable.LockButton_lock_rippleColor, Color.parseColor("#9Dffffff"));
            text = array.getString(R.styleable.LockButton_lock_text);
        } finally {
            array.recycle();
        }

        textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(BasicUtil.spToPx(context, 16));

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textView.setLayoutParams(params);
        addView(textView);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(80);
        paint.setAntiAlias(true);
        paint.setColor(rippleColor);

        setBackgroundResource(R.drawable.circle);
    }

    public void setText(String text) {
        this.text = text;
        textView.setText(text);
    }



    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {
            Rect src = new Rect(0, 0, getWidth(), getHeight());
            Rect dst = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(cropCircle(makeCircle()), src, dst, null);
        }
        invalidate();
    }

    protected void setDefaultProperties() {
        rippleSpeed = BasicUtil.dpToPx(2, getResources());
        rippleSize = BasicUtil.dpToPx(5, getResources());
        minWidth = 28 * 2;
        minHeight = 28 * 2;
    }


    private Bitmap makeCircle() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(x, y, radius, paint);
        if (radius > getHeight() / rippleSize || radius > getWidth() / rippleSize)
            radius += rippleSpeed;
        if (radius >= getWidth() && radius > getHeight()) {
            x = -1;
            y = -1;
            radius = getHeight() / rippleSize;
        }
        return bitmap;
    }


    public Bitmap cropCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(rippleColor);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            x = -1;
            y = -1;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            radius = getHeight() / rippleSize;
            x = getPivotX();
            y = getPivotY();
            if ((event.getX() <= getWidth() && event.getX() >= 0) &&
                    (event.getY() <= getHeight() && event.getY() >= 0)) {
                radius++;
            } else {
                x = -1;
                y = -1;
            }
        }
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (!gainFocus) {
            x = -1;
            y = -1;
        }
    }


    public void setRippleSize(int rippleSize) {
        this.rippleSize = rippleSize;
    }

    public void setRippleSpeed(float rippleSpeed) {
        this.rippleSpeed = rippleSpeed;
    }


    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }

}
