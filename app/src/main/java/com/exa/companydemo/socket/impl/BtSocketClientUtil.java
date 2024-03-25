package com.exa.companydemo.socket.impl;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @Author lsh
 * @Date 2024/3/21 18:12
 * @Description
 */
public class BtSocketClientUtil extends AbstractSocketClient {
    private static final String TAG = "BtSocketClientUtil";
    private static final BtSocketClientUtil mInstance = new BtSocketClientUtil();
    private BluetoothAdapter mAdapter;
    private BluetoothSocket mServerSocket;
    private BluetoothA2dpSink mBluetoothA2dpSink;
    private static final int A2DP_SINK = 11;

    public static BtSocketClientUtil getInstance() {
        return mInstance;
    }

    private final BluetoothProfile.ServiceListener mListener =
            new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    Log.i(TAG, "onServiceConnected: " + profile);
                    if (profile == A2DP_SINK) {
                        mBluetoothA2dpSink = (BluetoothA2dpSink) proxy;
                    }
                }

                @Override
                public void onServiceDisconnected(int profile) {
                    Log.i(TAG, "onServiceDisconnected: " + profile);
                    if (profile == A2DP_SINK) {
                        mBluetoothA2dpSink = null;
                    }
                }
            };

    @SuppressLint("MissingPermission")
    private void getConnectedDevices() {
        mExecutor.execute(() -> {
            if (mBluetoothA2dpSink != null) {
                List<BluetoothDevice> devices = mBluetoothA2dpSink.getConnectedDevices();
                if (devices != null && devices.size() > 0) {
                    Log.i(TAG, "getConnectedDevices: " + devices.get(0).getName()
                            + ", " + devices.get(0).getAddress());
                    connect(devices.get(0));
                }
            }
        });
    }

    private BtSocketClientUtil() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void init(Context context) {
        mAdapter.getProfileProxy(context, mListener, A2DP_SINK);
    }

    public void init() {
        Context context = getApplication();
        if (context != null) {
            mAdapter.getProfileProxy(context, mListener, A2DP_SINK);
        }
    }

    public void connect() {
        if (!mAdapter.isEnabled()) {
            onError("蓝牙未开启");
            return;
        }
        if (mBluetoothA2dpSink == null) {
            onError("mBluetoothA2dp is null");
            return;
        }
        getConnectedDevices();
    }

    @SuppressLint("MissingPermission")
    private void connect(BluetoothDevice device) {
        Log.i(TAG, "connect BluetoothDevice " + device.getName());
        mExecutor.execute(() -> {
            try {
                UUID uuid = getUUID(device.getName());
                Log.i(TAG, "connect uuid: " + uuid.toString());
                mServerSocket = device.createRfcommSocketToServiceRecord(uuid);
                mAdapter.cancelDiscovery();
                mServerSocket.connect();
                waitServerMessage();
                onConnected(device.getAddress(), 0);
            } catch (IOException e) {
                Log.e(TAG, "connect BluetoothDevice err", e);
                onError("connect BluetoothDevice err");
            }
        });
    }

    private void waitServerMessage() {
        new Thread(() -> {
            try {
                // 获取输入流
                InputStream is = mServerSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    String msg = new String(buffer, 0, len);
                    onReceiveMessage(msg);
                }
            } catch (IOException e) {
                Log.e(TAG, "waitServerMessage: ", e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void sendMessageToServer(String message) {
        Log.i(TAG, "sendMessageToServer: " + message);
        mExecutor.execute(() -> {
            if (mServerSocket != null) {
                try {
                    mServerSocket.getOutputStream().write(message.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "sendMessageToServer", e);
                }
            } else {
                onError("mServerSocket is null");
            }
        });
    }

    @Override
    public void release() {

    }
}
