package com.yinfeng.wypzh.utils;

import android.util.Log;

public class LogUtil {

    private static final boolean isDebug = true;

    private static final String TAG_APPLICATION = "com.yinfeng.wypzh";

    public static void error(String TAG, String content) {
        if (isDebug)
            Log.e(TAG, content);
    }

    public static void error(String content) {
        error(TAG_APPLICATION, content);
    }

    public static void info(String TAG, String content) {
        if (isDebug)
            Log.i(TAG, content);
    }

    public static void info(String content) {
        info(TAG_APPLICATION, content);
    }

}
