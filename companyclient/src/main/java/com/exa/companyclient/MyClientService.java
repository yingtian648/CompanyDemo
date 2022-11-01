package com.exa.companyclient;

import android.app.Service;
import android.content.Intent;
import com.android.server.location.gnss.GnssLocationExtHelper;

import android.location.GnssAntennaInfo;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.os.IBinder;

import com.android.server.location.gnss.GnssLocationExtHelper.Callback;
import com.exa.baselib.utils.L;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 测试服务中绑定服务
 */
public class MyClientService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d(getClass().getName() + ": onStartCommand");

        GnssLocationExtHelper provider = GnssLocationExtHelper.getInstance();
        provider.init(this, new GnssLocationExtListener());
        provider.bindServer();

        return super.onStartCommand(intent, flags, startId);
    }

    private final class GnssLocationExtListener implements GnssLocationExtHelper.Callback {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            L.d("onLocationChanged: " + location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void repoSvStatus(int svCount, int[] svidWithFlags, float[] cn0s, float[] svElevations, float[] svAzimuths, float[] svCarrierFreqs, float[] basebandCn0s) {
            L.d("repoSvStatus: " + svCount + ",svidWithFlags:" + Arrays.toString(svidWithFlags)
                    + ",cn0s:" + Arrays.toString(cn0s)
                    + ",svElevations:" + Arrays.toString(svElevations)
                    + ",svAzimuths:" + Arrays.toString(svAzimuths)
                    + ",svCarrierFreqs:" + Arrays.toString(svCarrierFreqs)
                    + ",basebandCn0s:" + Arrays.toString(basebandCn0s)
            );
        }

        @Override
        public boolean isInEmergencySession() {
            return false;
        }

        @Override
        public void reportAGpsStatus(int agpsType, int agpsStatus, byte[] suplIpAddr) {

        }

        @Override
        public void reportNmea(long time) {

        }

        @Override
        public void reportMeasurementData(GnssMeasurementsEvent event) {

        }

        @Override
        public void reportAntennaInfo(List<GnssAntennaInfo> antennaInfos) {

        }

        @Override
        public void reportNavigationMessage(GnssNavigationMessage event) {

        }

        @Override
        public void setTopHalCapabilities(int topHalCapabilities) {

        }

        @Override
        public void setSubHalMeasurementCorrectionsCapabilities(int subHalCapabilities) {

        }

        @Override
        public void setGnssYearOfHardware(int yearOfHardware) {

        }

        @Override
        public void setGnssHardwareModelName(String modelName) {

        }

        @Override
        public void reportGnssServiceDied() {

        }

        @Override
        public void reportLocationBatch(Location[] locationArray) {

        }

        @Override
        public void psdsDownloadRequest() {

        }

        @Override
        public void reportGeofenceTransition(int geofenceId, Location location, int transition, long transitionTimestamp) {

        }

        @Override
        public void reportGeofenceStatus(int status, Location location) {

        }

        @Override
        public void reportGeofenceAddStatus(int geofenceId, int status) {

        }

        @Override
        public void reportGeofenceRemoveStatus(int geofenceId, int status) {

        }

        @Override
        public void reportGeofencePauseStatus(int geofenceId, int status) {

        }

        @Override
        public void reportGeofenceResumeStatus(int geofenceId, int status) {

        }

        @Override
        public void reportNfwNotification(String proxyAppPackageName, byte protocolStack, String otherProtocolStackName, byte requestor, String requestorId, byte responseType, boolean inEmergencyMode, boolean isCachedLocation) {

        }

        @Override
        public void reportNiNotification(int notificationId, int niType, int notifyFlags, int timeout, int defaultResponse, String requestorId, String text, int requestorIdEncoding, int textEncoding) {

        }
    }

    private void reportAGpsStatus(int agpsType, int agpsStatus, byte[] suplIpAddr) {

    }
}
