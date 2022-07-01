package com.exa.companydemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private void test() {
//        new Thread(Tools.insertRunnable(this)).start();
    }

    public void clickBtn(View view) {
        startActivity(new Intent(this, MediaLoadActivity.class));
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