package com.exa.companydemo.utils

import android.util.Log
import com.exa.baselib.utils.L

/**
 * @Author lsh
 * @Date 2023/7/11 15:42
 * @Description
 */
object LogUtil {
    private var TAG = L.TAG

    private val DEBUG = Log.isLoggable(TAG, Log.DEBUG) or true

    private var TAG_DIVIDER = ": "

    fun setTag(tag: String) {
        TAG = tag
    }

    fun setTagDivider(divider: String) {
        TAG_DIVIDER = divider
    }

    /**
     * 获取调用方法名
     */
    fun dd() {
        try {
            val s = Thread.currentThread().stackTrace
            val methodName = s[3].methodName
            w(methodName)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun dd(msg: String) {
        try {
            val s = Thread.currentThread().stackTrace
            val methodName = s[3].methodName
            w(methodName + TAG_DIVIDER + msg)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun dd(msg: Long) {
        try {
            val s = Thread.currentThread().stackTrace
            val methodName = s[3].methodName
            w(methodName + TAG_DIVIDER + msg)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun v(msg: String) {
        if (DEBUG) {
            Log.v(TAG, "" + msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (DEBUG) {
            Log.v(TAG, tag + TAG_DIVIDER + msg)
        }
    }

    fun v(tag: String, format: String, vararg obj: Any?) {
        if (DEBUG) {
            Log.v(TAG, tag + TAG_DIVIDER + format(format, obj))
        }
    }

    fun d(msg: String) {
        if (DEBUG) {
            Log.d(TAG, "" + msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (DEBUG) {
            Log.d(TAG, tag + TAG_DIVIDER + msg)
        }
    }

    fun d(tag: String, format: String, vararg obj: Any?) {
        if (DEBUG) {
            Log.d(TAG, tag + TAG_DIVIDER + format(format, obj))
        }
    }

    fun i(msg: String) {
        Log.i(TAG, "" + msg)
    }

    fun i(tag: String, msg: String) {
        Log.i(TAG, tag + TAG_DIVIDER + msg)
    }

    fun i(tag: String, format: String, vararg obj: Any?) {
        Log.i(TAG, tag + TAG_DIVIDER + format(format, obj))
    }

    fun w(msg: String) {
        Log.w(TAG, "" + msg)
    }

    fun w(msg: String?, tr: Throwable?) {
        Log.w(TAG, msg, tr)
    }

    fun w(tag: String, msg: String) {
        Log.w(TAG, tag + TAG_DIVIDER + msg)
    }

    fun w(tag: String, msg: String, tr: Throwable?) {
        Log.w(TAG, tag + TAG_DIVIDER + msg, tr)
    }

    fun w(tag: String, format: String, vararg obj: Any?) {
        Log.w(TAG, tag + TAG_DIVIDER + format(format, obj))
    }

    fun e(msg: String) {
        Log.e(TAG, "" + msg)
    }

    fun e(msg: String?, tr: Throwable?) {
        Log.e(TAG, msg, tr)
    }

    fun e(tag: String, msg: String) {
        Log.e(TAG, tag + TAG_DIVIDER + msg)
    }

    fun e(tag: String, msg: String, tr: Throwable?) {
        Log.e(TAG, tag + TAG_DIVIDER + msg, tr)
    }

    fun e(tag: String, format: String, vararg obj: Any?) {
        Log.e(TAG, tag + TAG_DIVIDER + format(format, obj))
    }

    private fun format(formatStr: String, vararg args: Any): String {
        return try {
            String.format(formatStr, *args)
        } catch (e: Exception) {
            formatStr
        }
    }
}