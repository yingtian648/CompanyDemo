package com.exa.baselib;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseConstants {
    public static String AUTHORITY = "com.media.my";
    public static final String ACTION_MY_PROVIDER_SCAN_FINISH = "com.media.mine.finish";

    public static final Uri CUSTOMER_URI = Uri.parse("content://" + AUTHORITY + "/customer");

    public static final String FILE_DIR_MUSIC = "/storage/emulated/0/Music/Music";
    public static final String FILE_DIR_IMAGE = "/storage/emulated/0/Music/Image";
    public static final String FILE_DIR_VIDEO = "/storage/emulated/0/Music/Video";
    public static final String FILE_DIR = "/storage/emulated/0/Music";


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
     * 系统媒体内容提供者搜索类型
     */
    public static class SystemMediaType {
        public static final int Audio = 1;// 音频
        public static final int Video = 2;// 视频
        public static final int Image = 3;// 图片
        public static final int AudioThumb = 4;// 音频缩略图
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
