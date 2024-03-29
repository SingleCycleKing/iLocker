package com.sample.applocker.util;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.TypedValue;

import com.sample.applocker.database.model.AppModel;
import com.sample.applocker.view.widget.pattern.PatternView;

import java.util.ArrayList;
import java.util.List;

public class BasicUtil {

    public static ArrayList<AppModel> getAllApplication(Context context, ArrayList<AppModel> selectedInfoList) throws PackageManager.NameNotFoundException {
        PackageManager manager = context.getPackageManager();
        ArrayList<AppModel> mModels = new ArrayList<>();
        List<ApplicationInfo> apps = manager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo mInfo : apps) {
            if (0 == (mInfo.flags & ApplicationInfo.FLAG_SYSTEM)) {
                AppModel mModel = new AppModel();
                mModel.setPackageName(mInfo.packageName);
                mModel.setIconId(mInfo.icon);
                mModel.setLabel(mInfo.loadLabel(manager).toString());
                boolean hasItem = false;
                for (int i = 0; i < selectedInfoList.size(); i++) {
                    if (selectedInfoList.get(i).getPackageName().equals(mModel.getPackageName())) {
                        hasItem = true;
                        break;
                    }
                }
                mModel.setIsSelected(hasItem);
                mModels.add(mModel);
            }
        }
        return mModels;
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

    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static Bitmap getWallpaper(Context context) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        return ((BitmapDrawable) wallpaperManager.getDrawable()).getBitmap();
    }


    public static Bitmap blur(Context context, Bitmap sentBitmap, int radius) {
        Bitmap bitmap = Bitmap.createScaledBitmap(sentBitmap, sentBitmap.getWidth() / 2, sentBitmap.getHeight() / 2, false);
        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(renderScript, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }


    public static String pattern2String(List<PatternView.Cell> cells) {
        StringBuilder sb = new StringBuilder();
        if (cells != null) {
            for (int i = 0; i < cells.size(); i++) {
                if (i != 0) {
                    sb.append(',');
                }
                sb.append(cells.get(i).toNumber());
            }
        }
        return sb.toString();
    }

    public static String passwordToString(List<Integer> password) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < password.size(); i++) {
            sb.append(password.get(i));
            if (i != password.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
