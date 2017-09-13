package com.march.socialsdk.helper;

import android.util.Log;

/**
 * CreateAt : 2016/12/22
 * Describe : log
 *
 * @author chendong
 */

public class PlatformLog {

    public static boolean DEBUG = true;

    private static String getMsg(Object msg) {
        return msg == null ? "null" : msg.toString();
    }

    public static void e(String tag, Object msg) {
        if (DEBUG)
            Log.e(tag + "|platform", getMsg(msg));
    }

    public static void e(String tag, Object... msg) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (Object o : msg) {
                sb.append(" ").append(getMsg(o)).append(" ");
            }
            Log.e(tag + "|platform", sb.toString());
        }
    }

    public static void e(Object msg) {
        if (DEBUG)
            Log.e("platform", getMsg(msg));
    }
}
