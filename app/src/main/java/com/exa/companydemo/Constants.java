package com.exa.companydemo;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Constants {
    public static String FILE_DIR_MUSIC = "/storage/emulated/0/Music/Music";
    public static String FILE_DIR_IMAGE = "/storage/emulated/0/Music/Image";

    private static ExecutorService singlePool, fixPool, scheduledPool;
    private static final int MAX_FIX_POOL_SIZE = 5;//最大核心线程数

    private static Handler handler;

    public static void init() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static Handler getHandler() {
        return handler;
    }

    /**
     * 单线程
     * 适用于一个一个执行
     *
     * @return
     */
    public static ExecutorService getSinglePool() {
        if (singlePool == null) {
            singlePool = Executors.newSingleThreadExecutor();
        }
        return singlePool;
    }

    /**
     * 多线程
     * 只有核心线程
     *
     * @return
     */
    public static ExecutorService getFixPool() {
        if (fixPool == null) {
            fixPool = Executors.newFixedThreadPool(MAX_FIX_POOL_SIZE);
        }
        return fixPool;
    }

    /**
     * 多线程
     * 核心线程,非核心线程
     *
     * @return
     */
    public static ExecutorService getScheduledPool() {
        if (scheduledPool == null) {
            scheduledPool = Executors.newScheduledThreadPool(MAX_FIX_POOL_SIZE);
        }
        return scheduledPool;
    }
}
