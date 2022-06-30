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
//        final long startTime = System.currentTimeMillis();
//        List<Runnable> runnables = new ArrayList<>();
//        for (int i = 0; i < 10000; i++) {
//            int finalI = i;
//            runnables.add(() -> {
//                int total = 0;
//                for (int j = 0; j < 100000; j++) {
//                    total += j;
//                }
//                L.d(finalI + "当前线程ID: " + Thread.currentThread().getId() + " " + (System.currentTimeMillis() - startTime));
//            });
//        }
//        for (int i = 0; i < 10000; i++) {
//            Constants.getScheduledPool().execute(runnables.get(i));
//        }
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