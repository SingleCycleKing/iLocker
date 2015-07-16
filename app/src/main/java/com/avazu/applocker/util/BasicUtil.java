package com.avazu.applocker.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;

import com.avazu.applocker.database.model.AppModel;

import java.util.ArrayList;
import java.util.List;

public class BasicUtil {

    public static ArrayList<AppModel> getAllApplication(Context context) {
        PackageManager manager = context.getPackageManager();
        ArrayList<AppModel> mModels = new ArrayList<>();
        List<ApplicationInfo> apps = manager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo mInfo : apps) {
            if (0 == (mInfo.flags & ApplicationInfo.FLAG_SYSTEM)) {
                AppModel mModel = new AppModel();
                mModel.setPackageName(mInfo.packageName);
                mModel.setLabel(mInfo.loadLabel(manager).toString());
                mModels.add(mModel);
            }
        }
        return mModels;
    }

    public static int makePressColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        r = (r - 30 < 0) ? 0 : r - 30;
        g = (g - 30 < 0) ? 0 : g - 30;
        b = (b - 30 < 0) ? 0 : b - 30;
        return Color.rgb(r, g, b);
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock mBook = Character.UnicodeBlock.of(c);
        return mBook == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || mBook == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || mBook == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || mBook == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || mBook == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || mBook == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static int spToPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
}
