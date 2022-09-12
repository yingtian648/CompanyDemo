/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

/**
 * Represents a GNSS position mode.
 */
public class CellLocationProvider {
    private final String TAG = "CellLocationProvider";
    private String provider;
    private IExtLocationInterface binder;
    private Context mContext;
    private Handler handler = new Handler(Looper.myLooper());

    private final long REQUEST_INTERVAL = 1000;//request location time ineterval
    private final long DELAY_BIND_SERVICE = 3000;//delay bind service
    private final int MAX_RECONNECT_TIME = 3;//max reconnect times

    private int reConnectTime = 0;

    public static CellLocationProvider getInstance() {
        return ClazzHolder.cellLocationProvider;
    }

    private static class ClazzHolder {
        private static CellLocationProvider cellLocationProvider = new CellLocationProvider();
    }

    private CellLocationProvider() {

    }

    public void init(Context context, String provider) {
        this.provider = provider;
        this.mContext = context;
    }

    /**
     * 上报位置信息
     *
     * @param location
     */
    private void reportLocationInfo(Location location) {
        Log.v(TAG, "reportLocationInfo:" + location.toString());
        if (provider != null) {
//            provider.reportLocation(location);
            // provider.handleReportLocation(true, location);
        }
    }

    /**
     * 启动
     */
    public void start() {
        Log.v(TAG, "start");
        reConnectTime = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bindServer();
            }
        }, DELAY_BIND_SERVICE);
    }

    /**
     * 停止
     */
    public void stop() {
        Log.v(TAG, "stop");
        if (binder != null) {
            mContext.unbindService(connection);
        }
        handler.removeCallbacksAndMessages(null);
        handler = null;
        provider = null;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            binder = IExtLocationInterface.Stub.asInterface(service);
            requestLocation();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
            binder = null;
            reConnectTime = 0;
            checkConnectServerStatus("onServiceDisconnected rebind");
        }
    };

    private IExtLocationCallback.Stub callback = new IExtLocationCallback.Stub() {
        @Override
        public void onLocation(long interval, Location location) throws RemoteException {
            // Log.d(TAG, "onLocation:" + interval + "," + location.getLongitude() + "," + location.getLatitude());
            reportLocationInfo(location);
        }
    };

    private void bindServer() {
        Log.v(TAG, "bindExtServer");
        try {
            Intent intentExt = new Intent();
            intentExt.setClassName("com.exa.companydemo", "com.exa.companydemo.aidlservice.ExtLocationService");
            mContext.bindService(intentExt, connection, Context.BIND_AUTO_CREATE);
            checkConnectServerStatus(intentExt.getComponent() == null ? intentExt.getPackage() : intentExt.getComponent().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "bindExtServer Exception: " + e.getMessage());
        }
    }

    private void checkConnectServerStatus(final String bindInfo) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (binder == null) {
                    Log.d(TAG, "bindExtServer[" + bindInfo + "] timeout,rebind!");
                    reConnectTime++;
                    if (reConnectTime < MAX_RECONNECT_TIME) {
                        bindServer();
                    }
                }
            }
        }, DELAY_BIND_SERVICE);
    }

    private void requestLocation() {
        Log.d(TAG, "requestLocation");
        try {
            if (binder != null) {
                binder.setLocationRequest(REQUEST_INTERVAL, callback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            binder = null;
            Log.e(TAG, "requestLocation RemoteException: " + e.getMessage());
        }
    }
}
