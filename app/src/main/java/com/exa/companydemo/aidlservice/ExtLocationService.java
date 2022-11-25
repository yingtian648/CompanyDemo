package com.exa.companydemo.aidlservice;

import android.app.Service;
import android.content.Intent;
import com.android.server.location.gnss.IExtLocationCallback;
import com.android.server.location.gnss.IExtLocationInterface;
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
    private int index = 0;
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

        @Override
        public boolean isGnssVisibilityControlSupported() throws RemoteException {
            return false;
        }

        @Override
        public void cleanUp() throws RemoteException {

        }

        @Override
        public void setPositionMode(int mode, int recurrence, int min_interval, int preferred_accuracy, int preferred_time, boolean lowPowerMode) throws RemoteException {

        }

        @Override
        public void deleteAidingData() throws RemoteException {

        }

        @Override
        public int readNmea(byte[] buffer, int bufferSize) throws RemoteException {
            return 0;
        }

        @Override
        public void agpsNiMessage(byte[] msg, int length) throws RemoteException {

        }

        @Override
        public void setAgpsServer(int type, String hostname, int port) throws RemoteException {

        }

        @Override
        public void sendNiResponse(int notificationId, int userResponse) throws RemoteException {

        }

        @Override
        public void agpsSetRefLocationCellid(int type, int mcc, int mnc, int lac, int cid) throws RemoteException {

        }

        @Override
        public void agpsSetId(int type, String setid) throws RemoteException {

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
                index++;
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
                    mCallback.reportSvStatus(index, new int[]{1,2,3}, new float[]{4,5,6}, new float[]{7,8,9}, new float[]{10,11,12}, new float[]{13,14,15}, new float[]{16,17,18});
//                    mCallback.reportSvStatus(3, null, null, null, null, null,null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    mCallback = null;
                    L.e("circleSendLocation RemoteException: " + e.getMessage());
                }
                circleSendLocation();
            }
            if(index==10){
                index = 0;
                L.d("make crash");
                int x = 10/0;
                L.d("" + x);
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
