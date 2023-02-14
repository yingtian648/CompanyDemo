// IExtiLocationInterface.aidl
package com.exa.companydemo;

import com.exa.companydemo.UserInfo;
import com.exa.companydemo.MCallback;
// Declare any non-default types here with import statements

interface MInterface {

    void setCallback(in MCallback callback);

    int getMode();//获取模式

    boolean setMode(int mode);//设置模式

    UserInfo getInfo(long id);//获取GNSS硬件信息
}