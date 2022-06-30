package com.exa.companydemo.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.exa.companydemo.Constants;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context app;
    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();
        app = this;
    }

    public static Context getContext() {
        return app;
    }
}
