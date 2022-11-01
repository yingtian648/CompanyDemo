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

    boolean isGnssVisibilityControlSupported();

    void cleanUp();

    void setPositionMode(in int mode, in int recurrence, in int min_interval,
                in int preferred_accuracy, in int preferred_time, in boolean lowPowerMode);

    void deleteAidingData();

    int readNmea(in byte[] buffer, in int bufferSize);

    void agpsNiMessage(in byte[] msg, in int length);

    void setAgpsServer(in int type, in String hostname, in int port);

    void sendNiResponse(in int notificationId, in int userResponse);

    void agpsSetRefLocationCellid(in int type, in int mcc, in int mnc, in int lac, in int cid);

    void agpsSetId(in int type, in String setid);
}
