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
    @Override
    public void onCreate() {
        super.onCreate();
        L.init("testapp--->",true);
        BaseConstants.init();
    }
}
