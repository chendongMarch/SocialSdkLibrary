package com.march.socialsdk.utils;

import android.os.Looper;
import android.util.Log;

import com.march.socialsdk.BuildConfig;

/**
 * CreateAt : 2016/12/22
 * Describe : log
 *
 * @author chendong
 */

public class LogUtils {

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

    public static void t(Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    private static String getHeaderInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (null == stackTrace) {
            return "";
        } else {
            StackTraceElement destStackTraceElement = null;
            boolean next = true;
            int var4 = stackTrace.length;
            int lineNumber;
            for (lineNumber = 0; lineNumber < var4; ++lineNumber) {
                StackTraceElement traceElement = stackTrace[lineNumber];
                if (traceElement.getClassName().equals(LogUtils.class.getName())) {
                    next = false;
                } else if (!next) {
                    destStackTraceElement = traceElement;
                    break;
                }
            }

            if (null == destStackTraceElement) {
                return "";
            } else {
                String className = destStackTraceElement.getClassName();
                String methodName = destStackTraceElement.getMethodName();
                lineNumber = destStackTraceElement.getLineNumber();
                if (lineNumber < 0) {
                    lineNumber = 0;
                }
                String threadInfo = "Thread: " + Thread.currentThread().getName();
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    threadInfo = threadInfo + "(UI线程), ";
                } else {
                    threadInfo = threadInfo + "(Work线程), ";
                }

                String classAndMethodInfo = "[(" + className + ".java" + ":" + lineNumber + ")#" + methodName + "]\n";
                return threadInfo + classAndMethodInfo;
            }
        }
    }

}
