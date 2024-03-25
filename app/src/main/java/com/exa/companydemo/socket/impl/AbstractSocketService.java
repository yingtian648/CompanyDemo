package com.exa.companydemo.socket.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author  lsh
 * @date  2024/3/21 15:49
 * @description 抽象socket服务
 */
public abstract class AbstractSocketService {

    protected Context mContext;
    protected Executor mExecutor = Executors.newFixedThreadPool(10);
    protected final List<SocketCallback> mCallbacks = new ArrayList<>();
    protected final Object mLock = new Object();
    protected Handler mHandler = new Handler(Looper.myLooper());
    private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String UUID_END = "-0000-1000-8000-00805F9B34FB";
    /**
     * 延时重试
     */
    protected static final int DELAY_RETRY = 3000;

    public void registerCallback(SocketCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(SocketCallback callback) {
        mCallbacks.remove(callback);
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

    public abstract void init(Context context);

    public abstract void startService();

    public abstract void sendMessage(String message);

    public abstract void release();
}
