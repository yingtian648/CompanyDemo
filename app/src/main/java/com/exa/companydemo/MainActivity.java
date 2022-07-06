package com.exa.companydemo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import com.exa.companydemo.base.BaseActivity;
import com.exa.companydemo.db.entity.Files;
import com.exa.companydemo.musicload.MediaLoadActivity;
import com.exa.companydemo.utils.L;
import com.exa.companydemo.utils.PermissionUtil;
import com.exa.companydemo.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
            startActivity(new Intent(this,SecondActivity.class));
        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }
}