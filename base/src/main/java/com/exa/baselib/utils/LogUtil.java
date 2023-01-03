/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.exa.baselib.utils;

import android.util.Log;

public final class LogUtil {

    private static final String TAG = "CarLocationService";
    private static boolean sGnssDebuggable = true;

    private LogUtil() {
    }

    /**
     * Warn level log.
     *
     * @param msg String
     */
    public static void info(String msg) {
        Log.i(TAG, msg);
    }

    /**
     * Warn level log.
     *
     * @param msg String
     */
    public static void warn(String msg) {
        Log.w(TAG, msg);
    }

    /**
     * Warn level log.
     *
     * @param msg       String
     * @param exception Exception trace
     */
    public static void warn(String msg, Exception exception) {
        Log.w(TAG, msg, exception);
    }

    /**
     * Debug level log.
     *
     * @param msg String
     */
    public static void debug(String msg) {
        if (sGnssDebuggable) {
            Log.d(TAG, msg);
        }
    }

    /**
     * Error level log.
     *
     * @param msg String
     */
    public static void error(String msg) {
        Log.e(TAG, "error:" + msg);
    }

    /**
     * What a Terrible Failure.
     *
     * @param exception Exception
     */
    public static void wtf(Exception exception) {
        Log.wtf(TAG, exception);
    }
}
