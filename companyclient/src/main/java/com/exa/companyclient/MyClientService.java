package com.exa.companyclient;

import android.app.Service;
import android.content.Intent;
import android.location.CellLocationProvider;
import android.os.IBinder;

import com.exa.baselib.utils.L;

import androidx.annotation.Nullable;

/**
 * 测试服务中绑定服务
 */
public class MyClientService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d(getClass().getName() + ": onStartCommand");
        CellLocationProvider provider = CellLocationProvider.getInstance();
        provider.init(this, "121");
        provider.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
