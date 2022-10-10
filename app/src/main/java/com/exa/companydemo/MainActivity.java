package com.exa.companydemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.mediaprovider.MediaScannerService;
import com.exa.companydemo.utils.PermissionUtil;

import androidx.annotation.NonNull;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class MainActivity extends BaseActivity {
    private TextView text;

    @Override
    protected void initData() {
        test();
    }

    @Override
    protected void initView() {
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        );
        String screen = "屏幕宽高：" + Tools.getScreenW(this) + "," + Tools.getScreenH(this);
        L.d(screen);
        text = findViewById(R.id.text);
        text.setText(screen);
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
            startActivity(new Intent(this, AppInfoActivity.class));
        });
        registerBroadcast();
    }

    @Override
    protected int getLayoutId() {
//        getWindow().getAttributes().systemUiVisibility = 0;
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        return R.layout.activity_main;
    }


    private void test() {

        Rect rt = new Rect();
        rt.bottom = 200;
    }

    private void checkPermission() {
//        PermissionUtil.requestPermission(this, () -> {
//            L.d("已授权 读写权限");
//        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}