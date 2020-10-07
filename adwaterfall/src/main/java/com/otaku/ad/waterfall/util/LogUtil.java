package com.otaku.ad.waterfall.util;

import android.util.Log;

public class LogUtil {
    private static final String TAG = "AD_WATERFALL@";
    public static boolean isDebug = true;
    public static void d(String tag, String msg) {
        if(isDebug) {
            Log.d(TAG + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if(isDebug) {
            Log.i(TAG + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if(isDebug) {
            Log.e(TAG + tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if(isDebug) {
            Log.v(TAG + tag, msg);
        }
    }
}
