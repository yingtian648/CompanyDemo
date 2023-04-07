package com.exa.systemui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.UiModeManager;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PatternMatcher;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.Gravity;
import android.view.IDisplayFoldListener;
import android.view.IDisplayWindowListener;
import android.view.IPinnedStackController;
import android.view.IPinnedStackListener;
import android.view.ISystemGestureExclusionListener;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

import com.android.internal.policy.IShortcutService;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.exa.baselib.utils.L;

import com.exa.systemui.common.Dependency;
import com.exa.systemui.minterface.IConfigChangedListener;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SystemUiMain implements MCommandQueue.Callback, IConfigChangedListener {

    private final String TAG = "SystemUiMain";
    private Context mContext;

    private final int STATUS_BAR_WIDTH = 776;
    private final int STATUS_BAR_HEIGHT = 84;
    private final int NAVIGATION_BAR_HEIGHT = 100;

    private WindowManager.LayoutParams mStatusBarParams;
    private WindowManager.LayoutParams mNavigationBarParams;
    private WindowManager mWindowManager;
    private IWindowManager mWindowManagerService;
    private IStatusBarService mBarService;
    private MCommandQueue mCommandQueue;
    private DisplayManager mDisplayManager;
    private int mNightMode;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
    private StatusBarView mStatusbar;
    private NavigationBarView mNavibar;
    private boolean mWallpaperVisible = false;
    private UiModeManager mUiModeManager;
    private int mDisplayId;

    public static SystemUiMain getInstance(Context context) {
        MainHolder.main.init(context);
        return MainHolder.main;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        // 获取当前语言，与之前记录语言对比，可用于响应语言改变
        // Locale locale = mContext.getResources().getConfiguration().getLocales().get(0);
        mNavibar.onConfigurationChanged(configuration);
        mStatusbar.onConfigurationChanged(configuration);
    }

    private static class MainHolder {
        private static final SystemUiMain main = new SystemUiMain();
    }

    private SystemUiMain() {
    }

    private void init(Context context) {
        this.mContext = context;
    }

    @SuppressLint("WrongConstant")
    public void start() {
        Log.d(TAG, "onCreate");
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
        mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
        mUiModeManager = mContext.getSystemService(UiModeManager.class);
        mDisplayId = mDisplayManager.getDisplay(Display.DEFAULT_DISPLAY).getDisplayId();
        registerCallback();
        registerMessageQueue();

        initStatusBar();
        initNavigationBar();

        // 白天黑夜模式获取
        mNightMode = mUiModeManager.getNightMode();
        L.d("isNightMode:" + (mUiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES));
    }

    private void registerMessageQueue() {
        mCommandQueue = new MCommandQueue(mContext);
        mCommandQueue.registerCallback(this);
        mBarService = IStatusBarService.Stub.asInterface(
                ServiceManager.getService("statusbar"));//Context.STATUS_BAR_SERVICE
        /**
         * 1.注册回调
         * 2.result可用于设置SystemUi的ime-mode
         */
        RegisterStatusBarResult result = null;
        try {
            result = mBarService.registerStatusBar(mCommandQueue);
            L.d("register mCommandQueue success");
        } catch (RemoteException ex) {
            ex.printStackTrace();
            L.e("registerStatusBar err", ex);
        }
    }

    private void registerCallback() {
        try {
            if (!mWindowManagerService.hasNavigationBar(mDisplayId)) {
                L.e("hasNavigationBar = false");
            }
        } catch (Exception e) {
            L.e("Cannot get WindowManager.");
            return;
        }
        try {// 注册寄存器堆栈监听
            mWindowManagerService.registerPinnedStackListener(mDisplayId, new IPinnedStackListener.Stub() {
                @Override
                public void onListenerRegistered(IPinnedStackController iPinnedStackController) throws RemoteException {
                    L.dd();
                }

                @Override
                public void onMovementBoundsChanged(boolean b) throws RemoteException {
                    L.dd();
                }

                @Override
                public void onImeVisibilityChanged(boolean b, int i) throws RemoteException {
                    L.dd();
                }

                @Override
                public void onActionsChanged(ParceledListSlice parceledListSlice) throws RemoteException {
                    L.dd();
                }

                @Override
                public void onActivityHidden(ComponentName componentName) throws RemoteException {
                    L.dd();
                }

                @Override
                public void onDisplayInfoChanged(DisplayInfo displayInfo) throws RemoteException {
                    L.dd();
                }

                @Override
                public void onConfigurationChanged() throws RemoteException {
                    L.dd();
                }

                @Override
                public void onAspectRatioChanged(float v) throws RemoteException {
                    L.dd();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            L.e("registerPinnedStackListener err", e);
        }
        try {
            // 墙纸可见性改变
            mWindowManagerService.registerWallpaperVisibilityListener(new IWallpaperVisibilityListener.Stub() {
                @Override
                public void onWallpaperVisibilityChanged(boolean newVisibility, int displayId) throws RemoteException {
                    mWallpaperVisible = newVisibility;
                    L.dd(newVisibility + "," + displayId);
                }
            }, mDisplayId);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("registerWallpaperVisibilityListener err", e);
        }

        IntentFilter overlayFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
        overlayFilter.addDataScheme("package");
        overlayFilter.addDataSchemeSpecificPart("android", PatternMatcher.PATTERN_LITERAL);
        mContext.registerReceiver(mReceiver, overlayFilter, null, null);
    }

    public static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("onReceive:" + intent.getAction());
        }
    };

    @SuppressLint("RtlHardcoded")
    private void initStatusBar() {
        Log.d(TAG, "initStatusBar");
        mStatusBarParams = new WindowManager.LayoutParams();
        mStatusBarParams.format = PixelFormat.TRANSLUCENT;
        mStatusBarParams.type = WindowManager.LayoutParams.TYPE_STATUS_BAR;
//        mStatusBarParams.type = WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL;
        mStatusBarParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        mStatusBarParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mStatusBarParams.width = WindowManager.LayoutParams.MATCH_PARENT;//STATUS_BAR_WIDTH;
        mStatusBarParams.height = STATUS_BAR_HEIGHT;
        mStatusBarParams.gravity = Gravity.TOP | Gravity.RIGHT;
        // X轴偏移45px
        mStatusBarParams.x = 45;

        mStatusbar = new StatusBarView(mContext, mUiModeManager, mCommandQueue, mDisplayId);
        mWindowManager.addView(mStatusbar.getRootView(), mStatusBarParams);
    }

    @SuppressLint("WrongConstant")
    private void initNavigationBar() {
        Log.d(TAG, "initNavigationBar");
        mNavigationBarParams = new WindowManager.LayoutParams();

        mNavigationBarParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                | WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        mNavigationBarParams.format = PixelFormat.TRANSPARENT;
        mNavigationBarParams.type = 2019;//WindowManager.LayoutParams.TYPE_NAVIGATION_BAR;
        mNavigationBarParams.width = WindowManager.LayoutParams.MATCH_PARENT; //WindowManager.LayoutParams.MATCH_PARENT;
        mNavigationBarParams.height = NAVIGATION_BAR_HEIGHT;
        mNavigationBarParams.gravity = Gravity.BOTTOM;

        mNavibar = new NavigationBarView(mContext, mUiModeManager, mCommandQueue, mDisplayId);
        mWindowManager.addView(mNavibar.getRootView(), mNavigationBarParams);

        WallpaperManager wallpaperManager = mContext.getSystemService(WallpaperManager.class);
        wallpaperManager.addOnColorsChangedListener((WallpaperManager.OnColorsChangedListener) (colors, which) -> {
            L.dd("colors = " + colors + ", witch = " + which);
        }, null);
    }

    // MCommandQueue 回调-打断瞬态
    @Override
    public void abortTransient(int displayId, int[] types) {
        // 移除——延时隐藏瞬态systemui
        mHandler.removeCallbacksAndMessages(null);
    }

    // MCommandQueue 回调-显示瞬态
    @Override
    public void showTransient(int displayId, int[] types) {
        // 延时隐藏瞬态systemui
//        mHandler.postDelayed(() -> hideTransient(displayId), 2500);
    }

    // 隐藏瞬态systemui
    private void hideTransient(int displayId) {
        try {
            mWindowManagerService.hideTransientBars(displayId);
        } catch (RemoteException ex) {
            Log.w(TAG, "Cannot get WindowManager");
        }
    }
}
