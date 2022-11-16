package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.Constants;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();
        BaseConstants.init();
        L.init("main--->", true);
        app = this;
        String screen = "屏幕宽高：" + Tools.getScreenW(this) + "," + Tools.getScreenH(this);
        L.d(screen);
        L.d("Android OS is " + Build.VERSION.RELEASE + " , SDK_INT= " + Build.VERSION.SDK_INT);
    }

    public static Context getContext() {
        return app;
    }
}
