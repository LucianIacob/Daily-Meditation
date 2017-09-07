package com.dailymeditation.android.utils;

import android.util.Log;

import com.dailymeditation.android.BuildConfig;

/**
 * Created with <3 by liacob & <Pi> on 04-Sep-17.
 */

@SuppressWarnings("unused")
public class LogUtils {

    public static void logE(String tag, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, "logE: ", e);
        }
    }

    public static void logI(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }
}
