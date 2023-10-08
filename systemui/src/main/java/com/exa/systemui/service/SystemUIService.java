package com.exa.systemui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.providers.settings.GlobalSettingsProto;

import com.exa.systemui.SystemUIApplication;

import androidx.annotation.Nullable;

/**
 * @Author lsh
 * @Date 2023/3/17 12:34
 * @Description
 */
public class SystemUIService extends Service {
    // adb shell am start-service com.exa.systemui/com.exa.systemui.service.SystemUIService
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication) getApplication()).startSystemUIMain();
    }
}
