package com.exa.companydemo.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GnssCapabilities;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityLocationBinding;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class LocationActivity extends BaseBindActivity<ActivityLocationBinding> {

    private LocationManager locationManager;
    private int index = 0;
    private List<String> eProviders;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_location;
    }

    @Override
    protected void initView() {
        setToolbarId(R.id.toolbar);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.e("缺少定位权限");
            setText("缺少定位权限");
            checkPermission();
            return;
        }
        bind.text.setMovementMethod(ScrollingMovementMethod.getInstance());
        bind.btn.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                setText(DateUtil.getNowDateFull());
                loadBaseLocationInfo();
            }
        });
        bind.openBtn.setOnClickListener(v -> {
            L.d("打开定位功能");
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        });
        bind.closeBtn.setOnClickListener(v -> {
            L.d("关闭定位功能");
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
        });
        bind.removeRequest.setOnClickListener(v -> {
            L.d("取消订阅Gps");
            unSubscribeGpsUpdates();
        });
        bind.setRequest.setOnClickListener(v -> {
            L.d("订阅Gps定位");
            subscribeGpsUpdates();
        });
        bind.removeRequestNet.setOnClickListener(v -> {
            L.d("取消订阅网络定位");
            unSubscribeNetworkUpdates();
        });
        bind.requestNet.setOnClickListener(v -> {
            L.d("订阅网络定位");
            subscribeNetworkUpdates();
        });
        initLocationManager();
//        BaseConstants.getHandler().postDelayed(this::recyclerGetNetworkLastLocation, 3000);
    }

    private void initLocationManager() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 权限校验
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setText("定位功能是否开启：" + locationManager.isLocationEnabled());
        }
        setText("GPS是否开启：" + isOPenGPS());
        setText("");
        List<String> providers = locationManager.getAllProviders();
        eProviders = locationManager.getProviders(true);
        getLocationManagerInfo();
        L.d("全部定位方式: " + (providers != null ? providers : "null"));
        setText(L.msg);
        L.d("可用的定位方式: " + (eProviders != null ? eProviders : "null"));
        setText(L.msg);
        if (eProviders != null) {
            for (int i = 0; i < eProviders.size(); i++) {
                getProviderSupportInfo(eProviders.get(i));
            }
        }
        loadBaseLocationInfo();
        getBestProvider();
        // 注册协议监听
        locationManager.addNmeaListener((message, timestamp) ->
                        L.d("locationManager.onNmeaMessage:" + message + "," + timestamp),
                new Handler(Looper.myLooper()));
        // 注册卫星状态改变监听
        locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
            @Override
            public void onStarted() {
                super.onStarted();
                setText("GnssStatusCallback:onStarted");
                L.d("GnssStatusCallback:onStarted");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setText("定位功能是否开启：" + locationManager.isLocationEnabled());
                }
            }

            @Override
            public void onStopped() {
                super.onStopped();
                setText("GnssStatusCallback:onStopped");
                L.d("GnssStatusCallback:onStopped");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setText("定位功能是否开启：" + locationManager.isLocationEnabled());
                }
            }

            @Override
            public void onFirstFix(int ttffMillis) {
                super.onFirstFix(ttffMillis);
                setText("GnssStatusCallback:onFirstFix:" + ttffMillis);
                L.d("GnssStatusCallback:onFirstFix:" + ttffMillis);
            }

            int index = 0;

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                index = index > (status.getSatelliteCount() - 1) ? 0 : index;
                setText("收到卫星变化 count=" + status.getSatelliteCount()
                                + " index:" + index
                                + "\t Svid=" + status.getSvid(index)
                                + ", Cn0=" + status.getCn0DbHz(index)
                                + ", ElevationDegrees=" + status.getElevationDegrees(index)
                                + ", AzimuthDegrees=" + status.getAzimuthDegrees(index)
                                + ", CarrierFrequencyHz=" + status.getCarrierFrequencyHz(index) /* android 8.0开始使用 */
//                        + ", BasebandCn0DbHz=" + status.getBasebandCn0DbHz(index) /* android 11.0开始使用 */
                );
                index++;
                L.d("onSatelliteStatusChanged:卫星数 = " + status.getSatelliteCount());
            }
        }, new Handler(Looper.myLooper()));

        test();
    }

    private void test() {
//        bind.btn.postDelayed(() -> {
//            subscribeGpsUpdates();
//            unSubscribeGpsUpdates();
//            test();
//        }, 50);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                setText("test Thread: " + Thread.currentThread().getName());
                subscribeGpsUpdates();
            }
        };
        bind.btn.postDelayed(thread::interrupt, 3000);
    }

    private void subscribeGpsUpdates() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            setText("订阅Gps定位");
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0 /* 时间隔时间 */,
                    0.0F /* 位置更新的最小距离(单位：米) */,
                    locationListener);
        }
    }

    private void unSubscribeGpsUpdates() {
        if (locationManager != null) {
            setText("取消订阅Gps定位");
            locationManager.removeUpdates(locationListener);
        }
    }

    private void subscribeNetworkUpdates() {
        if(!eProviders.contains(LocationManager.NETWORK_PROVIDER)){
            setText("订阅Network定位失败：未提供网络定位能力");
            return;
        }
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            setText("订阅Network定位");
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0 /* 时间隔时间 */,
                    0.0F /* 位置更新的最小距离(单位：米) */,
                    networkListener);
        }
    }

    private void unSubscribeNetworkUpdates() {
        if(eProviders.contains(LocationManager.NETWORK_PROVIDER) && locationManager != null){
            setText("取消订阅Network定位");
            locationManager.removeUpdates(networkListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            L.d(location.getProvider() + "  onLocationChanged:" + location);
            setText(location.toString());
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            L.d("onProviderDisabled:" + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            L.d("onStatusChanged:" + provider + "  " + status + "," + (extras == null ? "" : extras.keySet()));
        }
    };

    private final LocationListener networkListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            L.d(location.getProvider() + "  onLocationChanged:" + location);
            setText(location.toString());
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            L.d("onProviderDisabled:" + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            L.d("onStatusChanged:" + provider + "  " + status + "," + (extras == null ? "" : extras.keySet()));
        }
    };

    private void recyclerGetNetworkLastLocation() {
        BaseConstants.getHandler().postDelayed(() -> {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                setText("getLastKnownLocation gps:" + location.getTime() + "，" + location.getLatitude());
                L.d("getLastKnownLocation gps:" + location.getTime() + "，" + location.getLatitude());
            }
            if (activity != null) {
                recyclerGetNetworkLastLocation();
            }
        }, 1000);
    }

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
        setText(getProviderStr(provider) + " 是否可用: " + (provider == null ?
                "provider is null" : locationManager.isProviderEnabled(provider)));
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        setText(getProviderStr(LocationManager.NETWORK_PROVIDER) + " LastKnownLocation: "
                + (location == null ? "null" : (location.getLatitude()
                + "," + location.getLongitude())));
    }

    private void getProviderSupportInfo(String ps) {
        LocationProvider provider = locationManager.getProvider(ps);
        StringBuilder builder = new StringBuilder();
        builder.append(getProviderStr(ps))
                .append(": 精度=").append(provider.getAccuracy())
                .append(" 支持速度=").append(provider.supportsSpeed())
                .append(" 电源需求=").append(provider.getPowerRequirement())
                .append(" 支持海拔=").append(provider.supportsAltitude())
                .append(" 需要访问基站=").append(provider.requiresCell())
                .append(" 需要网络数据=").append(provider.requiresNetwork())
                .append(" 支持方向信息=").append(provider.supportsBearing())
                .append(" 访问卫星的定位系统=").append(provider.requiresSatellite())
                .append("");
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
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        Gravity.apply(Gravity.BOTTOM, 500, 200,
                new Rect(0, 0, 100, 500), 100, 50,
                new Rect(0, 0, 100, 500));
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
            if (addressList != null && addressList.size() != 0) {
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
            e.printStackTrace();
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

    private void setText(final String msg) {
        runOnUiThread(() -> {
            String n = msg + "\n" + bind.text.getText().toString();
            bind.text.setText(n);
        });
    }

    private String getProviderStr(String provider) {
        if (provider != null)
            switch (provider) {
                case "network":
                    return "网络定位";
                case "passive":
                    return "被动定位";
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
                },
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (index < 3) {
            initView();
        }
    }
}