package com.exa.companydemo.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import gxa.car.extlocationservice.GnssHwInfo;
import gxa.car.extlocationservice.IExtiLocationInterface;

public class ExtLocationAidlService extends Service {
    private final String TAG = ExtLocationAidlService.class.getSimpleName();
    private IExtiLocationInterface.Stub binder = new IExtiLocationInterface.Stub() {
        @Override
        public int getLocationMode() throws RemoteException {
            return 0;
        }

        @Override
        public int getFirstLocationTime() throws RemoteException {
            return 0;
        }

        @Override
        public boolean setLocationMode(int mode) throws RemoteException {
            return false;
        }

        @Override
        public GnssHwInfo getGnssHwInfo() throws RemoteException {
            return new GnssHwInfo("111", "gps", "1212");
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onBind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        binder = null;
        super.onDestroy();
    }
}
