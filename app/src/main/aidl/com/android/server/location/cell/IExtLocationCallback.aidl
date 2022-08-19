package com.android.server.location.cell;

import android.location.Location;

interface IExtLocationCallback {
    //interval：时间间隔（单位：毫秒）
    void onLocation(long interval,in Location location);
}
