package com.exa.systemui;

import android.app.Application;
import android.content.res.Configuration;

import com.exa.baselib.utils.CrashHandle;
import com.exa.baselib.utils.L;

import androidx.annotation.NonNull;

/**
 * @Author lsh
 * @Date 2023/3/17 11:48
 * @Description
 */
public class SystemUIApplication extends Application {
    private static SystemUiMain mSystemUiMain;

    @Override
    public void onCreate() {
        super.onCreate();
        L.init("systemui-lsh", true);
        CrashHandle.getInstance().init(this);

        startMain(this);
    }

    public static void startMain(Application context) {
        if (mSystemUiMain == null){
            mSystemUiMain = SystemUiMain.getInstance(context);
        }
        mSystemUiMain.start();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSystemUiMain.onConfigurationChanged(newConfig);
    }
}
