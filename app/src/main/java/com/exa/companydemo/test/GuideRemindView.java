/**
 * Copyright (c) 2018-2021 ThunderSoft
 * All Rights Reserved by Thunder Software Technology Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with ThunderSoft in writing by applicable law.
 */

package com.exa.companydemo.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.internal.policy.PhoneWindow;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.R;
import com.gxa.car.scene.SceneManager;
import com.gxa.car.scene.ServiceStateListener;

/**
 * 温馨提醒
 *
 * @author: Gary
 * @Date:
 */
public class GuideRemindView extends FrameLayout implements View.OnClickListener {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParam;
    private SceneManager mSceneManager;
    private Context mContext;
    //温馨提醒 true 不弹 false 弹
    private boolean isPromptAgain;
    private View mRootView;
    private static final String GAEI_FULLSCREEN = "gaei_fullscreen";
    /**
     * 退出温馨提醒广播
     */
    private static final String EXIT_DISCLAIMER_TEMP = "syncore.windows.EXIT_DISCLAIMER_TEMP";
    private boolean isShowInWindow;

    private static Window mWindow;
    private View mDecor;

    public GuideRemindView(Context context) {
        this(context, null);
    }

    public GuideRemindView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideRemindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initWindowManager();
        initView();
    }


    public void showGuideView() {
        initView();
        initScene();
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout_full, null);
        mRootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        Button sure_button = mRootView.findViewById(R.id.sure_button);
        Button cancel_button = mRootView.findViewById(R.id.cancel_button);
        sure_button.setOnClickListener(v -> {
            exitGuideRemindView();
        });
        cancel_button.setOnClickListener(v -> {
            ((Activity) mContext).getWindow().setNavigationBarColor(0);
        });
    }

    /**
     * window层级初始化
     */
    public void initScene() {
        mSceneManager = SceneManager.getInstance(mContext);
        if (mServiceStateListener != null) {
            mSceneManager.registerServiceStateListener(mServiceStateListener);
        }
    }

    @SuppressLint({"WrongConstant"})
    private void initWindowManager() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mParam = new WindowManager.LayoutParams();
        mParam.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        final int flags = WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mParam.setTitle(GAEI_FULLSCREEN);
        mParam.flags = flags;
        mParam.format = PixelFormat.TRANSPARENT;
        mParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mParam.width = 1920;
        mParam.height = 1080;
        mParam.type = 1999;
        mParam.gravity = Gravity.TOP | Gravity.START;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (!isShowInWindow) {
            isShowInWindow = true;
        }
        return result;
    }

    /**
     * 退出温馨页面
     */
    private void exitGuideRemindView() {
        mWindowManager.removeView(mRootView);
        L.d("设置状态栏导航栏颜色为  透明");
        ((Activity) mContext).getWindow().setStatusBarColor(mContext.getColor(R.color.transparent));
        ((Activity) mContext).getWindow().setNavigationBarColor(mContext.getColor(R.color.transparent));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    ServiceStateListener mServiceStateListener = new ServiceStateListener() {
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onServiceStarted() {
            if (mParam != null) {
                //2512 层级 31
//                mParam.type = mSceneManager.getWindowType(SceneManager.SCENE_DISCLAIMER);
            }
            // 温馨提醒view添加到window
            mWindowManager.addView(mRootView, mParam);
//            ((Activity) mContext).getWindow().setNavigationBarColor(mContext.getColor(R.color.gray));
//            ((Activity) mContext).getWindow().setStatusBarColor(mContext.getColor(R.color.gray));
        }

        @Override
        public void onServiceDied() {

        }
    };

    public int getSceneType() {
        return mParam.type;
    }

}
