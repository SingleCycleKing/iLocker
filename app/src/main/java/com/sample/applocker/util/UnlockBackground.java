package com.sample.applocker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.WindowManager;

import com.avazu.applocker.R;

import java.io.ByteArrayOutputStream;

public class UnlockBackground {
    private AverageColorInfo mAverageColorInfo;
    private byte[] originByte;
    private Context mContext;
    private Point mSize;

    public UnlockBackground(Bitmap origin, Context context) {
        mAverageColorInfo = AverageColorInfo.getColorInfo(origin);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        origin.compress(Bitmap.CompressFormat.PNG, 100, stream);
        originByte = stream.toByteArray();
        mContext = context;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display mDisplay = windowManager.getDefaultDisplay();
        mSize = new Point();
        mDisplay.getSize(mSize);
        DebugLog.e(mSize.x + ":" + mSize.y);
    }

    private Bitmap getZoomIcon() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(originByte, 0, originByte.length);
        return Bitmap.createScaledBitmap(bitmap, 1500, 1500, false);
    }

    private Drawable blur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = Bitmap.createScaledBitmap(sentBitmap, sentBitmap.getWidth() / 2, sentBitmap.getHeight() / 2, false);
        RenderScript renderScript = RenderScript.create(mContext);
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(renderScript, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        return new BitmapDrawable(bitmap);
    }

    public Drawable getBackground() {
        Bitmap bitmap = Bitmap.createBitmap(mSize.x, mSize.y, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.argb(255, mAverageColorInfo.getRed(), mAverageColorInfo.getGreen(), mAverageColorInfo.getBlue()));
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(getZoomIcon(), mSize.x / 2 - 750, mSize.y / 2 - 750, null);
        canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.transparent_background), 0, 0, null);
        canvas.save();
        return blur(bitmap, 20);
    }
}
