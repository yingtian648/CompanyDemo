package com.exa.companyclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.android.server.location.cell.ExtLocationInterface;
import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;

import androidx.annotation.Nullable;

/**
 * 测试服务中绑定服务
 */
public class MyClientService extends Service {
    private ExtLocationInterface binder;
    private Handler handler;
    private final int RECONNECT_TIME = 3000;//重连时间间隔
    private final int TIME_INTERVAL = 1000;//重复获取数据时间间隔

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            L.d("onServiceConnected");
            binder = ExtLocationInterface.Stub.asInterface(service);
            doBackground();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.e("onServiceDisconnected");
            binder = null;
            BaseConstants.getHandler().postDelayed(() -> {
                bindServer();
            }, RECONNECT_TIME);
        }
    };

    private void doBackground() {
        handler.postDelayed(() -> {
            if (binder != null) {
                try {
                    Location location = binder.getLocationInfo();
                    L.d("getLocationInfo:" + location.getLatitude() + "," + location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e("getLocationInfo Exception: " + e.getMessage());
                }
            } else {
                return;
            }
            doBackground();
        }, TIME_INTERVAL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("MyClientService");
        thread.start();
        handler = new Handler(thread.getLooper());
        bindServer();
    }

    private void bindServer() {
        handler.removeCallbacksAndMessages(null);
        Intent intentExt = new Intent("com.exa.companydemo.ExtLocationService");
        intentExt.setPackage("com.exa.companydemo");
        bindService(intentExt, connection, Context.BIND_AUTO_CREATE);
    }
}
