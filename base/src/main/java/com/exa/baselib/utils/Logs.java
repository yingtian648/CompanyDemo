package com.exa.baselib.utils;

import android.util.Log;

public class Logs {
    public static String TAG = "CarOTAService";

    public static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG) | true;

    private static final String TAG_DIVIDER = ": ";

    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, "" + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(TAG, tag + TAG_DIVIDER + msg);
        }
    }

    public static void v(String tag, String format, Object... obj) {
        if (DEBUG) {
            Log.v(TAG, tag + TAG_DIVIDER + format(format, obj));
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, "" + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(TAG, tag + TAG_DIVIDER + msg);
        }
    }

    public static void d(String tag, String format, Object... obj) {
        if (DEBUG) {
            Log.d(TAG, tag + TAG_DIVIDER + format(format, obj));
        }
    }

    public static void i(String msg) {
        Log.i(TAG, "" + msg);
    }

    public static void i(String tag, String msg) {
        Log.i(TAG, tag + TAG_DIVIDER + msg);
    }

    public static void i(String tag, String format, Object... obj) {
        Log.i(TAG, tag + TAG_DIVIDER + format(format, obj));
    }

    public static void w(String msg) {
        Log.w(TAG, "" + msg);
    }

    public static void w(String msg, Throwable tr) {
        Log.w(TAG, msg, tr);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG, tag + TAG_DIVIDER + msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        Log.w(TAG, tag + TAG_DIVIDER + msg, tr);
    }

    public static void w(String tag, String format, Object... obj) {
        Log.w(TAG, tag + TAG_DIVIDER + format(format, obj));
    }

    public static void e(String msg) {
        Log.e(TAG, "" + msg);
    }

    public static void e(String msg, Throwable tr) {
        Log.e(TAG, msg, tr);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, tag + TAG_DIVIDER + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(TAG, tag + TAG_DIVIDER + msg, tr);
    }

    public static void e(String tag, String format, Object... obj) {
        Log.e(TAG, tag + TAG_DIVIDER + format(format, obj));
    }

    private static String format(String formatStr, Object... args) {
        try {
            return String.format(formatStr, args);
        } catch (Exception e) {
            return formatStr;
        }
    }
}
