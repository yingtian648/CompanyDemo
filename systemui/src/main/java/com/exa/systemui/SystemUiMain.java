package com.exa.systemui;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PatternMatcher;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.exa.baselib.utils.L;
import com.exa.systemui.minterface.IConfigChangedListener;

import androidx.annotation.NonNull;

import static com.exa.systemui.common.BarTransitions.MODE_LIGHTS_OUT;
import static com.exa.systemui.common.BarTransitions.MODE_LIGHTS_OUT_TRANSPARENT;
import static com.exa.systemui.common.BarTransitions.MODE_OPAQUE;
import static com.exa.systemui.common.BarTransitions.MODE_SEMI_TRANSPARENT;
import static com.exa.systemui.common.BarTransitions.MODE_TRANSLUCENT;
import static com.exa.systemui.common.BarTransitions.MODE_TRANSPARENT;

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
    private int mSystemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
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

    @Override
    public void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis,
                                      int dockedStackVis, int mask, Rect fullscreenStackBounds,
                                      Rect dockedStackBounds, boolean navbarColorManagedByIme) {
        final int oldVal = mSystemUiVisibility;
        final int newVal = (oldVal & ~mask) | (vis & mask);
        L.dd("oldVal=" + oldVal + ", newVal=" + newVal);
        final int diff = newVal ^ oldVal;
        boolean nbModeChanged = false;
        if (diff != 0) {
            mSystemUiVisibility = newVal;

        }
    }

    private int computeBarMode(int oldVis, int newVis) {
        final int oldMode = barMode(oldVis);
        final int newMode = barMode(newVis);
        if (oldMode == newMode) {
            return -1; // no mode change
        }
        return newMode;
    }

    private int barMode(int vis) {
        final int NAVIGATION_BAR_TRANSPARENT = 0x00008000;
        final int NAVIGATION_BAR_TRANSLUCENT = 0x80000000;
        final int NAVIGATION_BAR_TRANSIENT = 0x08000000;
        final int lightsOutTransparent =
                View.SYSTEM_UI_FLAG_LOW_PROFILE | NAVIGATION_BAR_TRANSIENT;
        if ((vis & NAVIGATION_BAR_TRANSIENT) != 0) {
            return MODE_SEMI_TRANSPARENT;
        } else if ((vis & NAVIGATION_BAR_TRANSLUCENT) != 0) {
            return MODE_TRANSLUCENT;
        } else if ((vis & lightsOutTransparent) == lightsOutTransparent) {
            return MODE_LIGHTS_OUT_TRANSPARENT;
        } else if ((vis & NAVIGATION_BAR_TRANSPARENT) != 0) {
            return MODE_TRANSPARENT;
        } else if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            return MODE_LIGHTS_OUT;
        } else {
            return MODE_OPAQUE;
        }
    }
}
