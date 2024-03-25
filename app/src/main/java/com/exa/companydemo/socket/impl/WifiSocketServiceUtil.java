package com.exa.companydemo.socket.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author lsh
 * @Date 2024/3/21 11:26
 * @Description
 */
public class WifiSocketServiceUtil extends AbstractSocketService {
    private static final String TAG = "WifiSocketServiceUtil";
    private List<Socket> clientList = new ArrayList<>();
    private static final int PORT = 8080;
    private static final WifiSocketServiceUtil mInstance = new WifiSocketServiceUtil();
    private boolean isReleased = false;
    private boolean isStarted = false;
    private ServerSocket mServerSocket;

    public static WifiSocketServiceUtil getInstance() {
        return mInstance;
    }

    private WifiSocketServiceUtil() {
    }

    @Override
    public void init(Context context) {
        this.mContext = context;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startService() {
        Log.i(TAG, "startService");
        mExecutor.execute(() -> {
            synchronized (mLock) {
                if (isStarted) {
                    Log.w(TAG, "startService: isStarted");
                    return;
                }
            }
            try {
                mServerSocket = new ServerSocket(PORT);
                onStarted();
                while (!isReleased) {
                    Socket socket = mServerSocket.accept();
                    clientList.add(socket);
                    onClientConnected(socket);
                    waitClientMessage(socket);
                }
            } catch (IOException e) {
                Log.e(TAG, "startSocketService: ", e);
                onError("startSocketService " + e.getMessage());
            }
        });
    }

    private void waitClientMessage(final Socket socket) {
        new Thread(() -> {
            try {
                // 获取输入流
                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    String msg = new String(buffer, 0, len);
                    onReceiveMsg(msg);
                }
            } catch (IOException e) {
                Log.e(TAG, "waitClientMessage: ", e);
                clientList.remove(socket);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void onStarted() {
        synchronized (mLock) {
            isStarted = true;
        }
        for (SocketCallback callback : mCallbacks) {
            callback.onStarted(String.valueOf(WifiSocketServiceUtil.PORT));
        }
    }

    private void onError(String msg) {
        isStarted = true;
        for (SocketCallback callback : mCallbacks) {
            callback.onError(msg);
        }
    }

    private void onClientConnected(Socket socket) {
        for (SocketCallback callback : mCallbacks) {
            callback.onClientConnected(socket.getInetAddress().getHostAddress(), socket.getPort());
        }
    }

    private void onReceiveMsg(String msg) {
        Log.w(TAG, "收到客户端消息: " + msg);
        for (SocketCallback callback : mCallbacks) {
            callback.onReceived(msg);
        }
    }

    @Override
    public void sendMessage(final String message) {
        Log.w(TAG, "sendMessageToServer: " + message + ", clientList:" + clientList.size());
        mExecutor.execute(() -> {
            for (Socket socket : clientList) {
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(message.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "sendMessageToServer: ", e);
                }
            }
        });
    }

    @Override
    public void release() {
        Log.i(TAG, "release");
        synchronized (mLock) {
            mCallbacks.clear();
            for (Socket socket : clientList) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clientList.clear();
            if (mServerSocket != null && !mServerSocket.isClosed()) {
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            isReleased = true;
            isStarted = false;
        }
        Log.i(TAG, "release: complete");
    }
}
