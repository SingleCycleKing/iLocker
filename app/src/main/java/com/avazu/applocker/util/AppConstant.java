package com.avazu.applocker.util;

public abstract class AppConstant {
    public static final String APP_KEY = "Avazu";

    public static final String APP_SETTING = "app_Setting";

    public static final String APP_LOCK_TYPE = "app_lock_type";
    public static final int APP_LOCK_PATTERN = 0;
    public static final int APP_LOCK_PIN = 1;

    public static final String APP_LOCK_PIN_PASSWORD = "app_pin_password";
    public static final String APP_LOCK_PATTERN_PASSWORD = "app_pattern_password";

    public static final String APP_LOCK_PATTERN_ENABLE = "lock_pattern_enable";

    public static final String APP_VIBRATE_ON_TOUCH = "app_vibrate";

    public static final String APP_LOCK_OPTION = "app_option";
    public static final int APP_LOCK_ONCE = 0;
    public static final int APP_LOCK_EVERY_TIME = 1;

    public static final String APP_FIRST_OPEN = "app_first_open";

    public static int APP_START_REQUEST = 100;
    public static int APP_START_SUCCEED = 101;
    public static int APP_START_FAILED = 102;
}
