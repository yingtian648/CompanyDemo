package android.location;

import android.location.Location;

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
}
