package com.exa.companydemo.socket.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.EXTRA_STATE;

/**
 * @Author lsh
 * @Date 2024/3/21 10:16
 * @Description
 */
public class BtSocketServiceUtil extends AbstractService {
    private static final String TAG = "BtSocketServiceUtil";
    private BluetoothSocket mClientSocket;
    private BluetoothAdapter mAdapter;
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final BtSocketServiceUtil mInstance = new BtSocketServiceUtil();
    private boolean isRegistered = false;
    private boolean isStarted = false;
    private boolean isReleased = false;
    private BluetoothServerSocket mServerSocket;
    private UUID mUuid;

    public static BtSocketServiceUtil getInstance() {
        return mInstance;
    }

    @Override
    public void init(Context context) {
        this.mContext = context;
        synchronized (mLock) {
            if (!isRegistered) {
                isRegistered = true;
                registerReceiver(context);
            }
        }
    }

    private BtSocketServiceUtil() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(mBTReceiver, filter);
    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(EXTRA_STATE, 0);
            Log.d(TAG, "ACTION_STATE_CHANGED state=" + state);
            // 判断蓝牙功能已经开启
            if (state == BluetoothAdapter.STATE_ON) {
                startService();
            }
        }
    };

    /**
     * 1.通过蓝牙设备获取BluetoothSocket
     * 2.通过BluetoothSocket获取输入输出流
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Override
    public void startService() {
        Log.i(TAG, "startService");
        mExecutor.execute(() -> {
            synchronized (mLock) {
                if (isStarted) {
                    Log.w(TAG, "startService: isStarted");
                    onStarted(mUuid.toString());
                    return;
                }
            }
            try {
                if (mAdapter == null) {
                    onError("不支持蓝牙功能");
                    return;
                }
                if (!mAdapter.isEnabled()) {
                    mAdapter.enable();
                    Log.w(TAG, "startBluetoothSocketService bluetooth is not enabled");
                    return;
                }
                mUuid = getUUID(mAdapter.getName());
                Log.i(TAG, "startService uuid: " + mUuid.toString());
                mServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                        NAME_INSECURE, mUuid);
                onStarted(mUuid.toString());
                while (!isReleased) {
                    //阻塞式，直到获取socket
                    mClientSocket = mServerSocket.accept();
                    onClientConnected(mClientSocket);
                    waitServerMessage();
                    Log.i(TAG, "mClientSocket connected");
                }
            } catch (IOException e) {
                Log.e(TAG, "startBluetoothSocketService: ", e);
                onError("startBluetoothSocketService error ,delay 3s retry");
                mHandler.postDelayed(this::startService, DELAY_RETRY);
            }
        });
    }

    private void waitServerMessage() {
        mExecutor.execute(() -> {
            try {
                // 获取输入流
                InputStream is = mClientSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    String msg = new String(buffer, 0, len);
                    onReceiveMessage(msg);
                }
            } catch (IOException e) {
                mClientSocket = null;
                Log.e(TAG, "waitServerMessage: ", e);
                Thread.currentThread().interrupt();
            }
        });
    }

    private void onError(String msg) {
        Log.e(TAG, "onError: " + msg);
        for (Callback callback : mCallbacks) {
            callback.onError(msg);
        }
    }

    private void onStarted(String uuid) {
        isStarted = true;
        for (Callback callback : mCallbacks) {
            callback.onStarted(uuid);
        }
    }

    @SuppressLint("MissingPermission")
    private void onClientConnected(BluetoothSocket socket) {
        for (Callback callback : mCallbacks) {
            callback.onClientConnected(socket.getRemoteDevice().getName(), 0);
        }
    }

    private void onReceiveMessage(String msg) {
        Log.w(TAG, "收到客户端消息: " + msg);
        for (Callback callback : mCallbacks) {
            callback.onReceived(msg);
        }
    }

    @Override
    public void sendMessage(final String message) {
        if (mClientSocket != null) {
            mExecutor.execute(() -> {
                try {
                    mClientSocket.getOutputStream().write(message.getBytes());
                    Log.i(TAG, "sendMessageToServer: " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w(TAG, "sendMessageToServer: ", e);
                }
            });
        } else {
            Log.w(TAG, "sendMessageToServer: clientSocket is null");
        }
    }

    @Override
    public void release() {
        Log.i(TAG, "release");
        synchronized (mLock) {
            isReleased = true;
            isStarted = false;
            mCallbacks.clear();
            if (mContext != null && isRegistered) {
                mContext.unregisterReceiver(mBTReceiver);
            }
            isRegistered = false;
            try {
                if (mClientSocket != null) {
                    mClientSocket.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "release clientSocket.close", e);
            }
            if (mServerSocket != null) {
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "release mServerSocket.close", e);
                }
            }
        }
        Log.i(TAG, "release: complete");
    }
}
