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

    void onLocation(in Location location);


    /**
     * svidWithFlags 带标志的卫星编号d
     * cn0s 带标志的卫星编号d
     * svElevations 带标志的卫星编号d
     * svAzimuths 方位角
     * svCarrierFreqs 载波频率
     * basebandCn0s 基带捕获灵敏度 30dBHz
     */
    void reportSvStatus(in int svCount, in int[] svidWithFlags, in float[] cn0s,in float[] svElevations,
       in float[] svAzimuths,in float[] svCarrierFreqs,in float[] basebandCn0s);

    void onProviderEnabled();

    void onProviderDisabled();

    boolean isInEmergencySession();

    void reportAGpsStatus(in int agpsType, in int agpsStatus, in byte[] suplIpAddr);

    void reportNmea(in long time);

    void reportMeasurementData(in GnssMeasurementsEvent event);

    void reportAntennaInfo(in List<GnssAntennaInfo> antennaInfos);

    void reportNavigationMessage(in GnssNavigationMessage event);

    void setTopHalCapabilities(in int topHalCapabilities);

    void setSubHalMeasurementCorrectionsCapabilities(in int subHalCapabilities);

    void setGnssYearOfHardware(in int yearOfHardware);

    void setGnssHardwareModelName(in String modelName);

    void reportGnssServiceDied();

    void reportLocationBatch(in Location[] locationArray) ;

    void psdsDownloadRequest();

    void reportGeofenceTransition(in int geofenceId, in Location location, in int transition, in long transitionTimestamp);

    void reportGeofenceStatus(in int status, in Location location);

    void reportGeofenceAddStatus(in int geofenceId, in int status);

    void reportGeofenceRemoveStatus(in int geofenceId, in int status);

    void reportGeofencePauseStatus(in int geofenceId, in int status);

    void reportGeofenceResumeStatus(in int geofenceId, in int status);

    void reportNfwNotification(in String proxyAppPackageName, in byte protocolStack,
                String otherProtocolStackName, in byte requestor, in String requestorId, in byte responseType,
                in boolean inEmergencyMode, in boolean isCachedLocation);

    void reportNiNotification(in int notificationId, in int niType, in int notifyFlags,
                in int timeout, in int defaultResponse, in String requestorId, String text,
                in int requestorIdEncoding, in int textEncoding);
}
