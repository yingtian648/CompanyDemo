package com.exa.systemui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * @Author lsh
 * @Date 2023/3/17 12:34
 * @Description
 */
public class SystemUIService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
