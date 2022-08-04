package com.exa.companydemo.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityLocationBinding;
import com.exa.companydemo.utils.PermissionUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class LocationActivity extends BaseBindActivity<ActivityLocationBinding> {

    @Override
    protected int setContentViewLayoutId() {
        checkPermission();
        return R.layout.activity_location;
    }

    @Override
    protected void initView() {
        bind.text.setMovementMethod(ScrollingMovementMethod.getInstance());
        //获取位置管理器
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            L.e("缺少定位权限");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,//时间隔时间
                1F,//位置更新之间的最小距离
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        L.d(location.getProvider() + "  onLocationChanged:" + location.getLatitude() + "," + location.getLongitude() + "  " + location.getExtras().keySet());
                        locationUpdate(location, location.getProvider());
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        LocationListener.super.onProviderDisabled(provider);
                        L.d("onProviderDisabled:" + provider);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        LocationListener.super.onStatusChanged(provider, status, extras);
                        L.d("onStatusChanged:" + provider + "  " + status);
                    }
                });
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                1000,//时间隔时间
                1F,//位置更新之间的最小距离
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        L.d(location.getProvider() + "  onLocationChanged:" + location.getLatitude() + "," + location.getLongitude() + "  " + location.getExtras().keySet());
                        locationUpdate(location, location.getProvider());
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        LocationListener.super.onProviderDisabled(provider);
                        L.d("onProviderDisabled:" + provider);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        LocationListener.super.onStatusChanged(provider, status, extras);
                        L.d("onStatusChanged:" + provider + "  " + status);
                    }
                });
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000,//时间隔时间
                1F,//位置更新之间的最小距离
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        L.d(location.getProvider() + "  onLocationChanged:" + location.getLatitude() + "," + location.getLongitude() + "  " + location.getExtras().keySet());
                        locationUpdate(location, location.getProvider());
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        LocationListener.super.onProviderDisabled(provider);
                        L.d("onProviderDisabled:" + provider);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        LocationListener.super.onStatusChanged(provider, status, extras);
                        L.d("onStatusChanged:" + provider + "  " + status);
                    }
                });
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
        if (null != location) {
            setText(provider + "::" + location.getLongitude() + "," + location.getLatitude());
            getAddress(location.getLongitude(), location.getLatitude(), provider);
        } else {
            setText("没有获取到GPS信息");
        }
    }

    private void getAddress(double longitude, double latitude, String provider) {
        //Geocoder通过经纬度获取具体信息
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = gc.getFromLocation(latitude, longitude, 1);
            if (addressList != null) {
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
                setText(provider + "::" + longitude + "," + latitude + " " + addressLines);
                L.d(provider + "::" + longitude + "," + latitude + " " + addressLines + " " + specificAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setText(String msg) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        runOnUiThread(() -> {
            String n = msg + "\n" + bind.text.getText().toString();
            bind.text.setText(n);
        });
    }

    @Override
    protected void initData() {

    }

    private void checkPermission() {
        PermissionUtil.requestPermission(this, () -> L.d("有定位权限"),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION});
    }
}