package com.exa.companydemo.socket;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @Author lsh
 * @Date 2024/3/19 10:17
 * @Description
 */
public class SocketClientUtil {
    private static final String TAG = "SocketClientUtil";
    private static final SocketClientUtil mInstance = new SocketClientUtil();
    private ExecutorService mExecutor;
    private Socket clientSocket;
    private final List<Callback> mCallbacks = new ArrayList<>();
    private int threadIndex = 1;

    public interface Callback {
        void onReceived(String msg);
    }

    public static SocketClientUtil getInstance() {
        return mInstance;
    }

    private SocketClientUtil() {
        mExecutor = newFixedThreadPool(10);
    }

    public void registerCallback(Callback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    /**
     * 初始化客户端socket
     */
    public void initClientSocket(String ip, int port) {
        Log.w(TAG, "initClientSocket: " + ip + ":" + port + ", " + clientSocket);
        mExecutor.execute(() -> {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                clientSocket = new Socket(ip, port);
                InputStream is = clientSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    String msg = new String(buffer, 0, len);
                    onReceiveMsg(msg);
                }
                Log.i(TAG, "initClientSocket success: " + clientSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                Log.e(TAG, "initClientSocket: ", e);
            }
        });
    }

    private void onReceiveMsg(String msg) {
        Log.w(TAG, "收到服务端消息: " + msg);
        for (Callback callback : mCallbacks) {
            callback.onReceived(msg);
        }
    }

    public void sendMessageToServer(final String message) {
        if (clientSocket != null) {
            mExecutor.execute(() -> {
                try {
                    clientSocket.getOutputStream().write(message.getBytes());
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
}
