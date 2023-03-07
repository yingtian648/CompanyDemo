package com.exa.companydemo.systemui;

import android.app.ITransientNotificationCallback;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.view.AppearanceRegion;

/**
 * @Author lsh
 * @Date 2023/3/6 17:09
 * @Description
 */
public class MStatusBarImpl extends IStatusBar.Stub{

    public void init(){
        //StatusBarManagerService.updateUiVisibilityLocked
    }

    @Override
    public void setIcon(String s, StatusBarIcon statusBarIcon) throws RemoteException {

    }

    @Override
    public void removeIcon(String s) throws RemoteException {

    }

    @Override
    public void disable(int i, int i1, int i2) throws RemoteException {

    }

    @Override
    public void animateExpandNotificationsPanel() throws RemoteException {

    }

    @Override
    public void animateExpandSettingsPanel(String s) throws RemoteException {

    }

    @Override
    public void animateCollapsePanels() throws RemoteException {

    }

    @Override
    public void togglePanel() throws RemoteException {

    }

    @Override
    public void showWirelessChargingAnimation(int i) throws RemoteException {

    }

    @Override
    public void topAppWindowChanged(int i, boolean b, boolean b1) throws RemoteException {

    }

    @Override
    public void setImeWindowStatus(int i, IBinder iBinder, int i1, int i2, boolean b, boolean b1) throws RemoteException {

    }

    @Override
    public void setWindowState(int i, int i1, int i2) throws RemoteException {

    }

    @Override
    public void showRecentApps(boolean b) throws RemoteException {

    }

    @Override
    public void hideRecentApps(boolean b, boolean b1) throws RemoteException {

    }

    @Override
    public void toggleRecentApps() throws RemoteException {

    }

    @Override
    public void toggleSplitScreen() throws RemoteException {

    }

    @Override
    public void preloadRecentApps() throws RemoteException {

    }

    @Override
    public void cancelPreloadRecentApps() throws RemoteException {

    }

    @Override
    public void showScreenPinningRequest(int i) throws RemoteException {

    }

    @Override
    public void dismissKeyboardShortcutsMenu() throws RemoteException {

    }

    @Override
    public void toggleKeyboardShortcutsMenu(int i) throws RemoteException {

    }

    @Override
    public void appTransitionPending(int i) throws RemoteException {

    }

    @Override
    public void appTransitionCancelled(int i) throws RemoteException {

    }

    @Override
    public void appTransitionStarting(int i, long l, long l1) throws RemoteException {

    }

    @Override
    public void appTransitionFinished(int i) throws RemoteException {

    }

    @Override
    public void showAssistDisclosure() throws RemoteException {

    }

    @Override
    public void startAssist(Bundle bundle) throws RemoteException {

    }

    @Override
    public void onCameraLaunchGestureDetected(int i) throws RemoteException {

    }

    @Override
    public void showPictureInPictureMenu() throws RemoteException {

    }

    @Override
    public void showGlobalActionsMenu() throws RemoteException {

    }

    @Override
    public void onProposedRotationChanged(int i, boolean b) throws RemoteException {

    }

    @Override
    public void setTopAppHidesStatusBar(boolean b) throws RemoteException {

    }

    @Override
    public void addQsTile(ComponentName componentName) throws RemoteException {

    }

    @Override
    public void remQsTile(ComponentName componentName) throws RemoteException {

    }

    @Override
    public void clickQsTile(ComponentName componentName) throws RemoteException {

    }

    @Override
    public void handleSystemKey(int i) throws RemoteException {

    }

    @Override
    public void showPinningEnterExitToast(boolean b) throws RemoteException {

    }

    @Override
    public void showPinningEscapeToast() throws RemoteException {

    }

    @Override
    public void showShutdownUi(boolean b, String s) throws RemoteException {

    }

    @Override
    public void showAuthenticationDialog(Bundle bundle, IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal, int i, boolean b, int i1, String s, long l, int i2) throws RemoteException {

    }

    @Override
    public void onBiometricAuthenticated() throws RemoteException {

    }

    @Override
    public void onBiometricHelp(String s) throws RemoteException {

    }

    @Override
    public void onBiometricError(int i, int i1, int i2) throws RemoteException {

    }

    @Override
    public void hideAuthenticationDialog() throws RemoteException {

    }

    @Override
    public void onDisplayReady(int i) throws RemoteException {

    }

    @Override
    public void onRecentsAnimationStateChanged(boolean b) throws RemoteException {

    }

    @Override
    public void onSystemBarAppearanceChanged(int i, int i1, AppearanceRegion[] appearanceRegions, boolean b) throws RemoteException {

    }

    @Override
    public void showTransient(int i, int[] ints) throws RemoteException {

    }

    @Override
    public void abortTransient(int i, int[] ints) throws RemoteException {

    }

    @Override
    public void showInattentiveSleepWarning() throws RemoteException {

    }

    @Override
    public void dismissInattentiveSleepWarning(boolean b) throws RemoteException {

    }

    @Override
    public void showToast(int i, String s, IBinder iBinder, CharSequence charSequence, IBinder iBinder1, int i1, ITransientNotificationCallback iTransientNotificationCallback) throws RemoteException {

    }

    @Override
    public void hideToast(String s, IBinder iBinder) throws RemoteException {

    }

    @Override
    public void startTracing() throws RemoteException {

    }

    @Override
    public void stopTracing() throws RemoteException {

    }

    @Override
    public void suppressAmbientDisplay(boolean b) throws RemoteException {

    }
}
