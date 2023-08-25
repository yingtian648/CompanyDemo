package com.exa.baselib.utils;

public class Logs {
    public static String TAG = L.TAG;

    public static final boolean DEBUG = android.util.Log.isLoggable(TAG, android.util.Log.DEBUG) | true;

    private static final String TAG_DIVIDER = ": ";

    private Logs() {
    }

    public static final class Log extends Logs {
        private Log() {
        }
    }

    public static void setTag(String tag) {
        Logs.TAG = tag;
    }

    public static void v(String msg) {
        if (DEBUG) {
            android.util.Log.v(TAG, "" + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.v(TAG, tag + TAG_DIVIDER + msg);
        }
    }

    public static void v(String tag, String format, Object... obj) {
        if (DEBUG) {
            android.util.Log.v(TAG, tag + TAG_DIVIDER + format(format, obj));
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, "" + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, tag + TAG_DIVIDER + msg);
        }
    }

    public static void d(String tag, String format, Object... obj) {
        if (DEBUG) {
            android.util.Log.d(TAG, tag + TAG_DIVIDER + format(format, obj));
        }
    }

    public static void i(String msg) {
        android.util.Log.i(TAG, "" + msg);
    }

    public static void i(String tag, String msg) {
        android.util.Log.i(TAG, tag + TAG_DIVIDER + msg);
    }

    public static void i(String tag, String format, Object... obj) {
        android.util.Log.i(TAG, tag + TAG_DIVIDER + format(format, obj));
    }

    public static void w(String msg) {
        android.util.Log.w(TAG, "" + msg);
    }

    public static void w(String msg, Throwable tr) {
        android.util.Log.w(TAG, msg, tr);
    }

    public static void w(String tag, String msg) {
        android.util.Log.w(TAG, tag + TAG_DIVIDER + msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        android.util.Log.w(TAG, tag + TAG_DIVIDER + msg, tr);
    }

    public static void w(String tag, String format, Object... obj) {
        android.util.Log.w(TAG, tag + TAG_DIVIDER + format(format, obj));
    }

    public static void e(String msg) {
        android.util.Log.e(TAG, "" + msg);
    }

    public static void e(String msg, Throwable tr) {
        android.util.Log.e(TAG, msg, tr);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, tag + TAG_DIVIDER + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(TAG, tag + TAG_DIVIDER + msg, tr);
    }

    public static void e(String tag, String format, Object... obj) {
        android.util.Log.e(TAG, tag + TAG_DIVIDER + format(format, obj));
    }

    private static String format(String formatStr, Object... args) {
        try {
            return String.format(formatStr, args);
        } catch (Exception e) {
            return formatStr;
        }
    }
}
