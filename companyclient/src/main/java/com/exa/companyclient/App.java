package com.exa.companyclient;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        L.init("client--->", true);
        BaseConstants.init();
    }

    public static Context getContext() {
        return app;
    }
}
