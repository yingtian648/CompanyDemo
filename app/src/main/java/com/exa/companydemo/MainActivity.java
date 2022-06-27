package com.exa.companydemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.exa.companydemo.db.entity.Files;
import com.exa.companydemo.musicload.MediaLoadActivity;
import com.exa.companydemo.utils.L;
import com.exa.companydemo.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private ExecutorService pool;
    private ArrayList<Files> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        test();
    }


    private void test() {

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