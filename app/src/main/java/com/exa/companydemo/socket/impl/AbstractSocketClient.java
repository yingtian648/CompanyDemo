package com.exa.companydemo.socket.impl;

import android.app.Application;
import android.content.Context;
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
public abstract class AbstractSocketClient {
    protected static final String TAG = "AbstractSocketClient";
    protected ExecutorService mExecutor = newFixedThreadPool(10);
    protected final List<Callback> mCallbacks = new ArrayList<>();
    private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String UUID_END = "-0000-1000-8000-00805F9B34FB";
    private Application mApplication;

    public interface Callback {
        void onConnected(String ip, int port);

        void onReceived(String msg);

        void onError(String msg);
    }

    public Application getApplication() {
        synchronized (AbstractSocketClient.class) {
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
            name = name.replace(" ", "").trim();
            int ascii = 0;
            for (int i = 0; i < name.length(); i++) {
                ascii += name.charAt(i);
            }
            StringBuilder asciiStr = new StringBuilder(String.valueOf(ascii));
            if (asciiStr.length() < 8) {
                for (int i = 0; i < 8 - asciiStr.length(); i++) {
                    asciiStr.insert(0, "0");
                }
            }
            name = asciiStr + UUID_END;
            return UUID.fromString(name);
        }
        return UUID.fromString(UUID_STRING);
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    public abstract void sendMessageToServer(final String message);

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
