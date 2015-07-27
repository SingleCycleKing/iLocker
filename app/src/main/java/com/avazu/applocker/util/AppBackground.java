package com.avazu.applocker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.avazu.applocker.R;

public class AppBackground {
    private static Drawable background;

    public static Drawable getInstance(Context context) {
        synchronized (AppBackground.class) {
            if (null == background) {
                Bitmap origin = BasicUtil.blur(context, BasicUtil.getWallpaper(context), 1);
                Canvas canvas = new Canvas(origin);
                canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.transparent_background), 0, 0,null);
                canvas.save();
                background = new BitmapDrawable(origin);
            }
            return background;
        }
    }
}
