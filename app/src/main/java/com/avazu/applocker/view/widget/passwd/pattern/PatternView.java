package com.avazu.applocker.view.widget.passwd.pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;


import com.avazu.applocker.R;

import java.util.ArrayList;
import java.util.List;

public class PatternView extends View {

    public static int CIRCLE_STATE_NORMAL = 0;
    public static int CIRCLE_STATE_SELECTED = 1;
    public static int CIRCLE_STATE_SELECTING = 2;

    public static final String TAG = "PatterView";

    private static float ZOMMER_AFTER = 1f;
    private static float ALPHA_AFTER = 0.98f;

    private static float ALPHA_BEGINNING = 0.6F;
    private static int mMatrix = 3;
    private static int mPadding = 10;

    private int mGap;

    private Bitmap mBitmapCicleDefault;
    private Bitmap mBitmapCicleRight;
    private Bitmap mBitmapCicleWrong;

    private int mBitmapWidth;
    private int mBitmapHeight;

    // 当前正在选择的row,column.
    private int mSelectingRow = -1;
    private int mSelectingColumn = -1;

    private int mWidth;
    private int mHeight;

    private float scaleX;
    private float scaleY;
    private int offsetX;
    private int offsetY;

    private int mStartTop;
    private int mStartLeft;

    private boolean mFeedbackEnable;
    private boolean mInteractEnable;

    private OnPatterListener mOnPatternListener;

    private boolean mPatternLookup[][];
    private Rect mCellRects[][];

    private float mSquareWidth;
    private float mSquareHeight;
    private Paint mPaint = new Paint();
    private Paint mPathPaint = new Paint();

    private int mStrokeAlpha = 128;

    List<Cell> mDetectedCells;

    private List<Integer> mMoveFromDetectArea = new ArrayList<Integer>();
    private List<CellBindAnimation> mCellBindAnimation = new ArrayList<CellBindAnimation>();
    private CellBindAnimation mLastBindAnimation;

    private boolean mIsInDrawingProcess;
    private float mProcessX;
    private float mProcessY;

    private ListZoomAndAlphaer listZoomAndAlphaer;

    private boolean mIsWrong = false;
    private Runnable mClearRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            clearPattern();
        }
    };

    class CellBindAnimation {

        boolean isEnter;
        ZommerAndAlphaer mZoomAndAlpha;

        public void setIsEnter(boolean isEnter) {
            this.isEnter = isEnter;
            mZoomAndAlpha = new ZommerAndAlphaer(getContext(), new LinearInterpolator());
            if (isEnter) {
                mZoomAndAlpha.init(scaleX, 1, 0.85f - scaleX, -0.4f, 140);
            } else {
                mZoomAndAlpha.init(0.85f, 0.4f, scaleX - 0.85f, ALPHA_AFTER - 0.4f, 140);
            }
            mZoomAndAlpha.startChange();
        }

        public ZommerAndAlphaer getChanger() {

            return mZoomAndAlpha;
        }

    }

    public static class Cell implements Parcelable {

        public static String cellListString(List<Cell> cells) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < cells.size(); i++) {
                if (i != 0) {
                    sb.append(',');
                }
                sb.append(cells.get(i).toNumber());
            }
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            // TODO Auto-generated method stub
            if (o instanceof Cell) {

                Cell other = (Cell) o;
                return this.mRow == other.mRow && this.mColumn == other.mColumn;
            }
            return false;
        }

        public String toNumber() {
            return (mRow * mMatrix + mColumn) + "";
        }

        @Override
        public String toString() {
            return "Cell [mRow=" + mRow + ", mColumn=" + mColumn + "]";
        }

        int mRow;
        int mColumn;

        Cell(int row, int column) {
            checkRange(row, column);
            mRow = row;
            mColumn = column;
        }

        public Cell(int position) {
            mRow = position / mMatrix;
            mColumn = position % mMatrix;
        }

        public int getRow() {
            return mRow;
        }

        public int getColumn() {
            return mColumn;
        }

        public static void checkRange(int row, int column) {
            if (row < 0 || row > mMatrix - 1) {
                throw new IllegalArgumentException();
            }

            if (column < 0 || column > mMatrix - 1) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            // TODO Auto-generated method stub
            dest.writeInt(mColumn);
            dest.writeInt(mRow);
        }

        public static final Creator<Cell> CREATOR = new Creator<Cell>() {

            @Override
            public Cell[] newArray(int size) {
                // TODO Auto-generated method stub
                return new Cell[size];
            }

            @Override
            public Cell createFromParcel(Parcel source) {
                // TODO Auto-generated method stub
                return new Cell(source);
            }
        };

        private void readFromParcel(Parcel in) {
            mColumn = in.readInt();
            mRow = in.readInt();
        }

        Cell(Parcel in) {
            readFromParcel(in);
        }
    }

    public static interface OnPatterListener {
        public void onPatterStart();

        public void onPatterCellClear();

        public void onPatterCellAdd(List<Cell> cells);

        public void onPatterDetected(List<Cell> cells);
    }

    public PatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        this.init();
    }

    public PatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.init();
    }

    public PatternView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.init();
    }

    private boolean mIsPathEnable = true;

    public void setPathEnable(boolean isPathEnable) {
        this.mIsPathEnable = isPathEnable;
    }

    public void setWrongFlag(boolean isWrong) {
        mIsWrong = isWrong;
        if (mIsWrong) {
            mPathPaint.setColor(0xef5e61);
            mPathPaint.setAlpha((int) (0.4 * 255));

        } else {
            mPathPaint.setColor(0xffffff);
            mPathPaint.setAlpha(mStrokeAlpha);
        }
        invalidate();
    }

    //set pattern listener .
    public void setOnPatternListener(OnPatterListener listener) {
        this.mOnPatternListener = listener;
    }

    //set feedback state .
    public void setFeedBackEnable(boolean isEnable) {
        this.mFeedbackEnable = isEnable;
    }

    // set interact state .
    public void setInteractEnable(boolean isEnable) {
        this.mInteractEnable = isEnable;
    }

    // clear pattern .
    public void clearPattern() {
        resetPattern();
    }

    public void postDelayClear(long delay) {
        postDelayed(mClearRunnable, delay);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    private void init() {

        mDetectedCells = new ArrayList<>();
        mPatternLookup = new boolean[mMatrix][mMatrix];
        mCellRects = new Rect[mMatrix][mMatrix];

        new Thread(new Runnable() {

            @Override
            public void run() {
                loadBitmaps();
            }
        }).start();

        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);
        mPathPaint.setColor(Color.WHITE); // TODO
        mPathPaint.setAlpha(mStrokeAlpha);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeJoin(Paint.Join.BEVEL);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        mPathPaint.setStrokeWidth(10);

        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        listZoomAndAlphaer = getListZoomAndAlphaer();
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);


    }


    private void loadBitmaps() {

        this.mBitmapCicleDefault = loadBitmap(R.mipmap.pattern_circle_white3);
        this.mBitmapCicleWrong = loadBitmap(R.mipmap.pattern_circle_white5);
        this.mBitmapCicleRight = loadBitmap(R.mipmap.pattern_circle_white3);

        Bitmap[] bitmaps = new Bitmap[]{
                this.mBitmapCicleDefault,
                this.mBitmapCicleRight,
                this.mBitmapCicleWrong};

        for (Bitmap bitmap : bitmaps) {

            this.mBitmapWidth = Math.max(this.mBitmapWidth, bitmap.getWidth());
            this.mBitmapHeight = Math.max(this.mBitmapHeight, bitmap.getHeight());

        }

    }

    private boolean isCircleSelecting(int row, int column) {
        if (mSelectingRow == row && mSelectingColumn == column) {
            return true;
        }
        return false;
    }

    private Vibrator vibrator;
    private boolean mIsInitSuccess = false;

    private void playFeedback() {
        if (mFeedbackEnable) {
            vibrator.vibrate(30);
        }
    }


    private Cell detectAndAddHit(float x, float y) {
        for (int i = 0; i < mMatrix; i++) {
            for (int j = 0; j < mMatrix; j++) {
                Rect rect = mCellRects[i][j];
                boolean b = rect.contains((int) x, (int) y);
                if (b) {
                    Cell cell = new Cell(i, j);
                    if (!mDetectedCells.contains(cell)) {
                        mSelectingRow = i;
                        mSelectingColumn = j;
                        int middleItems = checkMiddleCell(cell);
                        for (int k = 0; k < middleItems; k++) {
                            CellBindAnimation cellBindAnimation = new CellBindAnimation();
                            cellBindAnimation.setIsEnter(false);
                            cellBindAnimation.getChanger().forceFinish(true);
                            mCellBindAnimation.add(cellBindAnimation);

                            mMoveFromDetectArea.add(0);
                        }

                        playFeedback();
                        if (mIsPathEnable) {
                            listZoomAndAlphaer.startChange();
                        } else {
                            //
                            CellBindAnimation cellBindAnimation = new CellBindAnimation();
                            cellBindAnimation.setIsEnter(true);
                            mLastBindAnimation = cellBindAnimation;
                            mCellBindAnimation.add(mLastBindAnimation);
                        }


                        invalidate();
                        notifyPatternAdd();
                        return cell;
                    }
                }
            }
        }
        return null;
    }


    private void detectPatternMoveFromDetectArea(float x, float y) {
        if (mDetectedCells.size() <= 0) {
            return;
        }

        if (mDetectedCells.size() - 1 == mMoveFromDetectArea.size()) {
            Cell cell = mDetectedCells.get(mDetectedCells.size() - 1);
            if (!mCellRects[cell.mRow][cell.mColumn].contains((int) x, (int) y)) {
                notifyPatternMoveFromDetectArea();
                mMoveFromDetectArea.add(0);
            }
        }
    }


    private void notifyPatternMoveFromDetectArea() {
        if (mLastBindAnimation != null) {
            mLastBindAnimation.setIsEnter(false);
        }
    }

    private void notifyPatternCleared() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatterCellClear();
        }
    }

    private void notifyPatternStarted() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatterStart();
        }
    }

    private void notifyPatternAdd() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatterCellAdd(mDetectedCells);
        }
    }

    private void notifyPatternDetected() {
        if (mOnPatternListener != null && mDetectedCells.size() != 0) {
            final List<Cell> password = copyPattern(mDetectedCells);
            setInteractEnable(false);
            postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    setInteractEnable(true);
                    mOnPatternListener.onPatterDetected(password);
                }
            }, 350);

        }

    }


    private void resetPattern() {
        setWrongFlag(false);
        mDetectedCells.clear();
        mMoveFromDetectArea.clear();
        mCellBindAnimation.clear();
        clearPatternDrawLookup();
        invalidate();
        setWrongFlag(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (!isEnabled() || !mInteractEnable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                removeCallbacks(mClearRunnable);
                handleActionDown(event);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    handleActionMove(event, true, i);
                }
                handleActionMove(event, false, -1);
                break;
            case MotionEvent.ACTION_CANCEL:
                handleActionCancel(event);
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * 1f);
        mWidth = width;
        mHeight = height;
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (!mIsInitSuccess) {
            return;
        }
        for (int row = 0; row < mMatrix; row++) {
            for (int column = 0; column < mMatrix; column++) {
                Rect rect = clearGap(mCellRects[row][column]);
                Cell cell = new Cell(row, column);
                int index = mDetectedCells.indexOf(cell);
                if (mDetectedCells.size() > 0) {
                    if (index >= 0) {
                        if (index > 0) {
                            Path path = getLine(mDetectedCells.get(index - 1), cell);
                            if (mIsPathEnable) {
                                canvas.drawPath(path, mPathPaint);
                            }
                        }

                        if (index == mDetectedCells.size() - 1) {
                            if (mIsInDrawingProcess && index != mMatrix * mMatrix - 1) {
                                if (distance(rect.centerX(), rect.centerY(), mProcessX, mProcessY) > mSquareWidth / 3) {
                                    Path path = getLine(cell, (int) mProcessX, (int) mProcessY);

                                    if (mIsPathEnable) {
                                        canvas.drawPath(path, mPathPaint);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        for (int row = 0; row < mMatrix; row++) {
            for (int column = 0; column < mMatrix; column++) {
                Rect rect = clearGap(mCellRects[row][column]);
                if (mIsPathEnable) {
                    drawSingleCircle(canvas, rect.left, rect.top, mDetectedCells.contains(new Cell(row, column)), row, column);
                } else {
                    drawSingleCirclePathDisable(canvas, rect.left, rect.top, mDetectedCells.contains(new Cell(row, column)), row, column);
                }
            }
        }
    }

    float distance(float x1, float y1, float x2, float y2) {
        float x = (float) Math.pow((x2 - x1), 2);
        float y = (float) Math.pow((y2 - y1), 2);
        return (float) Math.sqrt(x + y);
    }

    Path getLine(Cell cellStart, Cell cellStop) {
        Rect rectStart = clearGap(mCellRects[cellStart.getRow()][cellStart.getColumn()]);
        Rect rectStop = clearGap(mCellRects[cellStop.getRow()][cellStop.getColumn()]);
        int centryStartX = rectStart.centerX();
        int centryStartY = rectStart.centerY();
        int centryStopX = rectStop.centerX();
        int centryStopY = rectStop.centerY();
        Path path = currentPath(centryStartX, centryStartY, centryStopX, centryStopY, true);
        return path;
    }

    Path getLine(Cell cellStart, int stopX, int stopY) {
        Rect rectStart = clearGap(mCellRects[cellStart.getRow()][cellStart.getColumn()]);
        int centryStartX = rectStart.centerX();
        int centryStartY = rectStart.centerY();
        int centryStopX = stopX;
        int centryStopY = stopY;
        Path path = currentPath(centryStartX, centryStartY, centryStopX, centryStopY, false);
        return path;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mSquareWidth = (mWidth / (float) mMatrix);
        mSquareHeight = mHeight / (float) mMatrix;
        initRects();

        mIsInitSuccess = true;
    }

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        super.computeScroll();
        if (mIsPathEnable) {
            if (listZoomAndAlphaer.computeChange()) {
                invalidate();
            }
        } else {
            if (mLastBindAnimation != null) {
                ZommerAndAlphaer zoomerAndAlpha = mLastBindAnimation.getChanger();
                if (zoomerAndAlpha != null && zoomerAndAlpha.computeChange())
                    invalidate();
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // TODO Auto-generated method stub
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(state);
    }

    private void addCell(Cell cell) {
        if (!mDetectedCells.contains(cell)) {
            mDetectedCells.add(cell);
            mPatternLookup[cell.mRow][cell.mColumn] = true;
        }
    }

    private int checkMiddleCell(Cell cell) {

        if (cell == null) {
            return 0;
        }

        if (mDetectedCells.size() < 1) {
            addCell(cell);
            return 0;
        } else {

            List<Cell> newCells = new ArrayList<Cell>();
            final Cell lastCell = mDetectedCells.get(mDetectedCells.size() - 1);
            int dRow = cell.mRow - lastCell.mRow;
            int dCol = cell.mColumn - lastCell.mColumn;
            int rsign = dRow > 0 ? 1 : -1;
            int csign = dCol > 0 ? 1 : -1;

            if (dRow == 0) {
                for (int i = 1; i < Math.abs(dCol); i++) {
                    newCells.add(new Cell(lastCell.mRow, lastCell.mColumn + i * csign));
                }
            } else if (dCol == 0) {
                for (int i = 1; i < Math.abs(dRow); i++) {
                    newCells.add(new Cell(lastCell.mRow + i * rsign, lastCell.mColumn));
                }
            } else if (Math.abs(dCol) == Math.abs(dRow)) {
                for (int i = 1; i < Math.abs(dRow); i++) {
                    newCells.add(new Cell(lastCell.mRow + i * rsign, lastCell.mColumn + i * csign));
                }
            }

            for (Cell fillInGapCell : newCells) {
                if (fillInGapCell != null && !mPatternLookup[fillInGapCell.mRow][fillInGapCell.mColumn]) {
                    addCell(fillInGapCell);
                }
            }
            addCell(cell);
            return newCells.size();

        }


    }

    private void handleActionDown(MotionEvent event) {
        resetPattern();
        float x = event.getX();
        float y = event.getY();
        if (mDetectedCells.size() == 0) {
            notifyPatternStarted();
        }
        Cell cell = detectAndAddHit(x, y);
        if (cell != null) {
            invalidate();
        } else {
            notifyPatternCleared();
        }
    }

    private void clearPatternDrawLookup() {
        for (int i = 0; i < mMatrix; i++) {
            for (int j = 0; j < mMatrix; j++) {
                mPatternLookup[i][j] = false;
            }
        }
    }

    private void handleActionUp(MotionEvent event) {
        mIsInDrawingProcess = false;
        invalidate();
        notifyPatternDetected();
    }

    private void handleActionMove(MotionEvent event, boolean isHistory, int history) {
        float x = event.getX();
        float y = event.getY();
        if (isHistory) {
            x = event.getHistoricalX(history);
            y = event.getHistoricalY(history);
        }
        Cell cell = detectAndAddHit(x, y);
        if (cell == null) {
            mIsInDrawingProcess = true;
            mProcessX = x;
            mProcessY = y;
        }

        detectPatternMoveFromDetectArea(x, y);
        invalidate();
    }

    private void handleActionCancel(MotionEvent event) {
        handleActionUp(event);
    }

    private Bitmap loadBitmap(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 计算每个点的位置信息.方块信息.去除间隙
     */
    private void initRects() {

        scaleX = Math.min(1.0f, (mBitmapWidth) / (3 * mSquareWidth));
        scaleY = Math.min(1.0f, (mBitmapHeight) / (3 * mSquareWidth));

        mGap = (int) (mSquareWidth / (float) 4.5);

        offsetX = (int) ((mSquareWidth - mBitmapWidth) / 2);
        offsetY = (int) ((mSquareHeight - mBitmapHeight) / 2);

        for (int row = 0; row < mMatrix; row++) {
            for (int column = 0; column < mMatrix; column++) {
                int left = calculateLeft(column);
                int top = calculateTop(row);
                mCellRects[row][column] = new Rect(left + mGap, top + mGap, (int) (left + mSquareWidth - mGap), (int) (top + mSquareHeight - mGap));
            }
        }

    }

    /**
     * 补充间隙供画图使用.
     *
     * @param rect
     * @return
     */
    private Rect clearGap(Rect rect) {
        return new Rect(rect.left - mGap, rect.top - mGap, rect.right + mGap, rect.bottom + mGap);
    }

    private ListZoomAndAlphaer getListZoomAndAlphaer() {
        ZommerAndAlphaer changer1 = new ZommerAndAlphaer(getContext(), new CubicBezierInterpolator(0.355, 0, 0.76, 1));
        changer1.init(scaleX, 0.6f, 1.2f - scaleX, 0.1f, 250);

        ZommerAndAlphaer changer2 = new ZommerAndAlphaer(getContext(), new LinearInterpolator());
        changer2.init(1.2f, 0.7f, ZOMMER_AFTER - 1.2f, ALPHA_AFTER - 0.7f, 60);

        ListZoomAndAlphaer listZoomAndAlphaer = new ListZoomAndAlphaer();
        listZoomAndAlphaer.init(changer1, changer2);
        return listZoomAndAlphaer;
    }

    private int calculateLeft(int column) {
        return (int) (mSquareWidth * (column) + mStartLeft);
    }

    private int calculateTop(int row) {
        return (int) (mSquareHeight * row + mStartTop);
    }

    private void drawDockCircles(Canvas canvas) {
        for (int row = 0; row < mMatrix; row++) {
            for (int column = 0; column < mMatrix; column++) {
                Rect rect = clearGap(mCellRects[row][column]);
                drawSingleCircle(canvas, rect.left, rect.top, mDetectedCells.contains(new Cell(row, column)), row, column);
            }
        }
    }

    private float distance(int startX, int startY, int stopX, int stopY) {
        float x = (float) Math.pow((stopX - startX), 2);
        float y = (float) Math.pow((stopY - startY), 2);
        return (float) Math.sqrt(x + y);
    }

    private Path currentPath(int startX, int startY, int stopX, int stopY, boolean isLastAttach) {
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(stopX, stopY);
        return path;

    }

    private void drawLines(Canvas canvas) {
        Path path = currentPath();
        canvas.drawPath(path, mPathPaint);
    }

    private Path currentPath() {
        Path path = new Path();
        int length = mDetectedCells.size();
        for (int i = 0; i < length; i++) {
            Cell cell = mDetectedCells.get(i);
            Rect rect = clearGap(mCellRects[cell.getRow()][cell.getColumn()]);
            // 获取中间的位置.
            int centryX = rect.centerX();
            int centryY = rect.centerY();

            if (i == 0) {
                path.moveTo(centryX, centryY);
            } else {
                path.lineTo(centryX, centryY);
            }

            if (i == length - 1) {
                if (mIsInDrawingProcess && length != 9) {
                    path.lineTo(mProcessX, mProcessY);
                }
            }
        }
        return path;
    }

    private void drawSingleCirclePathDisable(Canvas canvas, int left, int top, boolean partOfPattern, int row, int column) {
        float width = mBitmapWidth;
        float height = mBitmapHeight;
        Matrix matrix = new Matrix();
        matrix.setTranslate(left + offsetX, top + offsetY);
        matrix.preTranslate(width / 2, height / 2);
        if (partOfPattern) {

            Cell cell = new Cell(row, column);
            int index = mDetectedCells.indexOf(cell);
            try {
                float zommer = scaleX;
                float alpha = 1;//
                matrix.preScale(zommer, zommer);
                mPaint.setAlpha((int) (alpha * 255));
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            mPaint.setAlpha((int) (ALPHA_BEGINNING * 255));
            matrix.preScale(scaleX, scaleY);
        }


        matrix.preTranslate(-width / 2, -height / 2);

        Bitmap bitmap = mBitmapCicleDefault;
        if (partOfPattern) {
            canvas.drawBitmap(bitmap, matrix, mPaint);
        } else {
            canvas.drawBitmap(mBitmapCicleDefault, matrix, mPaint);
        }
    }

    private void drawSingleCircle(Canvas canvas, int left, int top, boolean partOfPattern, int row, int column) {
        float width = mBitmapWidth;
        float height = mBitmapHeight;
        Matrix matrix = new Matrix();
        matrix.setTranslate(left + offsetX, top + offsetY);
        matrix.preTranslate(width / 2, height / 2);
        if (partOfPattern) {
            //
            if (isCircleSelecting(row, column)) {
                if (!listZoomAndAlphaer.isFinished()) {
                    float zommer = listZoomAndAlphaer.getCurrentZommer();
                    float alpha = listZoomAndAlphaer.getCurrentAlpha();
                    // LogUtil.i(TAG, "zommer=" + zommer + ",alpha=" + alpha + ",listZoomAndAlphaer=" + listZoomAndAlphaer.hashCode());
                    matrix.preScale(zommer, zommer);
                    mPaint.setAlpha((int) (alpha * 255));
                } else {
                    mSelectingRow = -1;
                    mSelectingColumn = -1;
                }
            } else {
                float zommer = ZOMMER_AFTER;
                float alpha = ALPHA_AFTER;//
                // LogUtil.i(TAG, "is finished---zoomer=" + zommer + ",alpha=" + alpha + ",listZoomAndAlphaer=" + listZoomAndAlphaer.hashCode());
                matrix.preScale(zommer, zommer);
                mPaint.setAlpha((int) (alpha * 255));
            }
        } else {
            mPaint.setAlpha((int) (ALPHA_BEGINNING * 255));
            matrix.preScale(scaleX, scaleY);
        }
        matrix.preTranslate(-width / 2, -height / 2);

        Bitmap bitmap = mBitmapCicleDefault;
        if (mIsWrong) {
            bitmap = mBitmapCicleWrong;
        }
        if (partOfPattern) {
            canvas.drawBitmap(bitmap, matrix, mPaint);
        } else {
            canvas.drawBitmap(mBitmapCicleDefault, matrix, mPaint);
        }
    }

    public static List<Cell> copyPattern(List<Cell> password) {
        List<Cell> retCell = new ArrayList<PatternView.Cell>();
        if (password != null) {
            for (int i = 0; i < password.size(); i++) {
                retCell.add(password.get(i));
            }
        }
        return retCell;
    }

}
