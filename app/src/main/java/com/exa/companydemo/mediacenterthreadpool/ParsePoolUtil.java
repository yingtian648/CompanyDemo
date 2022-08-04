/*
 * Copyright (C) 2022 author cao.zhi@zlingsmart.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exa.companydemo.mediacenterthreadpool;


import java.lang.Runnable;

import android.text.TextUtils;
import android.util.Log;

import com.exa.baselib.utils.L;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link ParsePoolUtil} is a util what deal to the parse file task.
 */
public class ParsePoolUtil extends PoolUtil<Task> {

    private static final String TAG = L.TAG;
    // control finish the all parse Runnable.
    private volatile boolean mNeedShutdown = false;
    // the pool running state.
    private volatile boolean mHasRunning = false;
    // The current scan folder.
    private volatile String mNewTaskPath;
    // the max thread num in the parseFileThreadPool.\
    private static final int CORE_THREAD_SIZE = 2;
    private static ParsePoolUtil mInstance;
    // save the all parse file task .
    private final BlockingDeque<Task> mParseTaskBlockingDeque =
            new LinkedBlockingDeque<>();
    // save the all Running Runnable .
    private final Map<Integer, RunningState> mRunStates = new HashMap();
    private ScanCompleteCallback scanCompleteCallback;
    // the num add the task .
    private int count;
    // the num execute the task .
    private int num;
    private final Object lock = new Object();

    public class ParseRunnable implements Runnable {

        int id = 0;

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (!mNeedShutdown) {
                takeFile();
                synchronized (lock) {
                    num++;
                }
            }
            boolean status = removeRunningMap(id);
            Log.d(TAG, "the end run id = " + id + " finish status is " + status);
            Log.d(TAG, "mRunStates size = " + mRunStates.size());
            boolean isRunning = !mRunStates.isEmpty();
            if (isRunning) {
                mHasRunning = false;
                if (scanCompleteCallback != null) {
                    scanCompleteCallback.onCompleted();
                }
                Log.i(TAG, "ParseRunnable all task is finished. the task count " + num);
                count = 0;
                num = 0;
            } else {
                Log.d(TAG, "ParseRunnable have some task is running:::" + mRunStates.keySet());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (scanCompleteCallback != null) {
                    scanCompleteCallback.onCompleted();
                }
                L.d("shutdownNow");
                mThreadPool.shutdownNow();
                mParseTaskBlockingDeque.clear();
                L.d("mThreadPool.shutdownNow()::" + (mThreadPool == null ? "null" : mThreadPool.isShutdown()));
            }
        }
    }

    /**
     * the method add the Runnable to the thread pool.
     *
     * @return
     */
    public synchronized boolean start() {
        if (mHasRunning) {
            Log.i(TAG, "start,but is running");
        }
        if (mThreadPool == null || mThreadPool.isShutdown()) {
            mThreadPool = myExecutorService();
        }
        if (mNeedShutdown) {
            mNeedShutdown = false;
        }
        num = 0;
        int needThreadNum = CORE_THREAD_SIZE - mRunStates.size();
        Log.i(TAG, "start, needThreadNum = " + needThreadNum);
        for (int i = 0; i < needThreadNum; i++) {
            ParseRunnable mParseRunnable = new ParseRunnable();
            int id = mParseRunnable.hashCode();
            mParseRunnable.setId(id);
            RunningState runningState = new RunningState(id);
            runningState.isRunning = true;
            Log.i(TAG, "start, add run id = " + id);
            addRunningMap(id, runningState);
            mThreadPool.execute(mParseRunnable);
        }
        mHasRunning = true;
        return true;
    }

    /**
     * when the Runnable finish.
     * call the method remove recording the Running state.
     *
     * @return
     */
    public boolean removeRunningMap(int id) {
        synchronized (mRunStates) {
            return mRunStates.remove(id) != null;
        }
    }

    /**
     * when the Runnable start.
     * call the method add recording the Running state.
     *
     * @return
     */
    public void addRunningMap(int id, RunningState runningState) {
        synchronized (mRunStates) {
            mRunStates.put(id, runningState);
        }
    }

    /**
     * Gets the thread pool policy
     *
     * @return {@ExecutorService} the thread pool policy
     */
    @Override
    protected ExecutorService myExecutorService() {
        return Executors.newScheduledThreadPool(CORE_THREAD_SIZE);
    }

    /**
     * add the the parsing media file task
     *
     * @param task the parsing media file task
     */
    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("put params to queue,but the param is null");
        }
        mParseTaskBlockingDeque.addLast(task);
        count++;
        if (mNeedShutdown) {
            Log.w(TAG, "putParseFile, num " + count + " can't  execute");
        }
    }

    private ParsePoolUtil() {
        super();
    }


    /**
     * get the task.
     */
    private void takeFile() {
        if (!mParseTaskBlockingDeque.isEmpty()) {
            try {
                Task task = mParseTaskBlockingDeque.take();
                if (task != null && task.getCallback() != null) {
                    task.getCallback().onProcess(task);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnCompletedCallback(ScanCompleteCallback scanCompleteCallback) {
        this.scanCompleteCallback = scanCompleteCallback;
    }


    public static ParsePoolUtil getInstance() {
        if (mInstance == null) {
            synchronized (ParsePoolUtil.class) {
                if (mInstance == null) {
                    mInstance = new ParsePoolUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * shut down the scanned dir path
     * only the task Path is the newest scanned dir path or the path is empty,
     * stop all ready task.
     *
     * @param taskPath
     */
    public void shutdown(String taskPath) {
        Log.w(TAG, "shutdown taskPath = " + taskPath + ", mNewTaskPath = " + mNewTaskPath);
        if (TextUtils.isEmpty(taskPath) || taskPath.equals(mNewTaskPath)) {
            mNeedShutdown = true;
        } else {
            Log.w(TAG, "shutdown fail ");
        }
    }

    public boolean hasRunning() {
        return mHasRunning;
    }

    /**
     * update the newest scanned dir path
     *
     * @param newTaskPath
     */
    public void updateTaskPath(String newTaskPath) {
        mNewTaskPath = newTaskPath;
    }

    private synchronized void release() {
        mNeedShutdown = false;
        mHasRunning = false;
        Log.d(TAG, "release.");
    }

    public interface ScanCompleteCallback {
        void onCompleted();
    }

    protected class RunningState {
        int mId;
        boolean isRunning;

        RunningState(int id) {
            mId = id;
        }
    }

}
