package com.exa.companydemo.socket.impl;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @Author lsh
 * @Date 2024/3/19 10:17
 * @Description
 */
public class WifiSocketClientUtil extends AbstractSocketClient {
    private static final String TAG = "SocketClientUtil";
    private static final WifiSocketClientUtil mInstance = new WifiSocketClientUtil();
    private Socket mServerSocket;
    private int threadIndex = 1;

    public static WifiSocketClientUtil getInstance() {
        return mInstance;
    }

    private WifiSocketClientUtil() {
    }

    /**
     * 初始化客户端socket
     */
    public void connect(String ip, int port) {
        Log.w(TAG, "initmServerSocket: " + ip + ":" + port + ", " + mServerSocket);
        mExecutor.execute(() -> {
            try {
                if (mServerSocket != null) {
                    mServerSocket.close();
                }
                mServerSocket = new Socket(ip, port);
                onConnected(ip, port);
                waitServerMessage();
                Log.i(TAG, "initmServerSocket success: " + mServerSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                Log.e(TAG, "initmServerSocket: ", e);
            }
        });
    }

    private void waitServerMessage() {
        new Thread(() -> {
            try {
                InputStream is = mServerSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    String msg = new String(buffer, 0, len);
                    onReceiveMessage(msg);
                }
            } catch (IOException e) {
                mServerSocket = null;
                Thread.currentThread().interrupt();
                Log.e(TAG, "waitServerMessage: ", e);
            }
        }).start();
    }

    @Override
    public void sendMessageToServer(final String message) {
        Log.i(TAG, "sendMessageToServer: " + message);
        if (mServerSocket != null) {
            mExecutor.execute(() -> {
                try {
                    mServerSocket.getOutputStream().write(message.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w(TAG, "sendMessageToServer: ", e);
                }
            });
        } else {
            Log.w(TAG, "sendMessageToServer: mServerSocket is null");
        }
    }

    @Override
    public void release() {

    }
}
