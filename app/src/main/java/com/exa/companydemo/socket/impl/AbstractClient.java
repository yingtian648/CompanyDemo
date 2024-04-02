package com.exa.companydemo.socket.impl;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author lsh
 * @date 2024/3/21 18:12
 * @description 抽象socket客户端
 */
public abstract class AbstractClient {
    protected static final String TAG = "AbstractClient";
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_BLUETOOTH = 2;
    protected ExecutorService mExecutor = newFixedThreadPool(10);
    protected final List<Callback> mCallbacks = new ArrayList<>();
    private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String MY_UUID = "FCD9FA90-825F-4DB1-AAF4-3D1B19B597C8";
    private Application mApplication;
    private static AbstractClient mInstance;

    public static AbstractClient getInstance(int type) {
        if (type == TYPE_WIFI) {
            mInstance = WifiSocketClientUtil.getInstance();
        } else if (type == TYPE_BLUETOOTH) {
            mInstance = BtSocketClientUtil.getInstance();
        }
        return mInstance;
    }

    public interface Callback {
        /**
         * 连接成功
         *
         * @param ip   ip
         * @param port 端口
         */
        void onConnected(String ip, int port);

        /**
         * 收到消息
         *
         * @param msg 消息
         */
        void onReceived(String msg);

        /**
         * 错误
         *
         * @param msg 错误信息
         */
        void onError(String msg);
    }

    public Application getApplication() {
        synchronized (AbstractClient.class) {
            if (mApplication == null) {
                try {
                    Class<?> clazz = Class.forName("android.app.ActivityThread");
                    Method method = clazz.getMethod("currentApplication");
                    mApplication = (Application) method.invoke(null);
                } catch (Exception e) {
                    Log.e(TAG, "getApplication 失败", e);
                }
            }
        }
        return mApplication;
    }

    public void registerCallback(Callback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public UUID getUUID(String name) {
        if (!TextUtils.isEmpty(name)) {
            return UUID.fromString(MY_UUID);
        }
        return UUID.fromString(UUID_STRING);
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    public void connect() {
    }

    public void connect(String ip, int port) {
    }

    public void init() {
    }

    public abstract void sendMessage(final String message);

    public abstract void release();

    public void onError(String message) {
        Log.e(TAG, "onError: " + message);
        for (Callback callback : mCallbacks) {
            callback.onError(message);
        }
    }

    public void onConnected(String ip, int port) {
        Log.w(TAG, "onConnected");
        for (Callback callback : mCallbacks) {
            callback.onConnected(ip, port);
        }
    }

    public void onReceiveMessage(final String message) {
        Log.w(TAG, "收到服务端消息: " + message);
        for (Callback callback : mCallbacks) {
            callback.onReceived(message);
        }
    }
}
