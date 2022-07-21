package com.exa.companydemo;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.companydemo.usb.USBReceiver;
import com.exa.companydemo.utils.PermissionUtil;

import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity {

    @Override
    protected void initData() {
        test();
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn).setOnClickListener(view -> {
            L.d("点击Toast测试1");
            Toast.makeText(this, "原生Toast测试", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            L.d("点击跳转到第二个页面");
            startActivity(new Intent(this, SecondActivity.class));
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private void test() {
        registerBroadcast();
    }

    private void checkPermission() {
        PermissionUtil.requestPermission(this, () -> {
            L.d("已授权 读写权限");
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }

    private USBReceiver mReceiver = new USBReceiver();

    private void registerBroadcast() {
        L.d("registerBroadcast");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}