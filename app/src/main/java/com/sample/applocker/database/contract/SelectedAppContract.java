package com.sample.applocker.database.contract;

import android.provider.BaseColumns;

public class SelectedAppContract {
    public static abstract class SelectedApp implements BaseColumns {
        public static final String TABLE_NAME = "selected_app";
        public static final String COLUMN_NAME_LABEL = "app_label";
        public static final String COLUMN_NAME_PACKAGE = "app_package";
    }
}
