package com.gxatek.cockpit.launcher;

import android.app.Application;

import com.exa.baselib.utils.CrashHandle;
import com.exa.baselib.utils.L;


/**
 * @Author lsh
 * @Date 2023/6/27 10:32
 * @Description
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        L.init("launcher-lsh", true);
        CrashHandle.getInstance().init(this);
    }
}
