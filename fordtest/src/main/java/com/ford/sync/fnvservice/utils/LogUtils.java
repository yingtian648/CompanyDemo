/**
 * @file LogUtils.java
 * @author xiarupeng
 * @email xia.rupeng@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "FNV.";

    public static void v(String message, Object... args) {
        log(Log.VERBOSE, getMessage(message, args));
    }

    public static void d(String message, Object... args) {
        log(Log.DEBUG, getMessage(message, args));
    }

    public static void i(String message, Object... args) {
        log(Log.INFO, getMessage(message, args));
    }

    public static void w(String message, Object... args) {
        log(Log.WARN, getMessage(message, args));
    }

    public static void e(String message, Object... args) {
        log(Log.ERROR, getMessage(message, args));
    }

    private static void log(int level, String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = getClassName(stackTrace);
        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG + className, message);
                break;
            case Log.DEBUG:
                Log.d(TAG + className, message);
                break;
            case Log.INFO:
                Log.i(TAG + className, message);
                break;
            case Log.WARN:
                Log.w(TAG + className, message);
                break;
            case Log.ERROR:
                Log.e(TAG + className, message);
                break;
          default:
            throw new IllegalStateException("Unexpected value: " + level);
        }
    }

    private static String getMessage(String message, Object... args) {
        return args.length > 0 ? String.format(message, args) : message;
    }

    private static String getClassName(StackTraceElement[] stackTrace) {
        for (int i = 2; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (!className.equals(LogUtils.class.getName())) {
                return className.substring(className.lastIndexOf(".") + 1);
            }
        }
        return "Unknown";
    }
}