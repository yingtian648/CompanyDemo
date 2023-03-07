package com.exa.companydemo.common;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.exa.baselib.utils.L;

public class MyService extends Service {
    private MyBinder myBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
        L.d("MyService.onCreate");
    }


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        L.d("MyService onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.d("MyService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d("MyService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d("MyService.onDestroy");
    }

    public static class MyBinder extends Binder {
        private Callback callback;
        public void setCallback(Callback callback) {
            L.d("MyBinder.setCallback");
            this.callback = callback;
            if(callback!=null){
                callback.onResult("我又回来了");
            }
        }
    }

    public interface Callback {
        void onResult(String content);
    }
}