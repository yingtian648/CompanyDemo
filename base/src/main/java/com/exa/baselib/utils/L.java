package com.exa.baselib.utils;

import android.util.Log;

public class L {
    public static String TAG = "--->";
    private static boolean isLog = true;

    public static void init(String tag, boolean isLog) {
        L.TAG = tag;
        L.isLog = isLog;
    }

    public static void d(String msg) {
        if (isLog)
            Log.d(TAG, "" + msg);
    }

    public static void e(String msg) {
        if (isLog)
            Log.e(TAG, "" + msg);
    }

    public static void v(String msg) {
        if (isLog)
            Log.v(TAG, "" + msg);
    }

    public static void e(String TAG, String msg) {
        if (isLog)
            Log.e(L.TAG + TAG, "" + msg);
    }

    public static void v(String TAG, String msg) {
        if (isLog)
            Log.v(L.TAG + TAG, "" + msg);
    }

    public static void d(String TAG, String msg) {
        if (isLog)
            Log.d(L.TAG + TAG, "" + msg);
    }

    public static void e(String msg, Throwable throwable) {
        if (isLog)
            Log.e(TAG, "" + msg + getThrowableLineNum(throwable));
    }

    public static void e(String TAG, String msg, Throwable throwable) {
        if (isLog)
            Log.d(L.TAG + TAG, "" + msg + getThrowableLineNum(throwable));
    }

    //获取异常行
    private static String getThrowableLineNum(Throwable throwable) {
        try {
            StackTraceElement[] trace = throwable.getStackTrace();
            // 下标为0的元素是上一行语句的信息, 下标为1的才是调用printLine的地方的信息
            StackTraceElement tmp = trace[0];
            return tmp.getClassName() + "." + tmp.getMethodName()
                    + "(" + tmp.getFileName() + ":" + tmp.getLineNumber() + ")";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前方法名
     */
    public static String getCurrentMethodName() {
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String methodName = stacks[level].getMethodName();
        return methodName;
    }

    public static String getCurrentClassName() {
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String className = stacks[level].getClassName();
        return className;
    }

    /**
     * 获取调用方法名
     */
    public static void dd() {
        StackTraceElement[] s = Thread.currentThread().getStackTrace();
        String methodName = s[3].getMethodName();
        d(methodName);
    }

    /**
     * 获取调用方法名
     */
    public static void dd(String msg) {
        StackTraceElement[] s = Thread.currentThread().getStackTrace();
        String methodName = s[3].getMethodName();
        d(methodName + " " + msg);
    }

    /**
     * 获取调用方法名
     */
    public static void dd(int msg) {
        StackTraceElement[] s = Thread.currentThread().getStackTrace();
        String methodName = s[3].getMethodName();
        d(methodName + " " + msg);
    }

    /**
     * 获取调用方法名
     */
    public static void dd(boolean state) {
        StackTraceElement[] s = Thread.currentThread().getStackTrace();
        String methodName = s[3].getMethodName();
        d(methodName + " " + state);
    }
}
