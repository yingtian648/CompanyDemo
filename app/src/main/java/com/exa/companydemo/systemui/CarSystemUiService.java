package com.exa.companydemo.systemui;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.exa.companydemo.R;

import androidx.annotation.Nullable;

public class CarSystemUiService extends Service {

    private final String TAG = "CarSystemUiService====";
    private Context mContext;

    private final int STATUS_BAR_WIDTH = 776;
    private final int STATUS_BAR_HEIGHT = 76;
    private final int NAVIGATION_BAR_HEIGHT = 100;

    private WindowManager.LayoutParams mStatusBarParams;
    private WindowManager.LayoutParams mNavigationBarParams;
    private WindowManager mWindowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mContext = this;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        initStatusBar();
        initNavigationBar();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initStatusBar() {
        Log.d(TAG, "initStatusBar");
        mStatusBarParams = new WindowManager.LayoutParams();
        mStatusBarParams.format = PixelFormat.TRANSLUCENT;
        //mStateBarParams.type = WindowManager.LayoutParams.TYPE_STATUS_BAR;
        mStatusBarParams.type = WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL;

        mStatusBarParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        mStatusBarParams.width = STATUS_BAR_WIDTH;
        mStatusBarParams.height = STATUS_BAR_HEIGHT;
        mStatusBarParams.gravity = Gravity.TOP | Gravity.RIGHT;
        mStatusBarParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        View view = LayoutInflater.from(mContext).inflate(R.layout.system_ui_statusbar,null,false);
        mWindowManager.addView(view,mStatusBarParams);
    }

    @SuppressLint("WrongConstant")
    private void initNavigationBar() {
        Log.d(TAG, "initNavigationBar");
        mNavigationBarParams = new WindowManager.LayoutParams();

        mNavigationBarParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                | WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        mNavigationBarParams.format = PixelFormat.TRANSLUCENT;
//        mNavigationBarParams.type = WindowManager.LayoutParams.TYPE_NAVIGATION_BAR;
        mNavigationBarParams.type = 2019;
        mNavigationBarParams.width = 500; //WindowManager.LayoutParams.MATCH_PARENT;
        mNavigationBarParams.height = NAVIGATION_BAR_HEIGHT;
        mNavigationBarParams.gravity = Gravity.BOTTOM;

        View view = LayoutInflater.from(mContext).inflate(R.layout.system_ui_navigationbar,null,false);
        mWindowManager.addView(view,mNavigationBarParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
