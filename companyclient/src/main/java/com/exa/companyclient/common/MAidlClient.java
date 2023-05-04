package com.exa.companyclient.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.exa.baselib.utils.L;
import com.exa.companydemo.MCallback;
import com.exa.companydemo.MInterface;

/**
 * 测试服务中绑定服务
 */
public class MAidlClient {
    private final Context mContext;
    private MInterface mInterface;
    private static final String SERVICE_PACKAGE_NAME = "com.exa.companydemo";
    private static final String SERVICE_CLASS_NAME = "com.exa.companydemo.service.MAidlService";
    private int index = 0;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = MInterface.Stub.asInterface(service);
            try {
                mInterface.setCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
                L.dd("RemoteException:" + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mInterface = null;
        }
    };

    private MCallback mCallback = new MCallback.Stub() {

        @Override
        public void onChanged(String aString) throws RemoteException {
            L.dd(aString);
            index++;
            if (index % 10 == 0) {
                L.dd(mInterface.getInfo(index).toString());
            }
        }
    };

    public MAidlClient(Context context) {
        this.mContext = context;
    }

    public void bindService() {
        Intent intent = new Intent();
        intent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_CLASS_NAME);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 声明周期-销毁
     */
    public void onDestroy() {
        mCallback = null;
        mServiceConnection = null;
        mInterface = null;
    }
}
