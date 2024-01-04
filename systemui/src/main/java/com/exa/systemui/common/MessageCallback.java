package com.exa.systemui.common;

/**
 * @Author lsh
 * @Date 2023/3/13 19:32
 * @Description
 */

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;

import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;

/**
 * These methods are called back on the main thread.
 */
public interface MessageCallback {
    /**
     * These methods are called back on the main thread.
     */
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
     * IStatusBar#disable(int, int, int).
     */
    default void disable(int displayId, int state1,int state2,
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
     * Called to notify visibility flag changes.
     *
     * @param displayId               The id of the display to notify.
     * @param vis                     The visibility flags except SYSTEM_UI_FLAG_LIGHT_STATUS_BAR which will
     *                                be reported separately in fullscreenStackVis and dockedStackVis.
     * @param fullscreenStackVis      The flags which only apply in the region of the fullscreen
     *                                stack, which is currently only SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.
     * @param dockedStackVis          The flags that only apply in the region of the docked stack, which
     *                                is currently only SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.
     * @param mask                    Which flags to change.
     * @param fullscreenStackBounds   The current bounds of the fullscreen stack, in screen
     *                                coordinates.
     * @param dockedStackBounds       The current bounds of the docked stack, in screen coordinates.
     * @param navbarColorManagedByIme {@code true} if navigation bar color is managed by IME.
     * IStatusBar#setSystemUiVisibility(int, int, int, int, int, Rect, Rect).
     */
    default void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis,
                                       int dockedStackVis, int mask, Rect fullscreenStackBounds,
                                       Rect dockedStackBounds, boolean navbarColorManagedByIme) {
    }

    /**
     * Called to notify IME window status changes.
     *
     * @param displayId       The id of the display to notify.
     * @param token           IME token.
     * @param vis             IME visibility.
     * @param backDisposition Disposition mode of back button. It should be one of below flags:
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
     * @param windowType    Window type. It should be one of {StatusBarManager#WINDOW_STATUS_BAR}
     *                  or {StatusBarManager#WINDOW_NAVIGATION_BAR}
     * @param state     Window visible state.
     * IStatusBar#setWindowState(int, int, int)
     */
    default void setWindowState(int displayId, int windowType,
                                /** WindowVisibleState */ int state) {
    }

    default void showScreenPinningRequest(int taskId) {
    }

    /**
     * Called to notify System UI that an application transition is pending.
     *
     * @param displayId The id of the display to notify.
     * @param forced    {@code true} to force transition pending.
     * IStatusBar#appTransitionPending(int).
     */
    default void appTransitionPending(int displayId, boolean forced) {
    }

    /**
     * Called to notify System UI that an application transition is canceled.
     *
     * @param displayId The id of the display to notify.
     * IStatusBar#appTransitionCancelled(int).
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
     * IStatusBar#appTransitionStarting(int, long, long).
     */
    default void appTransitionStarting(
            int displayId, long startTime, long duration, boolean forced) {
    }

    /**
     * Called to notify System UI that an application transition is finished.
     *
     * @param displayId The id of the display to notify.
     * IStatusBar#appTransitionFinished(int)
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

    default void onBiometricAuthenticated(boolean authenticated, String failureReason) {
    }

    default void onBiometricHelp(String message) {
    }

    default void onBiometricError(String error) {
    }

    default void hideBiometricDialog() {
    }

    /**
     * IStatusBar#onDisplayReady(int)
     */
    default void onDisplayReady(int displayId) {
    }

    /**
     * DisplayManager.DisplayListener onDisplayRemoved(int)
     */
    default void onDisplayRemoved(int displayId) {
    }

    /**
     * IStatusBar#onRecentsAnimationStateChanged(boolean)
     */
    default void onRecentsAnimationStateChanged(boolean running) {
    }

}