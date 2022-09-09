package android.location;

import android.location.Location;

/**
 * {@hide}
 */
interface IExtLocationCallback {
    //interval
    void onLocation(long interval,in Location location);
}
