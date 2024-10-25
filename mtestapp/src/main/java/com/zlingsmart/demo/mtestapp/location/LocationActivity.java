package com.zlingsmart.demo.mtestapp.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GnssCapabilities;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.baselib.utils.PermissionUtil;
import com.zlingsmart.demo.mtestapp.R;
import com.zlingsmart.demo.mtestapp.databinding.ActivityLocationBinding;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class LocationActivity extends BaseBindActivity<ActivityLocationBinding> {

    private LocationManager locationManager;
    private int index = 0;
    private List<String> eProviders;
    private Handler mHandler;
    private Timer mNmeaTimer;
    private String mLastNmea;
    private boolean mPushShiftData = false;
    private FordLocationUtil mFordLocationUtil;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_location;
    }

    @Override
    protected void initView() {
        HandlerThread thread = new HandlerThread(getClass().getName());
        thread.start();
        mHandler = new Handler(thread.getLooper());
        setToolbarId(R.id.toolbar);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.e("缺少定位权限");
            setText("缺少定位权限");
            checkPermission();
            return;
        }
        setText("已授权定位权限\n");
        bind.text.setMovementMethod(ScrollingMovementMethod.getInstance());
        bind.btn.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                setText(DateUtil.getNowDateFull());
                loadBaseLocationInfo();
            }
        });
        bind.swLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                L.d("打开定位功能");
                Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
            } else {
                L.d("关闭定位功能");
                Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            }
        });
        bind.swGPS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                L.d("subscribeGpsUpdates");
                subscribeGpsUpdates();
            } else {
                L.d("unSubscribeGpsUpdates");
                unSubscribeGpsUpdates();
            }
        });
        bind.swSv.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                L.d("subscribeSvUpdates");
                subscribeSvUpdates();
            } else {
                L.d("unSubscribeSvUpdates");
                unSubscribeSvUpdates();
            }
        });
        bind.swNmea.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                L.d("subNmea");
                subNmea();
            } else {
                L.d("unSubNmea");
                unSubNmea();
            }
        });
        bind.swCarPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                L.d("subscribeCarPlayUpdates");
                subscribeCarPlayUpdates();
            } else {
                L.d("unSubscribeCarPlayUpdates");
                unSubscribeCarPlayUpdates();
            }
        });
        bind.swPushShifted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                L.d("startPushMockShiftedData");
                setText(L.msg);
                mPushShiftData = true;
                if (!bind.swGPS.isChecked()) {
                    bind.swGPS.setChecked(true);
                }
                mFordLocationUtil.startPushMockShiftedData();
            } else {
                L.d("stopPushMockShiftedData");
                setText(L.msg);
                mPushShiftData = false;
                if (bind.swGPS.isChecked()) {
                    bind.swGPS.setChecked(false);
                }
                mFordLocationUtil.stopPushMockShiftedData();
            }
        });
        bind.testBtn.setOnClickListener(v -> {
            L.d("click testBtn");
//            testExtra();
        });
        initLocationManager();
        subscribeCarPlayUpdates();
    }

    private void subscribeCarPlayUpdates() {
        setText("subscribeCarPlayUpdates");
        mFordLocationUtil.subCarPlayData();
    }

    private void unSubscribeCarPlayUpdates() {
        setText("unSubscribeCarPlayUpdates");
        mFordLocationUtil.unSubCarPlayData();
    }

    private void initLocationManager() {
        mFordLocationUtil = new FordLocationUtil(this, this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 权限校验
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        eProviders = locationManager.getProviders(true);

        String content = "定位功能是否开启：" + locationManager.isLocationEnabled() + "\n" + "GPS是否开启：" + isOPenGPS() + "\n" + "全部定位方式: " + providers + "\n" + "可用的定位方式: " + (eProviders != null ? eProviders : "null");
        L.d(content);
        setText(content);

        getLocationManagerInfo();
        if (eProviders != null) {
            for (int i = 0; i < eProviders.size(); i++) {
                getProviderSupportInfo(eProviders.get(i));
            }
        }
        loadBaseLocationInfo();
        getBestProvider();
    }

    private void subNmea() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.d("权限不足，请授权定位");
            return;
        }
        startNmeaTimer();
        setText("订阅NMEA数据");
        locationManager.addNmeaListener(mNmeaListener, mHandler);
    }

    private void unSubNmea() {
        if (mNmeaTimer != null) {
            mNmeaTimer.cancel();
            mNmeaTimer = null;
        }
        setText("unSubNmea");
        locationManager.removeNmeaListener(mNmeaListener);
    }

    private void startNmeaTimer() {
        if (mNmeaTimer == null) {
            mNmeaTimer = new Timer();
            mNmeaTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mLastNmea != null) {
                        handleNmeaData(mLastNmea);
                        mLastNmea = null;
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * 处理nmea数据
     *
     * @param nmea
     */
    private void handleNmeaData(String nmea) {
        L.dd(nmea);
        setText(L.msg);
    }

    private final OnNmeaMessageListener mNmeaListener = new OnNmeaMessageListener() {
        @Override
        public void onNmeaMessage(String message, long timestamp) {
//            L.d("locationManager.onNmeaMessage:" + message + "," + timestamp);
            mLastNmea = message;
        }
    };

    private void subscribeGpsUpdates() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            setText("subscribeGpsUpdates");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000 /* 时间隔时间 毫秒 */, 0F /* 位置更新的最小距离(单位：米) */, locationListener);
        }
    }

    private void unSubscribeGpsUpdates() {
        if (locationManager != null) {
            setText("unSubscribeGpsUpdates");
            locationManager.removeUpdates(locationListener);
        }
    }

    private void subscribeSvUpdates() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            setText("subscribeSvUpdates");
            locationManager.registerGnssStatusCallback(svCallback, mHandler);
        }
    }

    private void unSubscribeSvUpdates() {
        if (locationManager != null) {
            setText("unSubscribeSvUpdates");
            locationManager.unregisterGnssStatusCallback(svCallback);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Bundle bundle = location.getExtras();
            StringBuilder builder = new StringBuilder();
            for (String key : bundle.keySet()) {
                builder.append(key).append("=").append(bundle.get(key)).append(",");
            }
            L.d("onLocationChanged " + ", time=" + location.getTime() + ", " + location);
            setText(L.msg + ", systemTime:" + DateUtil.getNowTime());
            if (mPushShiftData) {
                mFordLocationUtil.pushShiftedData(location);
            }
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            L.d("onProviderDisabled:" + provider);
        }
    };

    private final GnssStatus.Callback svCallback = new GnssStatus.Callback() {
        @Override
        public void onStarted() {
            super.onStarted();
            setText("GnssStatusCallback:onStarted");
            L.d("GnssStatusCallback:onStarted");
            setText("定位功能是否开启：" + locationManager.isLocationEnabled());
        }

        @Override
        public void onStopped() {
            super.onStopped();
            setText("GnssStatusCallback:onStopped");
            L.d("GnssStatusCallback:onStopped");
            setText("定位功能是否开启：" + locationManager.isLocationEnabled());
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            super.onFirstFix(ttffMillis);
            setText("GnssStatusCallback:onFirstFix:" + ttffMillis);
            L.d("GnssStatusCallback:onFirstFix:" + ttffMillis);
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
            super.onSatelliteStatusChanged(status);
            int usedInFixNum = 0;
            StringBuilder builder = new StringBuilder();
            StringBuilder snrSb = new StringBuilder();
            for (int i = 0; i < status.getSatelliteCount(); i++) {
                if (status.usedInFix(i)) {
                    usedInFixNum++;
                }
                builder.append(status.getSvid(i)).append(" ");
                snrSb.append((int) status.getCn0DbHz(i)).append(" ");
            }
            String msg = DateUtil.getNowTime() + " svlist count=" + status.getSatelliteCount() + ", usedInFixNum=" + usedInFixNum + ", satIds=" + builder + ",snrs=" + snrSb;
            L.d(msg);
            setText(msg);
        }
    };

    /**
     * 获取最佳的provider
     */
    private void getBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //设置不需要获取海拔方向数据
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        //设置允许产生资费
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);//要求低耗电
        String provider = locationManager.getBestProvider(criteria, true);
        L.e("获取最佳定位方式: " + getProviderStr(provider));
        setText("获取最佳定位方式: " + getProviderStr(provider));
        setText(getProviderStr(provider) + " 是否可用: " + (provider == null ? "provider is null" : locationManager.isProviderEnabled(provider)));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        setText(getProviderStr(LocationManager.NETWORK_PROVIDER) + " LastKnownLocation: " + (location == null ? "null" : (location.getLatitude() + "," + location.getLongitude())));
    }

    private void getProviderSupportInfo(String ps) {
        LocationProvider provider = locationManager.getProvider(ps);
        StringBuilder builder = new StringBuilder();
        assert provider != null;
        builder.append(getProviderStr(ps)).append(": 精度=").append(provider.getAccuracy()).append(" 支持速度=").append(provider.supportsSpeed()).append(" 电源需求=").append(provider.getPowerRequirement()).append(" 支持海拔=").append(provider.supportsAltitude()).append(" 需要访问基站=").append(provider.requiresCell()).append(" 需要网络数据=").append(provider.requiresNetwork()).append(" 支持方向信息=").append(provider.supportsBearing()).append(" 访问卫星的定位系统=").append(provider.requiresSatellite()).append("");
        L.d(builder.toString());
        setText(builder.toString());
    }

    private void getLocationManagerInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            GnssCapabilities capabilities = locationManager.getGnssCapabilities();
            L.d("capabilities.hasGnssAntennaInfo: " + capabilities.hasGnssAntennaInfo());
            int accuracy = locationManager.getProvider(LocationManager.GPS_PROVIDER).getAccuracy();
            L.d("精度accuracy: " + accuracy);
            setText("Gnss硬件模块名称:" + locationManager.getGnssHardwareModelName());
            L.d("getGnssHardwareModelName:" + locationManager.getGnssHardwareModelName());
        }
    }

    private void loadBaseLocationInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.e("缺少定位权限");
            setText("缺少定位权限");
            return;
        }
        // 获取最新的定位信息
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // 将最新的定位信息，传递给LocationUpdates()方法
        locationUpdate(location, LocationManager.GPS_PROVIDER);
        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        locationUpdate(location, LocationManager.PASSIVE_PROVIDER);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        locationUpdate(location, LocationManager.NETWORK_PROVIDER);
    }

    private void locationUpdate(Location location, String provider) {
        provider = getProviderStr(provider);
        if (null != location) {
            setText(provider + "::" + location.getLongitude() + "," + location.getLatitude());
            getAddress(location, location.getLongitude(), location.getLatitude(), provider);
        } else {
            setText("没有获取到定位信息:" + provider);
        }
    }

    private void getAddress(Location location, double longitude, double latitude, String provider) {
        //Geocoder通过经纬度获取具体信息
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = gc.getFromLocation(latitude, longitude, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                //获取国家名称
                String countryName = address.getCountryName();
                //返回地址的国家代码，CN
                String countryCode = address.getCountryCode();
                //对应的省或者市

                String adminArea = address.getAdminArea();
                //子管理区域 对应的镇
                String subAdminArea = address.getSubAdminArea();
                //一个市对应的具体的区
                String subLocality = address.getSubLocality();
                //具体镇名加具体位置
                String featureName = address.getFeatureName();
                //返回一个具体的位置串，这个就不用进行拼接了。
                String addressLines = address.getAddressLine(0);
                String specificAddress = countryName + adminArea + subLocality + featureName;
                setText(provider + "::" + latitude + "," + longitude + " " + addressLines + "," + location);
                L.d(provider + "::" + latitude + "," + longitude + " " + addressLines + " " + specificAddress);
            }
        } catch (Exception e) {
            L.e("getAddress Exception", e);
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @return true 表示开启
     */
    private boolean isOPenGPS() {
        // GPS定位
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 网络服务定位
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    private int indexSetText = 0;

    public void setText(final String msg) {
        indexSetText++;
        final String content;
        if (indexSetText >= 30) {
            indexSetText = 0;
            content = msg;
        } else {
            content = msg + "\n" + bind.text.getText().toString();
        }
        runOnUiThread(() -> {
            bind.text.setText(content);
        });
    }

    private String getProviderStr(String provider) {
        if (provider != null) switch (provider) {
            case "network":
                return "网络定位";
            case "passive":
                return "混合定位";
            default:
                return "GPS定位";
        }
        return "GPS定位";
    }

    @Override
    protected void initData() {

    }

    private void checkPermission() {
        index++;
        PermissionUtil.requestPermission(this, () -> {
            L.d("有定位权限");
            setText("有定位权限");
        }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (index < 3) {
            initView();
        }
    }
}