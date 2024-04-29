package com.exa.lsh.library;

import android.os.SystemClock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lsh
 * @date 2024/4/28 14:30
 * @description
 */
public class ThreadPoolManager {
    private ThreadPoolManager() {
    }

    /**
     * 获取单线程的线程池
     */
    public static ExecutorService getSingleThreadPool() {
        return getFixThreadPool(1);
    }

    /**
     * 获取多线程的线程池
     */
    public static ExecutorService getFixThreadPool(int corePoolSize) {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadFactory factory = r -> new Thread(r, "Thread_" + SystemClock.elapsedRealtime());
        return new ThreadPoolExecutor(corePoolSize, corePoolSize, 0L,
                TimeUnit.MILLISECONDS, workQueue, factory);
    }
}
