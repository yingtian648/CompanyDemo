package com.zlingsmart.demo.mtestapp;

import android.app.Application;

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
        L.init("MCompanyTestApp-->", true);
        BaseConstants.init();
        instance = this;
    }

    public static App getContext() {
        return instance;
    }
}
