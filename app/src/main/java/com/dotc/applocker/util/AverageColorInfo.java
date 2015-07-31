package com.dotc.applocker.util;

import android.graphics.Bitmap;
import android.graphics.Color;

public class AverageColorInfo {

    private int red, green, blue;

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public static AverageColorInfo getColorInfo(Bitmap bmp) {
        AverageColorInfo mInfo = new AverageColorInfo();
        int redColors = 0;
        int greenColors = 0;
        int blueColors = 0;
        int pixelCount = 0;
        for (int y = 0; y < bmp.getHeight(); y++) {
            for (int x = 0; x < bmp.getWidth(); x++) {
                int c = bmp.getPixel(x, y);
                pixelCount++;
                redColors += Color.red(c);
                greenColors += Color.green(c);
                blueColors += Color.blue(c);
            }
        }
        mInfo.setRed(redColors / pixelCount);
        mInfo.setGreen(greenColors / pixelCount);
        mInfo.setBlue(blueColors / pixelCount);
        return mInfo;
    }
}
