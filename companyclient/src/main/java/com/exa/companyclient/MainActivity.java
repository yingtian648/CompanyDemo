package com.exa.companyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.bean.EventBean;
import com.exa.baselib.utils.Utils;
import com.exa.companyclient.databinding.ActivityMainBinding;
import com.exa.companyclient.provider.SystemMediaProviderUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

public class MainActivity extends BaseBindActivity<ActivityMainBinding> {

    private boolean isFinish = false;
    private int index = 0;
    private Timer timer;

    @Override
    protected int setContentViewLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        checkPermissions();
        registerBroadcast();
        timer = new Timer();
        SystemMediaProviderUtil.registerObserver(this, SystemMediaProviderUtil.getObserver());
        bind.btn1.setOnClickListener(view -> {
//            MyProviderUtil.registerObserver(this, MyProviderUtil.getObserver());
            SystemMediaProviderUtil.registerObserver(this, SystemMediaProviderUtil.getObserver());
        });
        bind.btn2.setOnClickListener(view -> {
//            MyProviderUtil.unregisterObserver(this, MyProviderUtil.getObserver());
            SystemMediaProviderUtil.unregisterObserver(this, SystemMediaProviderUtil.getObserver());
        });
        loadData();
    }

    @Override
    protected void initData() {
        L.d("Android OS:" + Build.VERSION.RELEASE);
    }

    private void checkPermissions() {
//        PermissionUtil.requestPermission(this, this::loadData,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void loadData() {

//        SystemMediaProviderUtil.getSystemMediaProviderData(this, BaseConstants.SystemMediaType.Audio);
//        MyProviderUtil.testMyProvider(this);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isFinish) {
                    cancel();
                }
                index++;
                runOnUiThread(() -> {
                    bind.text.setText("加载中..." + index);
                });
            }
        }, 1000, 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean bean) {
        isFinish = true;
        if (bean.hasData()) {
            setText("显示结果：" + (bean.datas.size() == 0 ? "null" : bean.datas.size()));
        } else {
            setText(bean.message);
        }
    }

    private void setText(String msg) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        runOnUiThread(() -> {
            String n = bind.text.getText().toString() + "\n" + date + "\n" + msg;
            bind.text.setText(n);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("client onReceived:" + intent.getAction());
            setText("client onReceived:" + intent.getAction());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
        if (timer != null)
            timer.cancel();
    }
}