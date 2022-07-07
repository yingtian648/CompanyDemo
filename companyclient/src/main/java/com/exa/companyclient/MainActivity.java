package com.exa.companyclient;

import android.Manifest;
import android.os.Bundle;

import com.exa.baselib.utils.PermissionUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
    }

    private void checkPermissions() {
        PermissionUtil.requestPermission(this, this::loadData,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void loadData() {
//        SystemMediaProviderUtil.getSystemMediaProviderData(this,Constants.SystemMediaType.Audio);
//        MyProviderUtil.testMyProvider(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }
}