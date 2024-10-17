package com.zlingsmart.demo.mtestapp;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;

/**
 * @Author lsh
 * @Date 2023/3/31 17:50
 * @Description
 */
public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        L.init(getAppLabel(), true);
        BaseConstants.init();
        instance = this;
    }

    public static App getContext() {
        return instance;
    }

    private String getAppLabel() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        return applicationInfo.loadLabel(getPackageManager()).toString();
    }
}
