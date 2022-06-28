package com.exa.companydemo.base;

import android.app.Application;

import com.exa.companydemo.Constants;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Constants.init();
    }
}
