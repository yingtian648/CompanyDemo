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
import android.util.Log;

import com.android.server.location.cell.CellLocationProvider;
import com.android.server.location.cell.IExtLocationCallback;
import com.android.server.location.cell.IExtLocationInterface;
import com.exa.baselib.utils.L;

import androidx.annotation.Nullable;

/**
 * 测试服务中绑定服务
 */
public class MyClientService extends Service {
    private IExtLocationInterface binder;
    private Context mContext;
    private final int RECONNECT_TIME = 3000;//重连时间间隔
    private final int TIME_INTERVAL = 1000;//重复获取数据时间间隔

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d("onStartCommand");
        CellLocationProvider provider = CellLocationProvider.getInstance();
        provider.init(this,"121");
        provider.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
