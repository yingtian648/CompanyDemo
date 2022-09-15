package android.location;

import android.location.Location;

/**
 * {@hide}
 */
interface IExtLocationCallback {
    //interval
    void onLocation(in Location location);

    void onProviderEnabled();

    void onProviderDisabled();
}
