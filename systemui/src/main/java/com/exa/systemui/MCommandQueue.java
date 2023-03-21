package com.exa.systemui;

import android.app.ITransientNotificationCallback;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.view.AppearanceRegion;
import com.exa.baselib.utils.L;
import com.exa.systemui.common.MessageCallback;
import com.android.internal.os.SomeArgs;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * @Author lsh
 * @Date 2023/3/6 17:09
 * @Description
 */
public class MCommandQueue extends IStatusBar.Stub implements DisplayManager.DisplayListener {

    private final Object mLock = new Object();
    private final String TAG = MCommandQueue.class.getSimpleName();

    private static final int MSG_SHIFT = 16;
    private static final int MSG_ICON = 1 << MSG_SHIFT;
    private static final int MSG_DISABLE = 2 << MSG_SHIFT;
    private static final int MSG_EXPAND_NOTIFICATIONS = 3 << MSG_SHIFT;
    private static final int MSG_COLLAPSE_PANELS = 4 << MSG_SHIFT;
    private static final int MSG_EXPAND_SETTINGS = 5 << MSG_SHIFT;
    private static final int MSG_SYSTEM_BAR_APPEARANCE_CHANGED = 6 << MSG_SHIFT;
    private static final int MSG_DISPLAY_READY = 7 << MSG_SHIFT;
    private static final int MSG_SHOW_IME_BUTTON = 8 << MSG_SHIFT;
    private static final int MSG_TOGGLE_RECENT_APPS = 9 << MSG_SHIFT;
    private static final int MSG_PRELOAD_RECENT_APPS = 10 << MSG_SHIFT;
    private static final int MSG_CANCEL_PRELOAD_RECENT_APPS = 11 << MSG_SHIFT;
    private static final int MSG_SET_WINDOW_STATE = 12 << MSG_SHIFT;
    private static final int MSG_SHOW_RECENT_APPS = 13 << MSG_SHIFT;
    private static final int MSG_HIDE_RECENT_APPS = 14 << MSG_SHIFT;
    private static final int MSG_SHOW_SCREEN_PIN_REQUEST = 18 << MSG_SHIFT;
    private static final int MSG_APP_TRANSITION_PENDING = 19 << MSG_SHIFT;
    private static final int MSG_APP_TRANSITION_CANCELLED = 20 << MSG_SHIFT;
    private static final int MSG_APP_TRANSITION_STARTING = 21 << MSG_SHIFT;
    private static final int MSG_ASSIST_DISCLOSURE = 22 << MSG_SHIFT;
    private static final int MSG_START_ASSIST = 23 << MSG_SHIFT;
    private static final int MSG_CAMERA_LAUNCH_GESTURE = 24 << MSG_SHIFT;
    private static final int MSG_TOGGLE_KEYBOARD_SHORTCUTS = 25 << MSG_SHIFT;
    private static final int MSG_SHOW_PICTURE_IN_PICTURE_MENU = 26 << MSG_SHIFT;
    private static final int MSG_ADD_QS_TILE = 27 << MSG_SHIFT;
    private static final int MSG_REMOVE_QS_TILE = 28 << MSG_SHIFT;
    private static final int MSG_CLICK_QS_TILE = 29 << MSG_SHIFT;
    private static final int MSG_TOGGLE_APP_SPLIT_SCREEN = 30 << MSG_SHIFT;
    private static final int MSG_APP_TRANSITION_FINISHED = 31 << MSG_SHIFT;
    private static final int MSG_DISMISS_KEYBOARD_SHORTCUTS = 32 << MSG_SHIFT;
    private static final int MSG_HANDLE_SYSTEM_KEY = 33 << MSG_SHIFT;
    private static final int MSG_SHOW_GLOBAL_ACTIONS = 34 << MSG_SHIFT;
    private static final int MSG_TOGGLE_PANEL = 35 << MSG_SHIFT;
    private static final int MSG_SHOW_SHUTDOWN_UI = 36 << MSG_SHIFT;
    private static final int MSG_SET_TOP_APP_HIDES_STATUS_BAR = 37 << MSG_SHIFT;
    private static final int MSG_ROTATION_PROPOSAL = 38 << MSG_SHIFT;
    private static final int MSG_BIOMETRIC_SHOW = 39 << MSG_SHIFT;
    private static final int MSG_BIOMETRIC_AUTHENTICATED = 40 << MSG_SHIFT;
    private static final int MSG_BIOMETRIC_HELP = 41 << MSG_SHIFT;
    private static final int MSG_BIOMETRIC_ERROR = 42 << MSG_SHIFT;
    private static final int MSG_BIOMETRIC_HIDE = 43 << MSG_SHIFT;
    private static final int MSG_SHOW_CHARGING_ANIMATION = 44 << MSG_SHIFT;
    private static final int MSG_SHOW_PINNING_TOAST_ENTER_EXIT = 45 << MSG_SHIFT;
    private static final int MSG_SHOW_PINNING_TOAST_ESCAPE = 46 << MSG_SHIFT;
    private static final int MSG_RECENTS_ANIMATION_STATE_CHANGED = 47 << MSG_SHIFT;
    private static final int MSG_SHOW_TRANSIENT = 48 << MSG_SHIFT;
    private static final int MSG_ABORT_TRANSIENT = 49 << MSG_SHIFT;
    private static final int MSG_TOP_APP_WINDOW_CHANGED = 50 << MSG_SHIFT;
    private static final int MSG_SHOW_INATTENTIVE_SLEEP_WARNING = 51 << MSG_SHIFT;
    private static final int MSG_DISMISS_INATTENTIVE_SLEEP_WARNING = 52 << MSG_SHIFT;
    private static final int MSG_SHOW_TOAST = 53 << MSG_SHIFT;
    private static final int MSG_HIDE_TOAST = 54 << MSG_SHIFT;
    private static final int MSG_TRACING_STATE_CHANGED = 55 << MSG_SHIFT;
    private static final int MSG_SUPPRESS_AMBIENT_DISPLAY = 56 << MSG_SHIFT;

    private ArrayList<Callback> mCallbacks = new ArrayList<>();

    private Handler mHandler = new MH(Looper.getMainLooper());

    public MCommandQueue(Context context) {
        context.getSystemService(DisplayManager.class).registerDisplayListener(this, mHandler);
    }

    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    public void unRegisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    interface Callback extends MessageCallback {
    }

    @Override
    public void setIcon(String s, StatusBarIcon statusBarIcon) throws RemoteException {
        L.dd();
    }

    @Override
    public void removeIcon(String s) throws RemoteException {
        L.dd();
    }

    @Override
    public void disable(int i, int i1, int i2) throws RemoteException {
        L.dd();
    }

    @Override
    public void animateExpandNotificationsPanel() throws RemoteException {
        L.dd();
    }

    @Override
    public void animateExpandSettingsPanel(String s) throws RemoteException {
        L.dd();
    }

    @Override
    public void animateCollapsePanels() throws RemoteException {
        L.dd();
    }

    @Override
    public void togglePanel() throws RemoteException {
        L.dd();
    }

    @Override
    public void showWirelessChargingAnimation(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void topAppWindowChanged(int displayId, boolean isFullscreen, boolean isImmersive) throws RemoteException {
        synchronized (mLock) {
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = displayId;
            args.argi2 = isFullscreen ? 1 : 0;
            args.argi3 = isImmersive ? 1 : 0;
            mHandler.obtainMessage(MSG_TOP_APP_WINDOW_CHANGED, args).sendToTarget();
        }
        L.dd();
    }

    @Override
    public void setImeWindowStatus(int displayId, IBinder token, int vis, int backDisposition,
                                   boolean showImeSwitcher, boolean isMultiClientImeEnabled)
            throws RemoteException {
        L.dd();
    }

    @Override
    public void setWindowState(int displayId, int window, int state) throws RemoteException {
        L.dd(displayId);
    }

    @Override
    public void showRecentApps(boolean b) throws RemoteException {
        L.dd();
    }

    @Override
    public void hideRecentApps(boolean b, boolean b1) throws RemoteException {
        L.dd();
    }

    @Override
    public void toggleRecentApps() throws RemoteException {
        L.dd();
    }

    @Override
    public void toggleSplitScreen() throws RemoteException {
        L.dd();
    }

    @Override
    public void preloadRecentApps() throws RemoteException {
        L.dd();
    }

    @Override
    public void cancelPreloadRecentApps() throws RemoteException {
        L.dd();
    }

    @Override
    public void showScreenPinningRequest(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void dismissKeyboardShortcutsMenu() throws RemoteException {
        L.dd();
    }

    @Override
    public void toggleKeyboardShortcutsMenu(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void appTransitionPending(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void appTransitionCancelled(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void appTransitionStarting(int displayId, long l, long l1) throws RemoteException {
        L.dd();
    }

    @Override
    public void appTransitionFinished(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void showAssistDisclosure() throws RemoteException {
        L.dd();
    }

    @Override
    public void startAssist(Bundle bundle) throws RemoteException {
        L.dd();
    }

    @Override
    public void onCameraLaunchGestureDetected(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void showPictureInPictureMenu() throws RemoteException {
        L.dd();
    }

    @Override
    public void showGlobalActionsMenu() throws RemoteException {
        L.dd();
    }

    @Override
    public void onProposedRotationChanged(int i, boolean b) throws RemoteException {
        L.dd();
    }

    @Override
    public void setTopAppHidesStatusBar(boolean b) throws RemoteException {
//        L.dd();
    }

    @Override
    public void addQsTile(ComponentName componentName) throws RemoteException {
        L.dd();
    }

    @Override
    public void remQsTile(ComponentName componentName) throws RemoteException {
        L.dd();
    }

    @Override
    public void clickQsTile(ComponentName componentName) throws RemoteException {
        L.dd();
    }

    @Override
    public void handleSystemKey(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void showPinningEnterExitToast(boolean b) throws RemoteException {
        L.dd();
    }

    @Override
    public void showPinningEscapeToast() throws RemoteException {
        L.dd();
    }

    @Override
    public void showShutdownUi(boolean b, String s) throws RemoteException {
        L.dd();
    }

    @Override
    public void showAuthenticationDialog(Bundle bundle, IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal, int i, boolean b, int i1, String s, long l, int i2) throws RemoteException {
        L.dd();
    }

    @Override
    public void onBiometricAuthenticated() throws RemoteException {
        L.dd();
    }

    @Override
    public void onBiometricHelp(String s) throws RemoteException {
        L.dd();
    }

    @Override
    public void onBiometricError(int i, int i1, int i2) throws RemoteException {
        L.dd();
    }

    @Override
    public void hideAuthenticationDialog() throws RemoteException {
        L.dd();
    }

    @Override
    public void onDisplayReady(int i) throws RemoteException {
        L.dd();
    }

    @Override
    public void onRecentsAnimationStateChanged(boolean b) throws RemoteException {
        L.dd();
    }

    /**
     * @param displayId               the ID of the display to notify
     * @param appearance              @see android.view.WindowInsetsController.Appearance ,the appearance of the focused window.
     * @param appearanceRegions       a set of appearances which will be only applied in their own bounds.
     *                                This is for system bars which across multiple stack
     * @param navbarColorManagedByIme if navigation bar color is managed by IME
     * @throws RemoteException
     */
    @Override
    public void onSystemBarAppearanceChanged(int displayId, int appearance,
                                             AppearanceRegion[] appearanceRegions,
                                             boolean navbarColorManagedByIme) throws RemoteException {
        synchronized (mLock) {
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = displayId;
            args.argi2 = appearance;
            args.argi3 = navbarColorManagedByIme ? 1 : 0;
            args.arg1 = appearanceRegions;
            mHandler.obtainMessage(MSG_SYSTEM_BAR_APPEARANCE_CHANGED, args).sendToTarget();
        }
        L.dd();
    }

    @Override
    public void showTransient(int displayId, int[] types) throws RemoteException {//显示瞬态--临时显示systemui
        synchronized (mLock) {
            mHandler.obtainMessage(MSG_SHOW_TRANSIENT, displayId, 0, types).sendToTarget();
        }
        L.dd();
    }

    @Override
    public void abortTransient(int i, int[] ints) throws RemoteException {//终止瞬态
        L.dd();
    }

    @Override
    public void showInattentiveSleepWarning() throws RemoteException {
        L.dd();
    }

    @Override
    public void dismissInattentiveSleepWarning(boolean b) throws RemoteException {
        L.dd();
    }

    @Override
    public void showToast(int i, String s, IBinder iBinder, CharSequence charSequence, IBinder iBinder1, int i1, ITransientNotificationCallback iTransientNotificationCallback) throws RemoteException {
        L.dd();
    }

    @Override
    public void hideToast(String s, IBinder iBinder) throws RemoteException {
        L.dd();
    }

    @Override
    public void startTracing() throws RemoteException {
        L.dd();
    }

    @Override
    public void stopTracing() throws RemoteException {
        L.dd();
    }

    @Override
    public void suppressAmbientDisplay(boolean b) throws RemoteException {
        L.dd();
    }

    @Override
    public void onDisplayAdded(int displayId) {
        L.dd();
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        L.dd();
    }

    @Override
    public void onDisplayChanged(int displayId) {
        L.dd();
    }

    class MH extends Handler {

        public MH(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SomeArgs args;
            L.d(TAG, "handleMessage:" + msg.what);
            switch (msg.what) {
                case MSG_SHOW_TRANSIENT:
                    for (int i = 0; i < mCallbacks.size(); i++) {
                        mCallbacks.get(i).showTransient(msg.arg1, (int[]) msg.obj);
                    }
                    break;
                case MSG_SYSTEM_BAR_APPEARANCE_CHANGED: {
                    args = (SomeArgs) msg.obj;
                    for (int i = 0; i < mCallbacks.size(); i++) {
                        mCallbacks.get(i).onSystemBarAppearanceChanged(args.argi1, args.argi2,
                                (AppearanceRegion[]) args.arg1, args.argi3 == 1);
                    }
                    args.recycle();
                    break;
                }
                case MSG_TOP_APP_WINDOW_CHANGED: {
                    args = (SomeArgs) msg.obj;
                    for (int i = 0; i < mCallbacks.size(); i++) {
                        mCallbacks.get(i).topAppWindowChanged(
                                args.argi1, args.argi2 != 0, args.argi3 != 0);
                    }
                    args.recycle();
                    break;
                }
            }
        }
    }
}
