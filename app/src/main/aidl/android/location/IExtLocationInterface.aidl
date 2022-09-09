// IExtiLocationInterface.aidl
package android.location;

import android.location.Location;
import android.location.IExtLocationCallback;
// Declare any non-default types here with import statements

/**
 * {@hide}
 */
interface IExtLocationInterface {
    //setLocationRequest callback(interval：Unit: mm)
    void setLocationRequest(long interval,in IExtLocationCallback callback);

    //Get GNSS hardware information and return hardware information
    String getGnssHwInfo();
}