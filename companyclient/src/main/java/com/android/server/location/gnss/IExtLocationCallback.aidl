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

import android.location.Location;
import android.location.GnssMeasurementsEvent;
import android.location.GnssAntennaInfo;
import android.location.GnssNavigationMessage;

/**
 * {@hide}
 */
interface IExtLocationCallback {

    oneway void onLocation(in Location location);


    /**
     * svidWithFlags 带标志的卫星编号d
     * cn0s 带标志的卫星编号d
     * svElevations 带标志的卫星编号d
     * svAzimuths 方位角
     * svCarrierFreqs 载波频率
     * basebandCn0s 基带捕获灵敏度 30dBHz
     */
    oneway void reportSvStatus(in int svCount, in int[] svidWithFlags, in float[] cn0s,in float[] svElevations,
       in float[] svAzimuths,in float[] svCarrierFreqs,in float[] basebandCn0s);

    void onProviderEnabled();

    void onProviderDisabled();

    boolean isInEmergencySession();

    void reportAGpsStatus(int agpsType, int agpsStatus, in byte[] suplIpAddr);

    void reportNmea(long time);

    void reportMeasurementData(in GnssMeasurementsEvent event);

    void reportAntennaInfo(in List<GnssAntennaInfo> antennaInfos);

    void reportNavigationMessage(in GnssNavigationMessage event);

    void setTopHalCapabilities(int topHalCapabilities);

    void setSubHalMeasurementCorrectionsCapabilities(int subHalCapabilities);

    void setGnssYearOfHardware(int yearOfHardware);

    void setGnssHardwareModelName(String modelName);

    void reportGnssServiceDied();

    void reportLocationBatch(in Location[] locationArray) ;

    void psdsDownloadRequest();

    void reportGeofenceTransition(int geofenceId, in Location location, int transition, long transitionTimestamp);

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
