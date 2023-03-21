package com.exa.systemui;

import android.app.AlarmManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsetsController;

import com.android.internal.view.AppearanceRegion;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.systemui.databinding.SystemUiStatusbarBinding;

import androidx.databinding.DataBindingUtil;

/**
 * @Author lsh
 * @Date 2023/3/20 17:02
 * @Description
 */
public class StatusBarView implements View.OnClickListener, MCommandQueue.Callback {
    private final Context mContext;
    private final View mRootView;
    private UiModeManager mUiModeManager;
    private int mNightMode;
    private MCommandQueue mCommandQueue;
    protected SystemUiStatusbarBinding mBind;
    private final int mStatusBarBgColor = 0xFFB2B2B2;
    private final int[] ids = {
            R.id.BtnUser,
            R.id.BtnMail,
            R.id.BtnWifi,
    };
    private int mDisplayId;

    public View getRootView() {
        return mRootView;
    }

    public StatusBarView(Context context, UiModeManager modeManager, MCommandQueue commandQueue, int displayId) {
        mContext = context;
        mUiModeManager = modeManager;
        mCommandQueue = commandQueue;
        mDisplayId = displayId;
        mNightMode = modeManager.getNightMode();
        commandQueue.registerCallback(this);
        mBind = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.system_ui_statusbar, null, false);
        mRootView = mBind.getRoot();
        initView();
        registerTimeUpdateReceiver();
    }

    @Override
    public void onSystemBarAppearanceChanged(int displayId, int appearance, AppearanceRegion[]
            appearanceRegions, boolean navbarColorManagedByIme) {
        if (displayId == mDisplayId &&
                appearance == WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS) {
            mBind.tvTime.setTextColor(Color.BLUE);
        } else {
            mBind.tvTime.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void topAppWindowChanged(int displayId, boolean isFullscreen, boolean isImmersive) {
        L.dd("displayId=" + displayId + ", isFullscreen=" + isFullscreen + ", isImmersive=" + isImmersive);
        L.dd(mDisplayId);
        if (displayId == mDisplayId) {
            // 是沉浸式且非全屏
            if (isImmersive && !isFullscreen) {
                mBind.naviView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                mBind.naviView.setBackgroundColor(mStatusBarBgColor);
            }
        }
    }

    private void initView() {
        for (int id : ids) {
            mRootView.findViewById(id).setOnClickListener(this);
        }
        updateTimeUi();
    }

    @Override
    public void onClick(View v) {

    }

    private void registerTimeUpdateReceiver() {
        L.dd();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        mContext.registerReceiver(mTimeUpdateReceiver, filter);
    }

    private BroadcastReceiver mTimeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.dd();
            if (intent == null) return;
            String action = intent.getAction();
            if (action == null || action.isEmpty()) return;
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                //系统每1分钟发送一次广播
                updateTimeUi();
            } else if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                //系统手动更改时间发送广播
                updateTimeUi();
            }
        }
    };

    private void updateTimeUi() {
        mBind.tvTime.setText(DateUtil.getNowDateHM());
    }
}
