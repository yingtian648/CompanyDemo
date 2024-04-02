package com.exa.companydemo.socket.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.core.app.ActivityCompat;

/**
 * @author lsh
 * @date 2024/3/21 15:49
 * @description 抽象socket服务
 */
public abstract class AbstractService {

    protected static final String TAG = "AbstractService";
    protected Context mContext;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_BLUETOOTH = 2;
    protected Executor mExecutor = Executors.newFixedThreadPool(10);
    protected final List<Callback> mCallbacks = new ArrayList<>();
    protected final Object mLock = new Object();
    protected Handler mHandler = new Handler(Looper.myLooper());
    private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String MY_UUID = "FCD9FA90-825F-4DB1-AAF4-3D1B19B597C8";
    /**
     * 延时重试
     */
    protected static final int DELAY_RETRY = 3000;
    @SuppressLint("StaticFieldLeak")
    private static AbstractService mSocketService;
    private static int mType = TYPE_WIFI;

    public static int getType() {
        return mType;
    }

    public static AbstractService switchServiceType(Context context,
                                                    int type,
                                                    Callback callback) {
        Log.i(TAG, "switchServiceType: " + type);
        mType = type;
        if (type == TYPE_WIFI) {
            BtSocketServiceUtil.getInstance().release();
            mSocketService = WifiSocketServiceUtil.getInstance();
            mSocketService.registerCallback(callback);
            mSocketService.init(context);
            mSocketService.startService();
        } else if (type == TYPE_BLUETOOTH) {
            if (checkBtPermission(context)) {
                WifiSocketServiceUtil.getInstance().release();
                mSocketService = BtSocketServiceUtil.getInstance();
                mSocketService.registerCallback(callback);
                mSocketService.init(context);
                mSocketService.startService();
            } else {
                callback.onError("未授予蓝牙连接权限");
                return null;
            }
        } else {
            Log.e(TAG, "switchServiceType: 未知类型");
            return null;
        }
        return mSocketService;
    }

    public void registerCallback(Callback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    public UUID getUUID(String name) {
        if (!TextUtils.isEmpty(name)) {
            return UUID.fromString(MY_UUID);
        }
        return UUID.fromString(UUID_STRING);
    }

    public abstract void init(Context context);

    public abstract void startService();

    public abstract void sendMessage(String message);

    public abstract void release();

    private static boolean checkBtPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED);
        } else {
            return (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED);
        }
    }

    public interface Callback {

        /**
         * 服务端启动
         *
         * @param port 端口
         */
        void onStarted(String port);

        /**
         * 连接成功
         *
         * @param msg 消息
         */
        void onError(String msg);

        /**
         * 客户端连接
         *
         * @param address 地址
         * @param port    端口
         */
        void onClientConnected(String address, int port);

        /**
         * 接收到消息
         *
         * @param msg 消息
         */
        void onReceived(String msg);
    }
}
