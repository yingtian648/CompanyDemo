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

    boolean isGnssVisibilityControlSupported();//是否支持Gnss可见性控制

    void cleanUp();

    void setPositionMode(in int mode, in int recurrence, in int min_interval,
                in int preferred_accuracy, in int preferred_time, in boolean lowPowerMode);//设置位置模式

    void deleteAidingData();//删除辅助信息

    int readNmea(in byte[] buffer, in int bufferSize);//读取 nmea 信息

    void agpsNiMessage(in byte[] msg, in int length);//（Network-initiated）消息

    void setAgpsServer(in int type, in String hostname, in int port);//辅助定位服务

    void sendNiResponse(in int notificationId, in int userResponse);//发送 NI 响应

    void agpsSetRefLocationCellid(in int type, in int mcc, in int mnc, in int lac, in int cid);//AGPS 设置引用位置

    void agpsSetId(in int type, in String setid);//AGPS 设置 id
}
