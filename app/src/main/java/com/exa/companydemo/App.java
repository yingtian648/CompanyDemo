package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.exa.baselib.utils.L;
import com.exa.companydemo.Constants;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();
        L.init("main--->", true);
        app = this;
    }

    public static Context getContext() {
        return app;
    }
}
