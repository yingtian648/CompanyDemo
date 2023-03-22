package com.exa.systemui;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.exa.baselib.utils.CrashHandle;
import com.exa.baselib.utils.L;

import androidx.annotation.NonNull;

/**
 * @Author lsh
 * @Date 2023/3/17 11:48
 * @Description
 */
public class SystemUIApplication extends Application {
    private SystemUiMain mSystemUiMain;
    private static Application mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        L.init("systemui-lsh", true);
        CrashHandle.getInstance().init(this);

//        startSystemUIMain(mContext);
    }

    public void startSystemUIMain() {
        if (mSystemUiMain == null){
            mSystemUiMain = SystemUiMain.getInstance(mContext);
        }
        mSystemUiMain.start();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        L.dd();
        mSystemUiMain.onConfigurationChanged(newConfig);
    }
}
