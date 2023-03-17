package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();
        BaseConstants.init();
        L.init("CompanyDemo", true);
        app = this;
        String screen = "屏幕宽高px：" + Tools.getScreenW(this) + "," + Tools.getScreenH(this);
        String screenDp = "屏幕宽高dp：" + (int)(Tools.getScreenW(this)/Tools.getScreenDensity(this))
                + "," + (int)(Tools.getScreenH(this)/Tools.getScreenDensity(this));
        L.w(screen);
        L.w("屏幕密度：" + Tools.getScreenDensity(this));
        L.w(screenDp);
        L.w("Android OS is " + Build.VERSION.RELEASE + " , SDK_INT= " + Build.VERSION.SDK_INT);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                L.dd("----------" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                L.dd("----------" + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                L.dd("----------" + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                L.dd("----------" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                L.dd("----------" + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                L.dd("----------" + activity.getLocalClassName());
            }
        });
    }

    public static Context getContext() {
        return app;
    }
}
