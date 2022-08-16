package com.exa.companydemo.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.server.location.cell.ExtLocationInterface;
import com.exa.baselib.utils.L;

import androidx.annotation.Nullable;

public class ExtLocationService extends Service {
    private final String TAG = "ExtLocationService";

    private ExtLocationInterface.Stub binder = new ExtLocationInterface.Stub() {
        @Override
        public Location getLocationInfo() throws RemoteException {
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(34.565643);
            location.setLongitude(104.0565643);
            return location;
        }

        @Override
        public String getGnssHwInfo() throws RemoteException {
            return "mk123";
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        binder = null;
        super.onDestroy();
    }
}
