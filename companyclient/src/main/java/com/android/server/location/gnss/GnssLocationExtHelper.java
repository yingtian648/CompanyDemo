/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except compliance with the License.
 * You may obtaa copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to writing, software
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
import android.location.GnssAntennaInfo;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.IExtLocationCallback;
import android.location.IExtLocationInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a GNSS position mode.
 */
public class GnssLocationExtHelper {
    private static final String TAG = "GnssLocationExtHelper";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    private static final boolean VERBOSE = Log.isLoggable(TAG, Log.VERBOSE);

    private final String PROVIDER_NAME = "expand";
    private IExtLocationInterface binder;
    private Context mContext;
    private final Handler mHandler;
    private Callback mLocationListener;
    private static final String SERVICE_PACKAGE_NAME = "com.exa.companydemo";
    private static final String SERVICE_CLASS_NAME = "com.exa.companydemo.aidlservice.ExtLocationService";

    private final long DELAY_BIND_SERVICE = 1000;//delay bind service

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
        Log.d(TAG, "GnssLocationExtHelper init");
    }

    public static boolean isAvailable(Context context) {
        return isServiceAppInstalled(context, SERVICE_PACKAGE_NAME);
    }

    public void bindServer() {
        Log.v(TAG, "bindExtServer");
        if (binder == null && mContext != null) {
            try {
                Intent intentExt = new Intent();
                intentExt.setClassName(SERVICE_PACKAGE_NAME, SERVICE_CLASS_NAME);
                checkConnectServerStatus(intentExt.getComponent() == null ? intentExt.getPackage() : intentExt.getComponent().toString());
                mContext.bindService(intentExt, mServiceConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "bindExtServer Exception: " + e.getMessage());
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
            if (DEBUG) Log.d(TAG, "onLocation:" + location);
            if (mLocationListener != null) {
                mLocationListener.onLocationChanged(location);
            }
        }

        @Override
        public void reportSvStatus(int svCount, int[] svidWithFlags, float[] cn0s, float[] svElevations,
                                   float[] svAzimuths, float[] svCarrierFreqs, float[] basebandCn0s) throws RemoteException {
            if (DEBUG) {
                Log.d(TAG, "reportSvStatus:" + svCount
                        + Arrays.toString(svidWithFlags) + Arrays.toString(cn0s)
                        + Arrays.toString(svElevations) + Arrays.toString(svAzimuths)
                        + Arrays.toString(svCarrierFreqs) + Arrays.toString(basebandCn0s)
                );
            }
            if (mLocationListener != null && svCount > 0) {
                if (svidWithFlags != null && cn0s != null && svElevations != null
                        && svAzimuths != null && svCarrierFreqs != null && basebandCn0s != null
                        && svidWithFlags.length >= svCount
                        && cn0s.length >= svCount
                        && svElevations.length >= svCount
                        && svAzimuths.length >= svCount
                        && svCarrierFreqs.length >= svCount
                        && basebandCn0s.length >= svCount) {
                    mLocationListener.repoSvStatus(svCount, svidWithFlags, cn0s, svElevations, svAzimuths, svCarrierFreqs, basebandCn0s);
                } else {
                    Log.i(TAG, "reportSvStatus err data: svCount cannot be greater than the array length");
                }
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

        @Override
        public boolean isInEmergencySession() throws RemoteException {
            return false;
        }

        @Override
        public void reportAGpsStatus(int agpsType, int agpsStatus, byte[] suplIpAddr) throws RemoteException {

        }

        @Override
        public void reportNmea(long time) throws RemoteException {

        }

        @Override
        public void reportMeasurementData(GnssMeasurementsEvent event) throws RemoteException {

        }

        @Override
        public void reportAntennaInfo(List<GnssAntennaInfo> antennaInfos) throws RemoteException {

        }

        @Override
        public void reportNavigationMessage(GnssNavigationMessage event) throws RemoteException {

        }

        @Override
        public void setTopHalCapabilities(int topHalCapabilities) throws RemoteException {

        }

        @Override
        public void setSubHalMeasurementCorrectionsCapabilities(int subHalCapabilities) throws RemoteException {

        }

        @Override
        public void setGnssYearOfHardware(int yearOfHardware) throws RemoteException {

        }

        @Override
        public void setGnssHardwareModelName(String modelName) throws RemoteException {

        }

        @Override
        public void reportGnssServiceDied() throws RemoteException {

        }

        @Override
        public void reportLocationBatch(Location[] locationArray) throws RemoteException {

        }

        @Override
        public void psdsDownloadRequest() throws RemoteException {

        }

        @Override
        public void reportGeofenceTransition(int geofenceId, Location location, int transition, long transitionTimestamp) throws RemoteException {

        }

        @Override
        public void reportGeofenceStatus(int status, Location location) throws RemoteException {

        }

        @Override
        public void reportGeofenceAddStatus(int geofenceId, int status) throws RemoteException {

        }

        @Override
        public void reportGeofenceRemoveStatus(int geofenceId, int status) throws RemoteException {

        }

        @Override
        public void reportGeofencePauseStatus(int geofenceId, int status) throws RemoteException {

        }

        @Override
        public void reportGeofenceResumeStatus(int geofenceId, int status) throws RemoteException {

        }

        @Override
        public void reportNfwNotification(String proxyAppPackageName, byte protocolStack, String otherProtocolStackName, byte requestor, String requestorId, byte responseType, boolean inEmergencyMode, boolean isCachedLocation) throws RemoteException {

        }

        @Override
        public void reportNiNotification(int notificationId, int niType, int notifyFlags, int timeout, int defaultResponse, String requestorId, String text, int requestorIdEncoding, int textEncoding) throws RemoteException {

        }
    };

    /**
     * check is need reconnect
     *
     * @param bindInfo
     */
    private void checkConnectServerStatus(final String bindInfo) {
        mHandler.removeCallbacksAndMessages(null);
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

    private static boolean isServiceAppInstalled(Context context, String pkgName) {
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

    public interface Callback extends LocationListener {
        void repoSvStatus(int svCount, int[] svidWithFlags, float[] cn0s,
                          float[] svElevations, float[] svAzimuths, float[] svCarrierFreqs,
                          float[] basebandCn0s);

        boolean isInEmergencySession();

        void reportAGpsStatus(int agpsType, int agpsStatus, byte[] suplIpAddr);

        void reportNmea(long time);

        void reportMeasurementData(GnssMeasurementsEvent event);

        void reportAntennaInfo(List<GnssAntennaInfo> antennaInfos);

        void reportNavigationMessage(GnssNavigationMessage event);

        void setTopHalCapabilities(int topHalCapabilities);

        void setSubHalMeasurementCorrectionsCapabilities(int subHalCapabilities);

        void setGnssYearOfHardware(int yearOfHardware);

        void setGnssHardwareModelName(String modelName);

        void reportGnssServiceDied();

        void reportLocationBatch(Location[] locationArray);

        void psdsDownloadRequest();

        void reportGeofenceTransition(int geofenceId, Location location, int transition, long transitionTimestamp);

        void reportGeofenceStatus(int status, Location location);

        void reportGeofenceAddStatus(int geofenceId, int status);

        void reportGeofenceRemoveStatus(int geofenceId, int status);

        void reportGeofencePauseStatus(int geofenceId, int status);

        void reportGeofenceResumeStatus(int geofenceId, int status);

        void reportNfwNotification(String proxyAppPackageName, byte protocolStack,
                                   String otherProtocolStackName, byte requestor, String requestorId, byte responseType,
                                   boolean inEmergencyMode, boolean isCachedLocation);

        void reportNiNotification(int notificationId, int niType, int notifyFlags,
                                  int timeout, int defaultResponse, String requestorId, String text,
                                  int requestorIdEncoding, int textEncoding);
    }
}
