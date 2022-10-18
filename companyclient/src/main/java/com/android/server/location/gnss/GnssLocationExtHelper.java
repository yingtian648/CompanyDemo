/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.location.gnss;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;

import android.location.IExtLocationInterface;
import android.location.IExtLocationCallback;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Represents a GNSS position mode.
 */
public class GnssLocationExtHelper {
    private final String TAG = "GnssLocationExtHelper";
    private final String PROVIDER_NAME = "extend";
    private IExtLocationInterface binder;
    private Context mContext;
    private final Handler mHandler;
    private Callback mLocationListener;
    private final String SERVICE_PACKAGE_NAME = "com.gxa.car.service.location";
    private final String SERVICE_CLASS_NAME = "com.gxa.car.service.location.CarLocationService";

    private final long DELAY_BIND_SERVICE = 1000;//delay bind service

	public interface Callback extends LocationListener {
        void repoSvStatus(int svCount, int[] svidWithFlags, float[] cn0s,
                            float[] svElevations, float[] svAzimuths, float[] svCarrierFreqs,
                            float[] basebandCn0s);
    }

    public static GnssLocationExtHelper getInstance() {
        return ClazzHolder.cellLocationProvider;
    }

    private static class ClazzHolder {
        private static final GnssLocationExtHelper cellLocationProvider = new GnssLocationExtHelper();
    }

    private GnssLocationExtHelper() {
        HandlerThread mHandlerThread = new HandlerThread("GnssLocationExtHelper_HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    /**
     * set Context andd loctionlistener
     *
     * @param context
     * @param locationListener
     */
    public void init(Context context, Callback locationListener) {
        this.mContext = context;
        this.mLocationListener = locationListener;
    }

    public void bindServer() {
        Log.v(TAG, "bindExtServer");
        if (binder == null && mContext != null) {
            boolean serverValid = isServiceAppInstalled(mContext, SERVICE_PACKAGE_NAME);
            if (serverValid) {
                try {
                    Intent intentExt = new Intent();
                    intentExt.setClassName(SERVICE_PACKAGE_NAME, SERVICE_CLASS_NAME);
                    mContext.bindService(intentExt, mServiceConnection, Context.BIND_AUTO_CREATE);
                    checkConnectServerStatus(intentExt.getComponent() == null ? intentExt.getPackage() : intentExt.getComponent().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "bindExtServer Exception: " + e.getMessage());
                }
            }
        }
    }

    /**
     * on user enable Location
     */
    public boolean setEnable() {
        Log.d(TAG, "setEnable");
        try {
            if (binder != null) {
                return binder.setEnable();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            binder = null;
            Log.e(TAG, "setEnable RemoteException: " + e.getMessage());
        }
        return false;
    }

    /**
     * on user disable Location
     */
    public boolean setDisable() {
        Log.d(TAG, "setDisable");
        try {
            if (binder != null) {
                return binder.setDisable();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            binder = null;
            Log.e(TAG, "setDisable RemoteException: " + e.getMessage());
        }
        return false;
    }

    /**
     * unbind ExtServer
     */
    public void unbind() {
        Log.v(TAG, "unbindExtServer");
        if (binder != null && mContext != null) {
            mContext.unbindService(mServiceConnection);
        }
        mContext = null;
        mLocationListener = null;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            binder = IExtLocationInterface.Stub.asInterface(service);
            setCallback();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
            binder = null;
            checkConnectServerStatus("onServiceDisconnected");
        }
    };

    private IExtLocationCallback.Stub callback = new IExtLocationCallback.Stub() {
        @Override
        public void onLocation(Location location) throws RemoteException {
            // Log.d(TAG, "onLocation:" + interval + "," + location.getLongitude() + "," + location.getLatitude());
            if (mLocationListener != null) {
                mLocationListener.onLocationChanged(location);
            }
        }
		
		@Override
        public void reportSvStatus(int svCount, int[] svidWithFlags, float[] cn0s, float[] svElevations,
                                   float[] svAzimuths, float[] svCarrierFreqs, float[] basebandCn0s) throws RemoteException {
            if (mLocationListener != null) {
                mLocationListener.repoSvStatus(svCount, svidWithFlags, cn0s, svElevations, svAzimuths, svCarrierFreqs, basebandCn0s);
            }
        }

        @Override
        public void onProviderEnabled() throws RemoteException {
            Log.d(TAG, "onProviderEnabled");
            if (mLocationListener != null) {
                mLocationListener.onProviderEnabled(PROVIDER_NAME);
            }
        }

        @Override
        public void onProviderDisabled() throws RemoteException {
            Log.d(TAG, "onProviderDisabled");
            if (mLocationListener != null) {
                mLocationListener.onProviderDisabled(PROVIDER_NAME);
            }
        }
    };

    /**
     * check is need reconnect
     *
     * @param bindInfo
     */
    private void checkConnectServerStatus(final String bindInfo) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (binder == null) {
                    Log.d(TAG, "bindExtServer[" + bindInfo + "] timeout or disconnect,rebind!");
                    bindServer();
                }
            }
        }, DELAY_BIND_SERVICE);
    }

    /**
     * set service callback
     */
    private void setCallback() {
        Log.d(TAG, "setCallback");
        try {
            if (binder != null) {
                binder.setCallback(callback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            binder = null;
            Log.e(TAG, "setCallback RemoteException: " + e.getMessage());
        }
    }

    public String getProviderName() {
        return PROVIDER_NAME;
    }

    private boolean isServiceAppInstalled(Context context, String pkgName) {
        if (pkgName != null) {
            try {
                context.getPackageManager().getPackageInfo(pkgName, 0);
            } catch (Exception e) {
                Log.d(TAG, "isServiceAppInstalled false: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
