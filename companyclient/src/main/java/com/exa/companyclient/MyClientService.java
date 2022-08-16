package com.exa.companyclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
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
            L.d("onServiceDisconnected");
        }
    };

    private void doBackground() {
        BaseConstants.getHandler().postDelayed(() -> {
            if (binder != null) {
                try {
                    Location location = binder.getLocationInfo();
                    L.d("getLocationInfo:" + location.getLatitude() + "," + location.getLongitude());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
            doBackground();
        }, 1000);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intentExt = new Intent("com.exa.companydemo.ExtLocationService");
        intentExt.setPackage("com.exa.companydemo");
        bindService(intentExt, connection, Context.BIND_AUTO_CREATE);
    }
}
