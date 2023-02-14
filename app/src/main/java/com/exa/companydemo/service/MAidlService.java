package com.exa.companydemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.companydemo.MCallback;
import com.exa.companydemo.MInterface;
import com.exa.companydemo.UserInfo;

import java.util.Random;

import androidx.annotation.Nullable;

/**
 * @author Administrator
 */
public class MAidlService extends Service {
    private final String TAG = "ExtLocationService";
    private MCallback mCallback;
    private Handler handler;
    private int index = 0;
    private MInterface.Stub binder = new MInterface.Stub() {

        @Override
        public void setCallback(MCallback callback) throws RemoteException {
            mCallback = callback;
        }

        @Override
        public int getMode() throws RemoteException {
            return 0;
        }

        @Override
        public boolean setMode(int mode) throws RemoteException {
            return false;
        }

        @Override
        public UserInfo getInfo(long id) throws RemoteException {
            return new UserInfo(id, "用户名", new Random().nextInt(30) + "岁", "韶关");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("ExtLocationService");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        circleSendInfo();
    }

    /**
     * 循环发送订单为那些
     */
    private void circleSendInfo() {
        handler.postDelayed(() -> {
            handler.removeCallbacksAndMessages(null);
            if (mCallback != null) {
                index++;
                try {
                    mCallback.onChanged(DateUtil.getNowDateFull());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    L.e("circleSendInfo err,", e);
                }
                circleSendInfo();
            }
        }, 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        binder = null;
        super.onDestroy();
    }
}
