package android.location;

import android.location.Location;
import android.location.GnssMeasurementsEvent;
import android.location.GnssAntennaInfo;
import android.location.GnssNavigationMessage;

/**
 * {@hide}
 */
interface IExtLocationCallback {

    void onLocation(in Location location);

    //GnssLocationProvider.SvStatusInfo or GnssStatus.java 143
    void reportSvStatus(in int svCount, in int[] svidWithFlags, in float[] cn0s,in float[] svElevations,
       in float[] svAzimuths,in float[] svCarrierFreqs,in float[] basebandCn0s);

    void onProviderEnabled();

    void onProviderDisabled();

    boolean isInEmergencySession();//是否在紧急会话

    void reportAGpsStatus(in int agpsType, in int agpsStatus, in byte[] suplIpAddr);//辅助 GPS

    void reportNmea(in long time);//协议

    void reportMeasurementData(in GnssMeasurementsEvent event); //上报测量信息

    void reportAntennaInfo(in List<GnssAntennaInfo> antennaInfos);//上报天线信息

    void reportNavigationMessage(in GnssNavigationMessage event);//上报导航信息

    void setTopHalCapabilities(in int topHalCapabilities);//顶部hal能力

    void setSubHalMeasurementCorrectionsCapabilities(in int subHalCapabilities);//测量纠正能力

    void setGnssYearOfHardware(in int yearOfHardware);

    void setGnssHardwareModelName(in String modelName);

    void reportGnssServiceDied();//上报Gnss服务死亡

    void reportLocationBatch(in Location[] locationArray) ;//上报一批位置

    void psdsDownloadRequest();//规格数据表下载请求

    void reportGeofenceTransition(in int geofenceId, in Location location, in int transition, in long transitionTimestamp);//地理围栏

    void reportGeofenceStatus(in int status, in Location location);//上报地理围栏状态

    void reportGeofenceAddStatus(in int geofenceId, in int status);//上报地理围栏添加状态

    void reportGeofenceRemoveStatus(in int geofenceId, in int status);//上报地理围栏移出状态

    void reportGeofencePauseStatus(in int geofenceId, in int status);//上报地理围栏暂停状态

    void reportGeofenceResumeStatus(in int geofenceId, in int status);//上报地理围栏恢复状态

    // Implements method nfwNotifyCb() in IGnssVisibilityControlCallback.hal. 上报Nfw通知，由GnssVisibilityControl上报
    void reportNfwNotification(in String proxyAppPackageName, in byte protocolStack,
                String otherProtocolStackName, in byte requestor, in String requestorId, in byte responseType,
                in boolean inEmergencyMode, in boolean isCachedLocation);

    //报告 NI 通知
    void reportNiNotification(in int notificationId, in int niType, in int notifyFlags,
                in int timeout, in int defaultResponse, in String requestorId, String text,
                in int requestorIdEncoding, in int textEncoding);
}
