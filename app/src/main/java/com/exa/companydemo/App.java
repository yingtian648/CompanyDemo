package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.accessibility.MAccessibility;
import com.exa.companydemo.utils.PathUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Application app;
    public static int index = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();
        BaseConstants.init();
        L.init("MCompanyDemo", true);
        L.w("开机时长：" + DateUtil.getTimeStr(SystemClock.elapsedRealtime()));
        PathUtil.INSTANCE.init(this);
        app = this;
        Tools.logScreenWH(this);

        //启动无障碍服务
//        Settings.Secure.putString(getContentResolver(),
//                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
//                getPackageName() + "/" + MAccessibility.class.getName());
//        Settings.Secure.putInt(getContentResolver(),
//                Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }

    private void listenActivityLife() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        L.dd();
    }

    public static Application getContext() {
        return app;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        L.dd("---App---" + newConfig.getLocales().toString());
    }

    public static void exit() {
        L.d("App exit");
        System.exit(0);
    }
}
