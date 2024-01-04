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
import android.os.IBinder;
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
import com.android.internal.statusbar.StatusBarIcon;
import com.exa.baselib.utils.L;
import com.exa.systemui.common.BarTransitions;
import com.exa.systemui.minterface.IConfigChangedListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import static com.exa.systemui.common.BarTransitions.MODE_LIGHTS_OUT;
import static com.exa.systemui.common.BarTransitions.MODE_LIGHTS_OUT_TRANSPARENT;
import static com.exa.systemui.common.BarTransitions.MODE_OPAQUE;
import static com.exa.systemui.common.BarTransitions.MODE_SEMI_TRANSPARENT;
import static com.exa.systemui.common.BarTransitions.MODE_TRANSLUCENT;
import static com.exa.systemui.common.BarTransitions.MODE_TRANSPARENT;

public class SystemUiMain implements MCommandQueue.Callbacks, IConfigChangedListener {

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
    private boolean mAutohideSuspended;

    public static final int WINDOW_STATE_SHOWING = 0;
    public static final int WINDOW_STATE_HIDING = 1;
    public static final int WINDOW_STATE_HIDDEN = 2;

    public static final int STATUS_BAR_TRANSIENT = 0x04000000;
    public static final int NAVIGATION_BAR_TRANSIENT = 0x08000000;
    public static final int STATUS_BAR_TRANSLUCENT = 0x40000000;
    public static final int NAVIGATION_BAR_TRANSLUCENT = 0x80000000;
    public static final int STATUS_BAR_UNHIDE = 0x10000000;
    public static final int NAVIGATION_BAR_UNHIDE = 0x20000000;
    public static final int STATUS_BAR_TRANSPARENT = 0x00000008;
    public static final int NAVIGATION_BAR_TRANSPARENT = 0x00008000;

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
    private int mStatusBarMode;
    private int mLastDispatchedSystemUiVisibility = ~View.SYSTEM_UI_FLAG_VISIBLE;

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
        mCommandQueue = new MCommandQueue();
        mCommandQueue.addCallbacks(this);
        mBarService = IStatusBarService.Stub.asInterface(
                ServiceManager.getService("statusbar"));//Context.STATUS_BAR_SERVICE
        /**
         * 1.注册回调
         * 2.result可用于设置SystemUi的ime-mode
         */
        try {
            Rect dockedStackBounds = new Rect();
            Rect fullscreenStackBounds = new Rect();
            ArrayList<String> iconSlots = new ArrayList<>();
            ArrayList<StatusBarIcon> icons = new ArrayList<>();
            int[] switches = new int[9];
            ArrayList<IBinder> binders = new ArrayList<>();

            mBarService.registerStatusBar(mCommandQueue, iconSlots, icons, switches, binders, dockedStackBounds, fullscreenStackBounds);
            L.d("register mCommandQueue success");
        } catch (RemoteException ex) {
            ex.printStackTrace();
            L.e("registerStatusBar err", ex);
        }
    }

    private void registerCallback() {
        try {
            if (!mWindowManagerService.hasNavigationBar()) {
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

        mStatusbar = new StatusBarView(mContext, mUiModeManager, mDisplayId);
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

        mNavibar = new NavigationBarView(mContext, mUiModeManager, mDisplayId);
        mWindowManager.addView(mNavibar.getRootView(), mNavigationBarParams);

        WallpaperManager wallpaperManager = mContext.getSystemService(WallpaperManager.class);
        wallpaperManager.addOnColorsChangedListener((WallpaperManager.OnColorsChangedListener) (colors, which) -> {
            L.dd("colors = " + colors + ", witch = " + which);
        }, null);
    }

    @Override
    public void setSystemUiVisibility(int vis, int fullscreenStackVis, int dockedStackVis,
                                      int mask, Rect fullscreenStackBounds,
                                      Rect dockedStackBounds) {
        final int oldVal = mSystemUiVisibility;
        final int newVal = (oldVal & ~mask) | (vis & mask);
        L.dd("oldVal=" + oldVal + ", newVal=" + newVal);
        final int diff = newVal ^ oldVal;
        boolean nbModeChanged = false;
        boolean sbModeChanged = false;
        if (diff != 0) {
            mSystemUiVisibility = newVal;
            // update low profile
            if ((diff & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                L.w("setAreThereNotifications");
            }

            // ready to unhide
            if ((vis & STATUS_BAR_UNHIDE) != 0) {
                mSystemUiVisibility &= ~STATUS_BAR_UNHIDE;
            }
            // update status bar mode
            final int sbMode = computeStatusBarMode(oldVal, newVal);
            final int nbMode = computeNavigationBarMode(oldVal, newVal);

            sbModeChanged = sbMode != -1;
            nbModeChanged = nbMode != -1;
            if ((nbModeChanged || sbModeChanged) && sbMode != mStatusBarMode) {
                mStatusBarMode = sbMode;
                checkBarModes();
                touchAutoHide();
            }

            if ((vis & NAVIGATION_BAR_UNHIDE) != 0) {
                mSystemUiVisibility &= ~NAVIGATION_BAR_UNHIDE;
            }

            // send updated sysui visibility to window manager
            notifyUiVisibilityChanged(mSystemUiVisibility);
        }
    }

    protected int computeStatusBarMode(int oldVal, int newVal) {
        return computeBarMode(oldVal, newVal, STATUS_BAR_TRANSIENT,
                STATUS_BAR_TRANSLUCENT, STATUS_BAR_TRANSPARENT);
    }

    protected int computeNavigationBarMode(int oldVal, int newVal) {
        return computeBarMode(oldVal, newVal, NAVIGATION_BAR_TRANSIENT,
                NAVIGATION_BAR_TRANSLUCENT, NAVIGATION_BAR_TRANSPARENT);
    }

    protected int computeBarMode(int oldVis, int newVis,
                                 int transientFlag, int translucentFlag, int transparentFlag) {
        final int oldMode = barMode(oldVis, transientFlag, translucentFlag, transparentFlag);
        final int newMode = barMode(newVis, transientFlag, translucentFlag, transparentFlag);
        if (oldMode == newMode) {
            return -1; // no mode change
        }
        return newMode;
    }

    private int barMode(int vis, int transientFlag, int translucentFlag, int transparentFlag) {
        int lightsOutTransparent = View.SYSTEM_UI_FLAG_LOW_PROFILE | transparentFlag;
        return (vis & transientFlag) != 0 ? MODE_SEMI_TRANSPARENT
                : (vis & translucentFlag) != 0 ? MODE_TRANSLUCENT
                : (vis & lightsOutTransparent) == lightsOutTransparent ? MODE_LIGHTS_OUT_TRANSPARENT
                : (vis & transparentFlag) != 0 ? MODE_TRANSPARENT
                : (vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0 ? MODE_LIGHTS_OUT
                : MODE_OPAQUE;
    }

    void checkBarModes() {
//        checkBarMode(mStatusBarMode, mStatusBarWindowState, getStatusBarTransitions());
//        mNavigationBar.checkNavBarModes();
    }

    void checkBarMode(int mode, int windowState, BarTransitions transitions) {
//        final boolean anim = !mNoAnimationOnNextBarModeChange && mDeviceInteractive
//                && windowState != WINDOW_STATE_HIDDEN;
//        transitions.transitionTo(mode, anim);
    }

    void touchAutoHide() {
        L.dd();
        // update transient bar autohide
        if (mStatusBarMode == MODE_SEMI_TRANSPARENT) {
            scheduleAutohide();
        } else {
            cancelAutohide();
        }
    }

    private void cancelAutohide() {
        mAutohideSuspended = false;
        mHandler.removeCallbacks(mAutoHide);
    }

    private void scheduleAutohide() {
        cancelAutohide();
        mHandler.postDelayed(mAutoHide, AUTOHIDE_TIMEOUT_MS);
    }

    private static final int STATUS_OR_NAV_TRANSIENT =
            STATUS_BAR_TRANSIENT | NAVIGATION_BAR_TRANSIENT;
    private static final long AUTOHIDE_TIMEOUT_MS = 2250;

    private final Runnable mAutoHide = () -> {
        int requested = mSystemUiVisibility & ~STATUS_OR_NAV_TRANSIENT;
        if (mSystemUiVisibility != requested) {
            notifyUiVisibilityChanged(requested);
        }
    };

    private void notifyUiVisibilityChanged(int vis) {
        try {
            if (mLastDispatchedSystemUiVisibility != vis) {
                L.dd(vis);
                mWindowManagerService.statusBarVisibilityChanged(vis);
                mLastDispatchedSystemUiVisibility = vis;
            }
        } catch (RemoteException ex) {
        }
    }
}
