package com.exa.companydemo.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GnssAntennaInfo;
import android.location.GnssCapabilities;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.bean.EventBean;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.companydemo.Constants;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityLocationBinding;
import com.exa.companydemo.utils.PermissionUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocationActivity extends BaseBindActivity<ActivityLocationBinding> {

    private LocationManager locationManager;
    private int index = 0;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_location;
    }

    @Override
    protected void initView() {
        bind.text.setMovementMethod(ScrollingMovementMethod.getInstance());
        bind.btn.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                setText("\n");
                getProviderSupportInfo(LocationManager.GPS_PROVIDER);
                loadBaseLocationInfo();
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.e("缺少定位权限");
            setText("缺少定位权限");
            checkPermission();
            return;
        }
        //获取位置管理器
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setText("定位是否开启：" + isOPenGPS());
        setText("");
        List<String> providers = locationManager.getAllProviders();
        List<String> eProviders = locationManager.getProviders(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            GnssCapabilities capabilities = locationManager.getGnssCapabilities();
            L.d("getGnssCapabilities.hasGnssAntennaInfo: " + capabilities.hasGnssAntennaInfo());
            int accuracy = locationManager.getProvider(LocationManager.GPS_PROVIDER).getAccuracy();//精确度
            L.d("精度accuracy: " + accuracy);
            setText("Gnss硬件模块名称:"+locationManager.getGnssHardwareModelName());
            L.d("getGnssHardwareModelName:"+locationManager.getGnssHardwareModelName());
        }
        L.d("全部定位方式: " + (providers != null ? providers : "null"));
        setText(L.msg);
        L.d("可用的定位方式: " + (eProviders != null ? eProviders : "null"));
        setText(L.msg);

        locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                setText("GPS状态的监听器:" + event);
                L.d("GPS状态的监听器:" + event);
            }
        });
        locationManager.addNmeaListener(new GpsStatus.NmeaListener() {
            @Override
            public void onNmeaReceived(long timestamp, String nmea) {
                setText("onNmeaReceived:" + nmea);
                L.d("onNmeaReceived:" + nmea);
            }
        });

        if (eProviders != null) {
            for (int i = 0; i < eProviders.size(); i++) {
                getProviderSupportInfo(eProviders.get(i));
            }
            for (int i = 0; i < eProviders.size(); i++) {
                L.d(eProviders.get(i) + " requestLocationUpdates");
                locationManager.requestLocationUpdates(eProviders.get(i),
                        2000,//时间隔时间
                        10F,//位置更新之间的最小距离
                        new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                L.d(location.getProvider() + "  onLocationChanged:" + location.getLatitude() + "," + location.getLongitude() + "  " + location);
                                locationUpdate(location, location.getProvider());
                            }

                            @Override
                            public void onProviderDisabled(@NonNull String provider) {
                                L.d("onProviderDisabled:" + provider);
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                L.d("onStatusChanged:" + provider + "  " + status);
                            }
                        });
            }
        }
        loadBaseLocationInfo();
        getBestProvider();
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
        setText(getProviderStr(provider) + " 是否可用: " + locationManager.isProviderEnabled(provider));
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

    private void loadBaseLocationInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.e("缺少定位权限");
            setText("缺少定位权限");
            return;
        }
        //获取最新的定位信息
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //将最新的定位信息，传递给LocationUpdates()方法
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
        if (gps || network) {
            return true;
        }
        return false;
    }

    private void setText(final String msg) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        runOnUiThread(() -> {
            String n = msg + "\n" + bind.text.getText().toString();
            bind.text.setText(n);
        });
    }

    private String getProviderStr(String provider) {
        switch (provider) {
            case "network":
                return "网络定位";
            case "passive":
                return "被动定位";
            default:
                return "GPS定位";
        }
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