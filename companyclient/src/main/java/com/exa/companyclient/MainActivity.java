package com.exa.companyclient;

import android.Manifest;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.companyclient.databinding.ActivityMainBinding;
import com.exa.companyclient.provider.MyProviderUtil;
import com.exa.companyclient.provider.SystemMediaProviderUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends BaseBindActivity<ActivityMainBinding> {

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        checkPermissions();
        bind.btn1.setOnClickListener(view -> {
            MyProviderUtil.registerObserver(this, MyProviderUtil.getObserver());
        });
        bind.btn2.setOnClickListener(view -> {
            MyProviderUtil.unregisterObserver(this, MyProviderUtil.getObserver());
        });
    }

    @Override
    protected void initData() {

    }

    private void checkPermissions() {
        PermissionUtil.requestPermission(this, this::loadData,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void loadData() {

//        SystemMediaProviderUtil.getSystemMediaProviderData(this, BaseConstants.SystemMediaType.Audio);
//        MyProviderUtil.testMyProvider(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }
}