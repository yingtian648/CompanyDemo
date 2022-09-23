package com.exa.companyclient;

import android.app.Service;
import android.content.Intent;
import android.location.GnssLocationExtHelper;
import android.location.Location;
import android.location.LocationListener;
import android.os.IBinder;

import com.exa.baselib.utils.L;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
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
        GnssLocationExtHelper provider = GnssLocationExtHelper.getInstance();
        provider.init(this, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                L.d("MyClientService onLocationChanged:"+ location);
                EventBus.getDefault().post(location);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                L.d("MyClientService onProviderEnabled:"+provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                L.d("MyClientService onProviderDisabled:"+provider);
            }
        });
        provider.bindServer();
        return super.onStartCommand(intent, flags, startId);
    }
}
