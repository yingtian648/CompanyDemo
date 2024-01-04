package com.exa.companyclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.CrashHandle;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Application app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        L.init("MCompanyClient", true);
        BaseConstants.init();
        CrashHandle.getInstance().init(this);
        Tools.logScreenWH(this);
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

    public static Application getContext() {
        return app;
    }
}
