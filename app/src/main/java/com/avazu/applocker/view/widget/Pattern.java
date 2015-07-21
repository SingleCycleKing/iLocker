package com.avazu.applocker.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.avazu.applocker.R;
import com.avazu.applocker.database.model.PatternLineModel;
import com.avazu.applocker.database.model.PatternModel;
import com.avazu.applocker.util.DebugLog;

import java.util.ArrayList;

public class Pattern extends View {

    private ArrayList<PatternModel> models;
    private ArrayList<PatternLineModel> lineModels;
    private int tag = -1;
    private int childWidth;
    private int childHeight;
    private int childRadius;
    private int radius = 20;
    private String password = "";
    private OnPasswordInputCompletedListener onPasswordInputCompletedListener;

    private Paint paint;
    private Paint linePaint;


    public Pattern(Context context, AttributeSet attrs) {
        super(context, attrs);

        models = new ArrayList<>();
        lineModels = new ArrayList<>();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.white));

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(context.getResources().getColor(R.color.white));
        linePaint.setStrokeWidth(20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        childWidth = width / 3;
        childHeight = height / 3;
        childRadius = childWidth > childHeight ? childHeight / 2 : childWidth / 2;
        models.clear();
        setPatternModel();
    }

    private void setPatternModel() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                PatternModel model = new PatternModel();
                model.setTag(row * 3 + column);
                model.setCenterX(column * childWidth + childWidth / 2);
                model.setCenterY(row * childHeight + childHeight / 2);
                model.setIsSelected(false);
                model.setRadius(radius);
                DebugLog.e("add model");
                models.add(model);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (PatternModel model : models) {
            canvas.drawCircle(model.getCenterX(), model.getCenterY(), model.getRadius(), paint);
            DebugLog.e(model.getCenterX() + "--:--" + model.getCenterY());
        }
        for (PatternLineModel model : lineModels) {
            canvas.drawLine(model.getStartX(), model.getStartY(), model.getEndX(), model.getEndY(), linePaint);
            DebugLog.e(model.getStartX() + ":" + model.getStartY());
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (PatternModel model : models) {
                    if (model.getCenterX() - 50 < event.getX() && event.getX() < model.getCenterX() + 50
                            && model.getCenterY() - 50 < event.getY() && event.getY() < model.getCenterY() + 50) {
                        model.setIsSelected(true);
                        model.setRadius(childRadius / 2);
                        tag = model.getTag();
                        password += model.getTag();
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                boolean isSelected = false;
                for (PatternModel model : models) {
                    if (model.getCenterX() - 50 < event.getX() && event.getX() < model.getCenterX() + 50
                            && model.getCenterY() - 50 < event.getY() && event.getY() < model.getCenterY() + 50) {
                        model.setIsSelected(true);
                        model.setRadius(childRadius / 2);
                        password += model.getTag();
                        isSelected = true;
                        if (-1 != tag) {
                            PatternLineModel lineModel = new PatternLineModel();
                            lineModel.setStartX(models.get(tag).getCenterX());
                            lineModel.setStartY(models.get(tag).getCenterY());
                            lineModel.setEndX(model.getCenterX());
                            lineModel.setEndY(model.getCenterY());
                            lineModels.add(lineModel);
                            tag = model.getTag();
                        }
                        invalidate();
                    }
                }
                if (!isSelected) {
                    lineModels.remove(lineModels.size() - 1);
                    PatternLineModel lineModel = new PatternLineModel();
                    lineModel.setStartX(models.get(tag).getCenterX());
                    lineModel.setStartY(models.get(tag).getCenterY());
                    lineModel.setEndX(event.getX());
                    lineModel.setEndY(event.getY());
                    lineModels.add(lineModel);
                }

                break;
            case MotionEvent.ACTION_UP:
                onPasswordInputCompletedListener.onPasswordCompleted(password);
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnPasswordInputCompletedListener {
        void onPasswordCompleted(String password);
    }

    public void setOnPasswordInputCompletedListener(OnPasswordInputCompletedListener onPasswordInputCompletedListener) {
        this.onPasswordInputCompletedListener = onPasswordInputCompletedListener;
    }
}
