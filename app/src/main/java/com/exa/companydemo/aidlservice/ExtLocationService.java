package com.exa.companydemo.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.android.server.location.cell.IExtLocationCallback;
import com.android.server.location.cell.IExtLocationInterface;
import com.exa.baselib.utils.L;

import androidx.annotation.Nullable;

public class ExtLocationService extends Service {
    private final String TAG = "ExtLocationService";
    private IExtLocationCallback mCallback;
    private Handler handler;
    private long mInterval = 2000;//默认2秒更新一次location
    private IExtLocationInterface.Stub binder = new IExtLocationInterface.Stub() {

        @Override
        public void setLocationRequest(long interval, IExtLocationCallback callback) throws RemoteException {
            Log.d(TAG, "setLocationRequest from client");
            mCallback = callback;
            mInterval = interval;
            circleSendLocation();
        }

        @Override
        public String getGnssHwInfo() throws RemoteException {
            return "mk123";
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
                Log.d(TAG, "circleSendLocation");
                try {
                    mCallback.onLocation(mInterval, location);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    mCallback = null;
                    L.e("circleSendLocation RemoteException: " + e.getMessage());
                }
                circleSendLocation();
            }

        }, mInterval);

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
