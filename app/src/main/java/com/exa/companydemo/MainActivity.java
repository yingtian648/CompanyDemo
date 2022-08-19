package com.exa.companydemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.mediaprovider.MediaScannerService;
import com.exa.companydemo.utils.PermissionUtil;

import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity {
    private TextView text;

    @Override
    protected void initData() {
        test();
    }

    @Override
    protected void initView() {
        text = findViewById(R.id.text);
        checkPermission();
        findViewById(R.id.btnApp).setOnClickListener(view -> {
            L.d("点击App信息");
            startActivity(new Intent(this, LocationActivity.class));
            text.setText("点击App信息");
        });
        findViewById(R.id.btn).setOnClickListener(view -> {
            L.d("点击Toast测试1");
            test();
        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            L.d("点击跳转  到第二个页面");
            startActivity(new Intent(this, SecondActivity.class));
        });
        registerBroadcast();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private void test() {

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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("main onReceived:" + intent.getAction());
            String action = intent.getAction();
            switch (action) {
                case BaseConstants.ACTION_MY_PROVIDER_SCAN_FINISH://自定义媒体扫描完成
                    break;
                case Intent.ACTION_MEDIA_MOUNTED://挂载
                    startMediaScannerService(context, intent);
                    break;
                case Intent.ACTION_MEDIA_UNMOUNTED://卸载
                    break;
                case Intent.ACTION_MEDIA_SCANNER_STARTED://扫描开始
                    break;
                case Intent.ACTION_MEDIA_SCANNER_FINISHED://扫描结束

                    break;
            }
        }
    };

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

    private void startMediaScannerService(Context context, Intent intentRes) {
//        Intent intent = new Intent(context, MediaScannerService.class);
//        Bundle b = new Bundle();
//        b.putString("path", BaseConstants.FILE_DIR_MUSIC);
//        b.putString("path","/mnt/media_rw/usb1");
//        intent.putExtras(b);
        MediaScannerService.enqueueWork(this, intentRes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}