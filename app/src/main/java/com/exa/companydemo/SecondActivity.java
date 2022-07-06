package com.exa.companydemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.exa.companydemo.base.BaseActivity;
import com.exa.companydemo.utils.L;

public class SecondActivity extends BaseActivity {

    @Override
    protected void initData() {

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            L.d("onServiceConnected:" + service);
            if (service != null) {
                MyService.MyBinder binder = (MyService.MyBinder) service;
                binder.setCallback(content -> L.d("onResult:"+content));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.d("onServiceDisconnected:" + name);
        }
    };

    @Override
    protected void initView() {
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_second;
    }
}