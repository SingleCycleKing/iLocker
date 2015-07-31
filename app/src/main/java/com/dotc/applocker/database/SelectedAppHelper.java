package com.dotc.applocker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dotc.applocker.database.contract.SelectedAppContract;
import com.dotc.applocker.database.model.AppModel;

import java.util.ArrayList;

public class SelectedAppHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SelectedApp.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SelectedAppContract.SelectedApp.TABLE_NAME + " (" +
                    SelectedAppContract.SelectedApp._ID + " INTEGER PRIMARY KEY," +
                    SelectedAppContract.SelectedApp.COLUMN_NAME_PACKAGE + TEXT_TYPE + COMMA_SEP +
                    SelectedAppContract.SelectedApp.COLUMN_NAME_LABEL + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SelectedAppContract.SelectedApp.TABLE_NAME;

    public SelectedAppHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateAllData(ArrayList<AppModel> mData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(SelectedAppContract.SelectedApp.TABLE_NAME, null, null);
        for (AppModel mModel : mData) {
            insert(mModel);
        }
        sqLiteDatabase.close();
    }

    public void delete(String name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(SelectedAppContract.SelectedApp.TABLE_NAME, SelectedAppContract.SelectedApp.COLUMN_NAME_PACKAGE + "=?", new String[]{name});
        sqLiteDatabase.close();
    }

    public void insert(AppModel mApplication) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SelectedAppContract.SelectedApp.COLUMN_NAME_PACKAGE, mApplication.getPackageName());
        values.put(SelectedAppContract.SelectedApp.COLUMN_NAME_LABEL, mApplication.getLabel());
        sqLiteDatabase.insert(SelectedAppContract.SelectedApp.TABLE_NAME, null, values);
        sqLiteDatabase.close();
    }

    public ArrayList<AppModel> queryAll() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<AppModel> appModels = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SelectedAppContract.SelectedApp.TABLE_NAME, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            AppModel appModel = new AppModel();
            appModel.setPackageName(cursor.getString(1));
            appModel.setLabel(cursor.getString(2));
            appModels.add(appModel);
            if (!cursor.isLast()) cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return appModels;
    }

    public AppModel queryData(String packageName) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(SelectedAppContract.SelectedApp.TABLE_NAME, new String[]{SelectedAppContract.SelectedApp.COLUMN_NAME_PACKAGE}, "app_package=?", new String[]{packageName}, null, null, null);
        cursor.moveToFirst();
        AppModel appModel = new AppModel();
        appModel.setPackageName(cursor.getString(1));
        appModel.setLabel(cursor.getString(2));
        cursor.close();
        sqLiteDatabase.close();
        return appModel;
    }

}
