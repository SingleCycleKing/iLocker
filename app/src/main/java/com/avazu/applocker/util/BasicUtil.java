package com.avazu.applocker.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;

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
}
