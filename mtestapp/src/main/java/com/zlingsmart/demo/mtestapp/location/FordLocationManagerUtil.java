package com.zlingsmart.demo.mtestapp.location;

import android.car.Car;
import android.car.hardware.location.FordCarLocationManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.exa.baselib.utils.L;

/**
 * @author lsh
 * @date 2024/9/25 14:44
 * @description
 */
public class FordLocationManagerUtil {

    public static final String TAG = "FordLocationManagerUtil";
    private Handler handler = new Handler(Looper.myLooper());
    private Car car;
    private Context context;
    private FordCarLocationManager locationManager;

    private Runnable delayCheckCar = new Runnable() {
        @Override
        public void run() {
            if (car != null && car.isConnected()) {
                L.w(TAG, "Car初始化成功");
                getFordLocationManager();
            } else {
                L.e(TAG, "Car初始化失败");
                Toast.makeText(context, "Car初始化失败", Toast.LENGTH_LONG).show();
            }
        }
    };

    public FordLocationManagerUtil(Context context) {
        L.dd();
        this.context = context;
        car = Car.createCar(context);
        handler.postDelayed(delayCheckCar, 2000);
    }


    private void getFordLocationManager() {
        L.dd();
        locationManager = (FordCarLocationManager) car.getCarManager(Car.FORD_LOCATION_SERVICE);
        if (locationManager == null) {
            L.e(TAG, "FordCarLocationManager获取失败");
            Toast.makeText(context, "FordCarLocationManager获取失败", Toast.LENGTH_LONG).show();
        }
    }

    public void onLocationUpdate(Location location) {
        if (locationManager != null) {
            Bundle bundle = new Bundle();
            bundle.putDouble("ChinaShiftedLatitude", location.getLatitude());
            bundle.putDouble("ChinaShiftedLongitude", location.getLongitude());
            L.dd("sendLocationShiftedData");
            locationManager.sendLocationShiftedData(bundle);
        }
    }
}
