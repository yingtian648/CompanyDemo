package com.exa.systemui.common;

/**
 * @Author lsh
 * @Date 2023/3/13 19:32
 * @Description
 */

import android.app.ITransientNotificationCallback;
import android.content.ComponentName;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.os.Bundle;
import android.os.IBinder;
import android.view.InsetsState;

import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.view.AppearanceRegion;

/**
 * These methods are called back on the main thread.
 */
public interface MessageCallback {
    default void setIcon(String slot, StatusBarIcon icon) {
    }

    default void removeIcon(String slot) {
    }

    /**
     * Called to notify that disable flags are updated.
     *
     * @param displayId The id of the display to notify.
     * @param state1    The combination of following DISABLE_* flags:
     * @param state2    The combination of following DISABLE2_* flags:
     * @param animate   {@code true} to show animations.
     * @see IStatusBar#disable(int, int, int).
     */
    default void disable(int displayId, int state1, int state2,
                         boolean animate) {
    }

    default void animateExpandNotificationsPanel() {
    }

    default void animateCollapsePanels(int flags, boolean force) {
    }

    default void togglePanel() {
    }

    default void animateExpandSettingsPanel(String obj) {
    }

    /**
     * Called to notify IME window status changes.
     *
     * @param displayId       The id of the display to notify.
     * @param token           IME token.
     * @param vis             IME visibility.
     * @param backDisposition BackDispositionMode Disposition mode of back button. It should be one of below flags:
     * @param showImeSwitcher {@code true} to show IME switch button.
     */
    default void setImeWindowStatus(int displayId, IBinder token, int vis,
                                    int backDisposition, boolean showImeSwitcher) {
    }

    default void showRecentApps(boolean triggeredFromAltTab) {
    }

    default void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
    }

    default void toggleRecentApps() {
    }

    default void toggleSplitScreen() {
    }

    default void preloadRecentApps() {
    }

    default void dismissKeyboardShortcutsMenu() {
    }

    default void toggleKeyboardShortcutsMenu(int deviceId) {
    }

    default void cancelPreloadRecentApps() {
    }

    /**
     * Called to notify window state changes.
     *
     * @param displayId The id of the display to notify.
     * @param window    Window type. It should be one of StatusBarManager.WINDOW_STATUS_BAR
     *                  or StatusBarManager#WINDOW_NAVIGATION_BAR
     * @param state     Window visible state.
     * @see IStatusBar#setWindowState(int, int, int)
     */
    default void setWindowState(int displayId, int window,
                                int state) {
    }

    default void showScreenPinningRequest(int taskId) {
    }

    /**
     * Called to notify System UI that an application transition is pending.
     *
     * @param displayId The id of the display to notify.
     * @param forced    {@code true} to force transition pending.
     * @see IStatusBar#appTransitionPending(int).
     */
    default void appTransitionPending(int displayId, boolean forced) {
    }

    /**
     * Called to notify System UI that an application transition is canceled.
     *
     * @param displayId The id of the display to notify.
     * @see IStatusBar#appTransitionCancelled(int).
     */
    default void appTransitionCancelled(int displayId) {
    }

    /**
     * Called to notify System UI that an application transition is starting.
     *
     * @param displayId The id of the display to notify.
     * @param startTime Transition start time.
     * @param duration  Transition duration.
     * @param forced    {@code true} to force transition pending.
     * @see IStatusBar#appTransitionStarting(int, long, long).
     */
    default void appTransitionStarting(
            int displayId, long startTime, long duration, boolean forced) {
    }

    /**
     * Called to notify System UI that an application transition is finished.
     *
     * @param displayId The id of the display to notify.
     * @see IStatusBar#appTransitionFinished(int)
     */
    default void appTransitionFinished(int displayId) {
    }

    default void showAssistDisclosure() {
    }

    default void startAssist(Bundle args) {
    }

    default void onCameraLaunchGestureDetected(int source) {
    }

    default void showPictureInPictureMenu() {
    }

    default void setTopAppHidesStatusBar(boolean topAppHidesStatusBar) {
    }

    default void addQsTile(ComponentName tile) {
    }

    default void remQsTile(ComponentName tile) {
    }

    default void clickTile(ComponentName tile) {
    }

    default void handleSystemKey(int arg1) {
    }

    default void showPinningEnterExitToast(boolean entering) {
    }

    default void showPinningEscapeToast() {
    }

    default void handleShowGlobalActionsMenu() {
    }

    default void handleShowShutdownUi(boolean isReboot, String reason) {
    }

    default void showWirelessChargingAnimation(int batteryLevel) {
    }

    default void onRotationProposal(int rotation, boolean isValid) {
    }

    default void showAuthenticationDialog(Bundle bundle,
                                          IBiometricServiceReceiverInternal receiver, int biometricModality,
                                          boolean requireConfirmation, int userId, String opPackageName,
                                          long operationId, int sysUiSessionId) {
    }

    default void onBiometricAuthenticated() {
    }

    default void onBiometricHelp(String message) {
    }

    default void onBiometricError(int modality, int error, int vendorCode) {
    }

    default void hideAuthenticationDialog() {
    }

    /**
     * @see IStatusBar#onDisplayReady(int)
     */
    default void onDisplayReady(int displayId) {
    }

    /**
     * DisplayManager.DisplayListener#onDisplayRemoved(int)
     */
    default void onDisplayRemoved(int displayId) {
    }

    /**
     * @see IStatusBar#onRecentsAnimationStateChanged(boolean)
     */
    default void onRecentsAnimationStateChanged(boolean running) {
    }

    /**
     * @see IStatusBar#onSystemBarAppearanceChanged(int, int, AppearanceRegion[], boolean).
     */
    default void onSystemBarAppearanceChanged(int displayId, int appearance,
                                              AppearanceRegion[] appearanceRegions, boolean navbarColorManagedByIme) {
    }

    /**
     * @see IStatusBar#showTransient(int, int[]).
     */
    default void showTransient(int displayId, @InsetsState.InternalInsetsType int[] types) {
    }

    /**
     * @see IStatusBar#abortTransient(int, int[]).
     */
    default void abortTransient(int displayId, @InsetsState.InternalInsetsType int[] types) {
    }

    /**
     * @see IStatusBar#topAppWindowChanged(int, boolean, boolean).
     */
    default void topAppWindowChanged(int displayId, boolean isFullscreen, boolean isImmersive) {
    }

    /**
     * Called to notify System UI that a warning about the device going to sleep
     * due to prolonged user inactivity should be shown.
     */
    default void showInattentiveSleepWarning() {
    }

    /**
     * Called to notify System UI that the warning about the device going to sleep
     * due to prolonged user inactivity should be dismissed.
     */
    default void dismissInattentiveSleepWarning(boolean animated) {
    }

    /**
     * Called to suppress ambient display.
     */
    default void suppressAmbientDisplay(boolean suppress) {
    }

    /**
     * @see IStatusBar#showToast(int, String, IBinder, CharSequence, IBinder, int,
     * ITransientNotificationCallback)
     */
    default void showToast(int uid, String packageName, IBinder token, CharSequence text,
                           IBinder windowToken, int duration,
                           ITransientNotificationCallback callback) {
    }

    /**
     * @see IStatusBar#hideToast(String, IBinder) (String, IBinder)
     */
    default void hideToast(String packageName, IBinder token) {
    }

    /**
     * @param enabled
     */
    default void onTracingStateChanged(boolean enabled) {
    }
}