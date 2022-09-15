package com.exa.companydemo.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.location.IExtLocationCallback;
import android.location.IExtLocationInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.exa.baselib.utils.L;

import androidx.annotation.Nullable;

public class ExtLocationService extends Service {
    private final String TAG = "ExtLocationService";
    private IExtLocationCallback mCallback;
    private Handler handler;
    private IExtLocationInterface.Stub binder = new IExtLocationInterface.Stub() {

        @Override
        public void setCallback(IExtLocationCallback callback) throws RemoteException {
            Log.d(TAG, "setLocationRequest from client");
            mCallback = callback;
            circleSendLocation();
        }

        @Override
        public String getGnssHwInfo() throws RemoteException {
            return "mk123";
        }

        @Override
        public boolean setEnable() throws RemoteException {
            return false;
        }

        @Override
        public boolean setDisable() throws RemoteException {
            return false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("ExtLocationService");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        circleSendLocation();
    }

    double lat = 34.5624251;

    /**
     * 循环发送订单为那些
     */
    private void circleSendLocation() {
        handler.postDelayed(() -> {
            handler.removeCallbacksAndMessages(null);
            if (mCallback != null) {
                lat += Math.random() / 1000;
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setTime(System.currentTimeMillis());
                location.setLongitude(104.03453435);
                location.setLatitude(lat);
                location.setSpeed(20f);//单位：米/秒
                location.setAltitude(600);//海拔
                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());//启动后的纳秒数，包括睡眠时间
                location.setAccuracy(2f);//精度
                location.setBearing(0f);
                location.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    location.setElapsedRealtimeUncertaintyNanos(10000000.00);
                }
                Log.d(TAG, "circleSendLocation");
                try {
                    mCallback.onLocation(location);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    mCallback = null;
                    L.e("circleSendLocation RemoteException: " + e.getMessage());
                }
                circleSendLocation();
            }

        }, 1000);

    }

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
