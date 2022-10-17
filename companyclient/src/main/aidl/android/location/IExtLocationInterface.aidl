// IExtiLocationInterface.aidl
package android.location;

import android.location.Location;
import android.location.IExtLocationCallback;
// Declare any non-default types here with import statements

/**
 * {@hide}
 */
interface IExtLocationInterface {
    //setCallback
    void setCallback(in IExtLocationCallback callback);

    //Get GNSS hardware information and return hardware information
    String getGnssHwInfo();

    //enable source capability
    boolean setEnable();

    //disable source capability
    boolean setDisable();
}