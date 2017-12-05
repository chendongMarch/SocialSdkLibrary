package com.march.socialsdk.helper;

import android.util.Log;

import com.march.socialsdk.BuildConfig;

/**
 * CreateAt : 2016/12/22
 * Describe : log
 *
 * @author chendong
 */

public class PlatformLog {

    public static final String TAG = "social-sdk";
    public static boolean DEBUG = BuildConfig.DEBUG;

    private static String getMsg(Object msg) {
        return msg == null ? "null" : msg.toString();
    }

    public static void e(String tag, Object msg) {
        if (DEBUG)
            Log.e(tag + "|" + TAG, getMsg(msg));
    }

    public static void e(String tag, Object... msg) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (Object o : msg) {
                sb.append(" ").append(getMsg(o)).append(" ");
            }
            Log.e(tag + "|" + TAG, sb.toString());
        }
    }

    public static void e(Object msg) {
        if (DEBUG)
            Log.e(TAG, getMsg(msg));
    }
}
