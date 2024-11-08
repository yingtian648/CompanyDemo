package com.zlingsmart.demo.mtestapp.location;

import android.car.Car;
import android.car.hardware.location.FordCarLocationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.exa.baselib.utils.L;
import com.ford.sync.fnvservice.FnvConstants;
import com.ford.sync.fnvservice.FordFnv;
import com.ford.sync.fnvservice.FordFnvServiceListener;
import com.ford.sync.fnvservice.gnss.GgaNmeaData;
import com.ford.sync.fnvservice.gnss.GnssManager;
import com.ford.sync.fnvservice.gnss.RmcNmeaData;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author lsh
 * @date 2024/9/25 14:44
 * @description
 */
public class FordLocationUtil {

    private Handler handler = new Handler(Looper.myLooper());
    private Car car;
    private Context context;
    private FordCarLocationManager locationManager;
    private FordFnv fordFnv;
    private GnssManager gnssManager;
    private Timer pushShiftedDataTimer;
    private LocationActivity locationActivity;

    private final Runnable delayCheckCar = new Runnable() {
        @Override
        public void run() {
            if (car != null && car.isConnected()) {
                L.w("Car is created");
                getFordLocationManager();
            } else {
                L.e("Car init fail!");
                locationActivity.setText(L.msg);
            }
        }
    };

    public FordLocationUtil(Context context, LocationActivity locationActivity) {
        L.dd();
        this.context = context;
        this.locationActivity = locationActivity;
        car = Car.createCar(context);
        handler.postDelayed(delayCheckCar, 2000);
        // 获取FordFnv
        FordFnv.createFordFnv(context, new FnvServiceListener());
    }

    private class FnvServiceListener implements FordFnvServiceListener {

        @Override
        public void onServiceConnect(FordFnv fordFnv) {
            L.w("FordFnvService onServiceConnect");
            getFnvLocationManager(fordFnv);
        }

        @Override
        public void onServiceDisconnect() {
            fordFnv = null;
            gnssManager = null;
            L.w("FordFnvService onServiceDisconnect");
        }
    }

    /**
     * 通过 FordFnv 获取FnvSDK中的 GnssManager
     *
     * @param fordFnv
     */
    private void getFnvLocationManager(FordFnv fordFnv) {
        L.dd();
        this.fordFnv = fordFnv;
        if (fordFnv != null) {
            gnssManager = (GnssManager) fordFnv.getFnvManager(FnvConstants.GNSS_SERVICE);
            if (gnssManager == null) {
                L.e("FNV GnssManager init ");
                locationActivity.setText(L.msg);
            } else {
                gnssManager.init();
                L.w("FNV GnssManager init success!");
            }
        }
    }

    /**
     * CarPlay订阅NMEA数据
     */
    public void subCarPlayData() {
        try {
            if (gnssManager != null) {
                gnssManager.registerNmeaDataListener(nmeaDataListener);
            } else {
                L.e("FNV GnssManager is null");
            }
        } catch (Exception e) {
            L.de(e);
        }
    }

    /**
     * CarPlay取消订阅NMEA数据
     */
    public void unSubCarPlayData() {
        try {
            if (gnssManager != null) {
                gnssManager.unregisterNmeaDataListener(nmeaDataListener);
            } else {
                L.de("FNV GnssManager is null");
            }
        } catch (Exception e) {
            L.de(e);
        }
    }

    private final GnssManager.GnssNmeaDataListener nmeaDataListener = new GnssManager.GnssNmeaDataListener() {
        @Override
        public void onGnssGgaDataChanged(GgaNmeaData ggaNmeaData) {
            L.dd(ggaNmeaData.mSentence);
        }

        @Override
        public void onGnssRmcDataChanged(RmcNmeaData rmcNmeaData) {
            L.dd(rmcNmeaData.mSentence);
        }
    };


    private void getFordLocationManager() {
        L.dd();
        locationManager = (FordCarLocationManager) car.getCarManager(Car.FORD_LOCATION_SERVICE);
        if (locationManager == null) {
            L.e("FordCarLocationManager get fail");
            locationActivity.setText(L.msg);
        }
    }

    public void startPushMockShiftedData() {
        if (pushShiftedDataTimer == null) {
            pushShiftedDataTimer = new Timer();
            pushShiftedDataTimer.schedule(new TimerTask() {
                private double lat = 34.5624251;
                private double lon = 104.03663435;

                @Override
                public void run() {
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    lat += Math.random() / 3000;
                    lon -= Math.random() / 3000;
                    location.setLatitude(lat);
                    location.setLongitude(lon);
                    pushShiftedData(location);
                }
            }, 1000, 1000);
        }
    }

    public void stopPushMockShiftedData() {
        if (pushShiftedDataTimer != null) {
            pushShiftedDataTimer.cancel();
            pushShiftedDataTimer = null;
        }
    }

    /**
     * 模拟上报怕偏转数据
     *
     * @param location
     */
    public void pushShiftedData(Location location) {
        if (locationManager != null) {
            Bundle bundle = new Bundle();
            bundle.putDouble("ChinaShiftedLatitude", location.getLatitude());
            bundle.putDouble("ChinaShiftedLongitude", location.getLongitude());
            bundle.putLong("pairingKey", 123456 + (int) (Math.random() * 1000));
            L.dd("sendLocationShiftedData " + bundle);
            locationManager.sendLocationShiftedData(bundle);
        } else {
            if (car != null) {
                locationManager = (FordCarLocationManager) car.getCarManager(Car.FORD_LOCATION_SERVICE);
            }
            L.w("FordCarLocationManager is null");
        }
    }
}
