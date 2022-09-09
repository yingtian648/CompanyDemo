// IExtiLocationInterface.aidl
package gxa.car.extlocationservice;

import gxa.car.extlocationservice.GnssHwInfo;
// Declare any non-default types here with import statements

interface IExtiLocationInterface {

    int getLocationMode();//获取定位模式

    int getFirstLocationTime();//获取第一次定位时间(单位:秒)

    boolean setLocationMode(int mode);//设置定位模式

    GnssHwInfo getGnssHwInfo();//获取GNSS硬件信息
}