//package com.exa.systemui;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.graphics.Rect;
//import android.hardware.biometrics.IBiometricServiceReceiverInternal;
//import android.hardware.display.DisplayManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.Message;
//import android.os.RemoteException;
//
//import com.android.internal.statusbar.IStatusBar;
//import com.android.internal.statusbar.StatusBarIcon;
//import com.exa.baselib.utils.L;
//import com.exa.systemui.common.MessageCallback;
//import com.android.internal.os.SomeArgs;
//
//import java.util.ArrayList;
//
//import androidx.annotation.NonNull;
//
///**
// * @Author lsh
// * @Date 2023/3/6 17:09
// * @Description 适配android 10
// */
//public class MCommandQueueQ extends IStatusBar.Stub implements DisplayManager.DisplayListener {
//
//    private final Object mLock = new Object();
//    private final String TAG = "MCommandQueue";
//
//    private static final int MSG_SHIFT = 16;
//    private static final int MSG_ICON = 1 << MSG_SHIFT;
//    private static final int MSG_DISABLE = 2 << MSG_SHIFT;
//    private static final int MSG_EXPAND_NOTIFICATIONS = 3 << MSG_SHIFT;
//    private static final int MSG_COLLAPSE_PANELS = 4 << MSG_SHIFT;
//    private static final int MSG_EXPAND_SETTINGS = 5 << MSG_SHIFT;
//    private static final int MSG_SET_SYSTEMUI_VISIBILITY = 6 << MSG_SHIFT;
//    private static final int MSG_DISPLAY_READY = 7 << MSG_SHIFT;
//    private static final int MSG_SHOW_IME_BUTTON = 8 << MSG_SHIFT;
//    private static final int MSG_TOGGLE_RECENT_APPS = 9 << MSG_SHIFT;
//    private static final int MSG_PRELOAD_RECENT_APPS = 10 << MSG_SHIFT;
//    private static final int MSG_CANCEL_PRELOAD_RECENT_APPS = 11 << MSG_SHIFT;
//    private static final int MSG_SET_WINDOW_STATE = 12 << MSG_SHIFT;
//    private static final int MSG_SHOW_RECENT_APPS = 13 << MSG_SHIFT;
//    private static final int MSG_HIDE_RECENT_APPS = 14 << MSG_SHIFT;
//    private static final int MSG_SHOW_SCREEN_PIN_REQUEST = 18 << MSG_SHIFT;
//    private static final int MSG_APP_TRANSITION_PENDING = 19 << MSG_SHIFT;
//    private static final int MSG_APP_TRANSITION_CANCELLED = 20 << MSG_SHIFT;
//    private static final int MSG_APP_TRANSITION_STARTING = 21 << MSG_SHIFT;
//    private static final int MSG_ASSIST_DISCLOSURE = 22 << MSG_SHIFT;
//    private static final int MSG_START_ASSIST = 23 << MSG_SHIFT;
//    private static final int MSG_CAMERA_LAUNCH_GESTURE = 24 << MSG_SHIFT;
//    private static final int MSG_TOGGLE_KEYBOARD_SHORTCUTS = 25 << MSG_SHIFT;
//    private static final int MSG_SHOW_PICTURE_IN_PICTURE_MENU = 26 << MSG_SHIFT;
//    private static final int MSG_ADD_QS_TILE = 27 << MSG_SHIFT;
//    private static final int MSG_REMOVE_QS_TILE = 28 << MSG_SHIFT;
//    private static final int MSG_CLICK_QS_TILE = 29 << MSG_SHIFT;
//    private static final int MSG_TOGGLE_APP_SPLIT_SCREEN = 30 << MSG_SHIFT;
//    private static final int MSG_APP_TRANSITION_FINISHED = 31 << MSG_SHIFT;
//    private static final int MSG_DISMISS_KEYBOARD_SHORTCUTS = 32 << MSG_SHIFT;
//    private static final int MSG_HANDLE_SYSTEM_KEY = 33 << MSG_SHIFT;
//    private static final int MSG_SHOW_GLOBAL_ACTIONS = 34 << MSG_SHIFT;
//    private static final int MSG_TOGGLE_PANEL = 35 << MSG_SHIFT;
//    private static final int MSG_SHOW_SHUTDOWN_UI = 36 << MSG_SHIFT;
//    private static final int MSG_SET_TOP_APP_HIDES_STATUS_BAR = 37 << MSG_SHIFT;
//    private static final int MSG_ROTATION_PROPOSAL = 38 << MSG_SHIFT;
//    private static final int MSG_BIOMETRIC_SHOW = 39 << MSG_SHIFT;
//    private static final int MSG_BIOMETRIC_AUTHENTICATED = 40 << MSG_SHIFT;
//    private static final int MSG_BIOMETRIC_HELP = 41 << MSG_SHIFT;
//    private static final int MSG_BIOMETRIC_ERROR = 42 << MSG_SHIFT;
//    private static final int MSG_BIOMETRIC_HIDE = 43 << MSG_SHIFT;
//    private static final int MSG_SHOW_CHARGING_ANIMATION = 44 << MSG_SHIFT;
//    private static final int MSG_SHOW_PINNING_TOAST_ENTER_EXIT = 45 << MSG_SHIFT;
//    private static final int MSG_SHOW_PINNING_TOAST_ESCAPE = 46 << MSG_SHIFT;
//    private static final int MSG_RECENTS_ANIMATION_STATE_CHANGED = 47 << MSG_SHIFT;
//
//    public static final int FLAG_EXCLUDE_NONE = 0;
//    public static final int FLAG_EXCLUDE_SEARCH_PANEL = 1 << 0;
//    public static final int FLAG_EXCLUDE_RECENTS_PANEL = 1 << 1;
//    public static final int FLAG_EXCLUDE_NOTIFICATION_PANEL = 1 << 2;
//    public static final int FLAG_EXCLUDE_INPUT_METHODS_PANEL = 1 << 3;
//    public static final int FLAG_EXCLUDE_COMPAT_MODE_PANEL = 1 << 4;
//
//    private ArrayList<Callback> mCallbacks = new ArrayList<>();
//
//    private Handler mHandler = new MH(Looper.getMainLooper());
//
//    public MCommandQueueQ(Context context) {
//        context.getSystemService(DisplayManager.class).registerDisplayListener(this, mHandler);
//    }
//
//    public void registerCallback(Callback callback) {
//        mCallbacks.add(callback);
//    }
//
//    public void unRegisterCallback(Callback callback) {
//        mCallbacks.remove(callback);
//    }
//
//    interface Callback extends MessageCallback {
//    }
//
//    @Override
//    public void setIcon(String s, StatusBarIcon statusBarIcon) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void removeIcon(String s) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void disable(int i, int i1, int i2) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void animateExpandNotificationsPanel() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void animateExpandSettingsPanel(String s) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void animateCollapsePanels() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void togglePanel() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showWirelessChargingAnimation(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis,
//                                      int dockedStackVis, int mask, Rect fullscreenStackBounds,
//                                      Rect dockedStackBounds, boolean navbarColorManagedByIme)
//            throws RemoteException {
//        L.dd();
//        synchronized (mLock) {
//            // Don't coalesce these, since it might have one time flags set such as
//            // STATUS_BAR_UNHIDE which might get lost.
//            SomeArgs args = SomeArgs.obtain();
//            args.argi1 = displayId;
//            args.argi2 = vis;
//            args.argi3 = fullscreenStackVis;
//            args.argi4 = dockedStackVis;
//            args.argi5 = mask;
//            args.argi6 = navbarColorManagedByIme ? 1 : 0;
//            args.arg1 = fullscreenStackBounds;
//            args.arg2 = dockedStackBounds;
//            mHandler.obtainMessage(MSG_SET_SYSTEMUI_VISIBILITY, args).sendToTarget();
//        }
//    }
//
//    @Override
//    public void topAppWindowChanged(int i, boolean b) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void setImeWindowStatus(int displayId, IBinder token, int vis, int backDisposition,
//                                   boolean showImeSwitcher) throws RemoteException {
//        L.dd();
//        synchronized (mLock) {
//            mHandler.removeMessages(MSG_SHOW_IME_BUTTON);
//            SomeArgs args = SomeArgs.obtain();
//            args.argi1 = displayId;
//            args.argi2 = vis;
//            args.argi3 = backDisposition;
//            args.argi4 = showImeSwitcher ? 1 : 0;
//            args.arg1 = token;
//            Message m = mHandler.obtainMessage(MSG_SHOW_IME_BUTTON, args);
//            m.sendToTarget();
//        }
//    }
//
//    /**
//     * Called to notify window state changes.
//     * @see IStatusBar#setWindowState(int, int, int)
//     *
//     * @param displayId The id of the display to notify.
//     * @param windowType Window type. It should be one of {StatusBarManager#WINDOW_STATUS_BAR=1
//     *                   WINDOW_NAVIGATION_BAR=2}
//     * @param state Window visible state.
//     *              { WINDOW_STATE_SHOWING = 0 WINDOW_STATE_HIDING = 1 WINDOW_STATE_HIDDEN = 2}
//     */
//    @Override
//    public void setWindowState(int displayId, int windowType, int state) throws RemoteException {
//        L.dd(toWindowType(windowType) + " " + toStateString(state));
//    }
//
//    private String toWindowType(int windowType){
//        switch (windowType) {
//            case 1:return "WINDOW_STATUS_BAR";
//            case 2:return "WINDOW_NAVIGATION_BAR";
//        }
//        return "WINDOW_UNKNOWN";
//    }
//
//    private String toStateString(int state){
//        switch (state) {
//            case 0:return "SHOWING";
//            case 1:return "HIDING";
//            case 2:return "HIDDEN";
//        }
//        return "UNKNOWN";
//    }
//
//    @Override
//    public void showRecentApps(boolean b) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void hideRecentApps(boolean b, boolean b1) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void toggleRecentApps() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void toggleSplitScreen() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void preloadRecentApps() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void cancelPreloadRecentApps() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showScreenPinningRequest(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void dismissKeyboardShortcutsMenu() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void toggleKeyboardShortcutsMenu(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void appTransitionPending(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void appTransitionCancelled(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void appTransitionStarting(int displayId, long l, long l1) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void appTransitionFinished(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showAssistDisclosure() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void startAssist(Bundle bundle) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onCameraLaunchGestureDetected(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showPictureInPictureMenu() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showGlobalActionsMenu() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onProposedRotationChanged(int i, boolean b) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void setTopAppHidesStatusBar(boolean b) throws RemoteException {
////        L.dd();
//    }
//
//    @Override
//    public void addQsTile(ComponentName componentName) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void remQsTile(ComponentName componentName) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void clickQsTile(ComponentName componentName) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void handleSystemKey(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showPinningEnterExitToast(boolean b) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showPinningEscapeToast() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showShutdownUi(boolean b, String s) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void showBiometricDialog(Bundle bundle, IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal, int i, boolean b, int i1) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onBiometricAuthenticated(boolean b, String s) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onBiometricHelp(String s) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onBiometricError(String s) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void hideBiometricDialog() throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onDisplayReady(int i) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onRecentsAnimationStateChanged(boolean b) throws RemoteException {
//        L.dd();
//    }
//
//    @Override
//    public void onDisplayAdded(int displayId) {
//        L.dd();
//    }
//
//    @Override
//    public void onDisplayRemoved(int displayId) {
//        L.dd();
//    }
//
//    @Override
//    public void onDisplayChanged(int displayId) {
//        L.dd();
//    }
//
//    class MH extends Handler {
//
//        public MH(@NonNull Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            SomeArgs args;
//            switch (msg.what) {
//                case MSG_DISABLE:
//                    args = (SomeArgs) msg.obj;
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).disable(args.argi1, args.argi2, args.argi3,
//                                args.argi4 != 0 /* animate */);
//                    }
//                    break;
//                case MSG_EXPAND_NOTIFICATIONS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).animateExpandNotificationsPanel();
//                    }
//                    break;
//                case MSG_COLLAPSE_PANELS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).animateCollapsePanels(msg.arg1, msg.arg2 != 0);
//                    }
//                    break;
//                case MSG_TOGGLE_PANEL:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).togglePanel();
//                    }
//                    break;
//                case MSG_EXPAND_SETTINGS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).animateExpandSettingsPanel((String) msg.obj);
//                    }
//                    break;
//                case MSG_SET_SYSTEMUI_VISIBILITY:
//                    args = (SomeArgs) msg.obj;
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).setSystemUiVisibility(args.argi1, args.argi2, args.argi3,
//                                args.argi4, args.argi5, (Rect) args.arg1, (Rect) args.arg2,
//                                args.argi6 == 1);
//                    }
//                    args.recycle();
//                    break;
//                case MSG_SHOW_RECENT_APPS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showRecentApps(msg.arg1 != 0);
//                    }
//                    break;
//                case MSG_HIDE_RECENT_APPS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).hideRecentApps(msg.arg1 != 0, msg.arg2 != 0);
//                    }
//                    break;
//                case MSG_TOGGLE_RECENT_APPS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).toggleRecentApps();
//                    }
//                    break;
//                case MSG_PRELOAD_RECENT_APPS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).preloadRecentApps();
//                    }
//                    break;
//                case MSG_CANCEL_PRELOAD_RECENT_APPS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).cancelPreloadRecentApps();
//                    }
//                    break;
//                case MSG_DISMISS_KEYBOARD_SHORTCUTS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).dismissKeyboardShortcutsMenu();
//                    }
//                    break;
//                case MSG_TOGGLE_KEYBOARD_SHORTCUTS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).toggleKeyboardShortcutsMenu(msg.arg1);
//                    }
//                    break;
//                case MSG_SET_WINDOW_STATE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).setWindowState(msg.arg1, msg.arg2, (int) msg.obj);
//                    }
//                    break;
//                case MSG_SHOW_SCREEN_PIN_REQUEST:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showScreenPinningRequest(msg.arg1);
//                    }
//                    break;
//                case MSG_APP_TRANSITION_PENDING:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).appTransitionPending(msg.arg1, msg.arg2 != 0);
//                    }
//                    break;
//                case MSG_APP_TRANSITION_CANCELLED:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).appTransitionCancelled(msg.arg1);
//                    }
//                    break;
//                case MSG_APP_TRANSITION_STARTING:
//                    args = (SomeArgs) msg.obj;
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).appTransitionStarting(args.argi1, (long) args.arg1,
//                                (long) args.arg2, args.argi2 != 0 /* forced */);
//                    }
//                    break;
//                case MSG_APP_TRANSITION_FINISHED:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).appTransitionFinished(msg.arg1);
//                    }
//                    break;
//                case MSG_ASSIST_DISCLOSURE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showAssistDisclosure();
//                    }
//                    break;
//                case MSG_START_ASSIST:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).startAssist((Bundle) msg.obj);
//                    }
//                    break;
//                case MSG_CAMERA_LAUNCH_GESTURE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).onCameraLaunchGestureDetected(msg.arg1);
//                    }
//                    break;
//                case MSG_SHOW_PICTURE_IN_PICTURE_MENU:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showPictureInPictureMenu();
//                    }
//                    break;
//                case MSG_ADD_QS_TILE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).addQsTile((ComponentName) msg.obj);
//                    }
//                    break;
//                case MSG_REMOVE_QS_TILE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).remQsTile((ComponentName) msg.obj);
//                    }
//                    break;
//                case MSG_CLICK_QS_TILE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).clickTile((ComponentName) msg.obj);
//                    }
//                    break;
//                case MSG_TOGGLE_APP_SPLIT_SCREEN:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).toggleSplitScreen();
//                    }
//                    break;
//                case MSG_HANDLE_SYSTEM_KEY:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).handleSystemKey(msg.arg1);
//                    }
//                    break;
//                case MSG_SHOW_GLOBAL_ACTIONS:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).handleShowGlobalActionsMenu();
//                    }
//                    break;
//                case MSG_SHOW_SHUTDOWN_UI:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).handleShowShutdownUi(msg.arg1 != 0, (String) msg.obj);
//                    }
//                    break;
//                case MSG_SET_TOP_APP_HIDES_STATUS_BAR:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).setTopAppHidesStatusBar(msg.arg1 != 0);
//                    }
//                    break;
//                case MSG_ROTATION_PROPOSAL:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).onRotationProposal(msg.arg1, msg.arg2 != 0);
//                    }
//                    break;
//                case MSG_BIOMETRIC_SHOW: {
//                    mHandler.removeMessages(MSG_BIOMETRIC_ERROR);
//                    mHandler.removeMessages(MSG_BIOMETRIC_HELP);
//                    mHandler.removeMessages(MSG_BIOMETRIC_AUTHENTICATED);
//                    SomeArgs someArgs = (SomeArgs) msg.obj;
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showBiometricDialog(
//                                (Bundle) someArgs.arg1,
//                                (IBiometricServiceReceiverInternal) someArgs.arg2,
//                                someArgs.argi1 /* type */,
//                                (boolean) someArgs.arg3 /* requireConfirmation */,
//                                someArgs.argi2 /* userId */);
//                    }
//                    someArgs.recycle();
//                    break;
//                }
//                case MSG_BIOMETRIC_AUTHENTICATED: {
//                    SomeArgs someArgs = (SomeArgs) msg.obj;
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).onBiometricAuthenticated(
//                                (boolean) someArgs.arg1 /* authenticated */,
//                                (String) someArgs.arg2 /* failureReason */);
//                    }
//                    someArgs.recycle();
//                    break;
//                }
//                case MSG_BIOMETRIC_HELP:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).onBiometricHelp((String) msg.obj);
//                    }
//                    break;
//                case MSG_BIOMETRIC_ERROR:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).onBiometricError((String) msg.obj);
//                    }
//                    break;
//                case MSG_BIOMETRIC_HIDE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).hideBiometricDialog();
//                    }
//                    break;
//                case MSG_SHOW_CHARGING_ANIMATION:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showWirelessChargingAnimation(msg.arg1);
//                    }
//                    break;
//                case MSG_SHOW_PINNING_TOAST_ENTER_EXIT:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showPinningEnterExitToast((Boolean) msg.obj);
//                    }
//                    break;
//                case MSG_SHOW_PINNING_TOAST_ESCAPE:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).showPinningEscapeToast();
//                    }
//                    break;
//                case MSG_DISPLAY_READY:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).onDisplayReady(msg.arg1);
//                    }
//                    break;
//                case MSG_RECENTS_ANIMATION_STATE_CHANGED:
//                    for (int i = 0; i < mCallbacks.size(); i++) {
//                        mCallbacks.get(i).onRecentsAnimationStateChanged(msg.arg1 > 0);
//                    }
//                    break;
//            }
//        }
//    }
//}
