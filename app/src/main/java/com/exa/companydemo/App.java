package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.utils.PathUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Application app;

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();
        BaseConstants.init();
        L.init("MCompanyDemo", true);
        PathUtil.INSTANCE.init(this);
        app = this;
        Tools.logScreenWH(this);
    }

    private void listenActivityLife(){
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
        L.dd("---App---");
    }

    public static void exit(){
        L.d("App exit");
        System.exit(0);
    }
}
