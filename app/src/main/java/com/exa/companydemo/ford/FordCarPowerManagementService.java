//package com.exa.companydemo.ford;
//
//import static android.car.hardware.power.FordCarPowerManager.BOOT_REASON_UNKNOWN;
//
//import android.annotation.NonNull;
//
//import android.car.Car;
//import android.car.hardware.CarPropertyValue;
//import android.car.hardware.power.CarPowerManager;
//import android.car.hardware.power.FordCarPowerManager;
//import android.car.hardware.power.ICarPowerStateListener;
//import android.car.hardware.power.IFordCarPower;
//import android.car.hardware.power.IFordCarPowerStateListener;
//
//import android.hardware.automotive.vehicle.V2_0.VehicleApPowerStateReq;
//import android.hardware.automotive.vehicle.V2_0.VehicleApPowerStateShutdownParam;
//
//import android.car.hardware.power.FordCarPowerManager.FordCarPowerStateListener;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.text.TextUtils;
//import android.net.ConnectivityManager;
//import android.net.wifi.WifiManager;
//import android.os.Build;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.Message;
//import android.os.PowerManager;
//import android.os.RemoteCallbackList;
//import android.os.RemoteException;
//import android.os.SystemClock;
//import android.os.SystemProperties;
//import android.util.Log;
//import android.util.Slog;
//import com.android.car.Manifest;
//import com.android.car.hal.FordPowerHalService;
//import com.android.car.hal.PowerHalService;
//import com.android.car.systeminterface.SystemInterface;
//import com.android.car.user.CarUserService;
//import com.android.internal.annotations.GuardedBy;
//import com.android.internal.annotations.VisibleForTesting;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.BluetoothAdapter;
//
//
//import java.io.PrintWriter;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Set;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Implement the API of FordCarPowerManager.
// *
// * @hide
// */
//public class FordCarPowerManagementService extends IFordCarPower.Stub implements CarServiceBase,
//    FordPowerHalService.PowerEventListener {
//
//  private static final String TAG = "FORD.CAR.POWER";
//
//  private static final long INITIAL_SUSPEND_RETRY_INTERVAL_MS = 10;
//  private static final long MAX_RETRY_INTERVAL_MS = 100;
//  private static final long MAX_JOIN_THREAD_MS = 1000;
//  private static final long MAX_SUSPEND_WAIT_DURATION_MS = 180000;
//  private static final int MIN_MAX_GARAGE_MODE_DURATION_MS = 900000;
//  private static final String PROP_MAX_GARAGE_MODE_DURATION_OVERRIDE = "android.car.garagemodeduration";
//  private static final int SHUTDOWN_EXTEND_MAX_MS = 5000;
//  private static final int SHUTDOWN_POLLING_INTERVAL_MS = 2000;
//
//  private int mAppCounter = 0;
//
//  private CarPropertyValue<Integer> mCarProp;
//  private final Context mContext;
//  private CpmsState mCurrentPowerState;
//  private FordPowerHalService.PowerState mCurrentFordPowerState;
//  private int mDeepSleepRetryCount;
//  private boolean mGarageModeShouldExitImmediately;
//  private final FordPowerHalService mHal;
//
//  private final HandlerThread mHandlerThread = CarServiceUtils.getHandlerThread("ford.power");
//  private final PowerHandler mHandler = new PowerHandler(mHandlerThread.getLooper(), this);
//
//  @GuardedBy("mLock")
//  private boolean mIsBooting = true;
//  @GuardedBy("mLock")
//  private boolean mIsResuming;
//
//  private long mLastSleepEntryTime;
//  private final Set<IBinder> mListenersWeAreWaitingFor = new HashSet();
//  private final Object mLock = new Object();
//  private final long mMaxSuspendWaitDurationMs;
//  private int mNextWakeupSec;
//  private final LinkedList<CpmsState> mPendingApPowerStates = new LinkedList<>();
//  private final LinkedList<FordPowerHalService.PowerState> mPendingFordPowerStates = new LinkedList<>();
//  private final Map<IBinder, Integer> mPowerManagerListenerTokens = new ConcurrentHashMap();
//  private final FordPowerManagerCallbackList mFordPowerManagerListeners = new FordPowerManagerCallbackList();
//  private final PowerManagerCallbackList mPowerManagerListeners = new PowerManagerCallbackList();
//  private final PowerManagerCallbackList mPowerManagerListenersWithCompletion = new PowerManagerCallbackList();
//  private final ArrayList<String> mPowerOffIndicators = new ArrayList<>();
//  private long mProcessingStartTime;
//  private boolean mRebootAfterGarageMode;
//  private boolean mShutdownOnFinish;
//  private boolean mShutdownOnNextSuspend;
//
//  private final Object mSimulationWaitObject = new Object();
//  @GuardedBy("mSimulationWaitObject")
//  private boolean mWakeFromSimulatedSleep;
//  @GuardedBy("mSimulationWaitObject")
//  private boolean mInSimulatedDeepSleepMode;
//
//  @GuardedBy("mLock")
//  private int mShutdownPrepareTimeMs = MIN_MAX_GARAGE_MODE_DURATION_MS;
//  @GuardedBy("mLock")
//  private int mShutdownPollingIntervalMs = SHUTDOWN_POLLING_INTERVAL_MS;
//
//  private final SystemInterface mSystemInterface;
//  private final SharedPreferences mSharedPreferences;
//  private Timer mTimer;
//  private boolean mTimerActive;
//
//  private int mBootReason = BOOT_REASON_UNKNOWN;
//
//  private final WifiSwitcher mWifiSwitcher;
//  private final BluetoothSwitcher mBtSwitcher;
//  private final AudioSwitcher mAudioSwitcher;
//
//  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//      String action = intent.getAction();
//      Log.i(TAG, "onReceive: action = " + action);
//
//      if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
//        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
//        Log.i(TAG, "onReceive: mWiFiState = " + wifiState);
//        Log.i(TAG, "isWifiEnable = " + mWifiSwitcher.isEnabled());
//      }
//
//      if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
//        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
//        Log.i(TAG, "BluetoothAdapter: state = " + state);
//        Log.i(TAG, "isBtEnable = " + mBtSwitcher.isBtEnable());
//      }
//    }
//  };
//
//  /**
//   * callback for cover AAOS's CarPowerManager
//   */
//  private class PowerManagerCallbackList extends RemoteCallbackList<ICarPowerStateListener> {
//
//    @Override
//    public void onCallbackDied(ICarPowerStateListener listener) {
//      Log.i(TAG, "binderDied " + listener.asBinder());
//      doUnregisterListener(listener);
//    }
//  }
//
//  /**
//   * callback for cover FordCarPowerManager
//   */
//  private class FordPowerManagerCallbackList extends
//      RemoteCallbackList<IFordCarPowerStateListener> {
//
//    @Override
//    public void onCallbackDied(IFordCarPowerStateListener listener) {
//      Log.i(TAG, "binderDied " + listener.asBinder());
//      doUnregisterFordListener(listener);
//    }
//  }
//
//  @FunctionalInterface
//  interface MyConsumer<T> {
//
//    void accept(T listener) throws RemoteException;
//  }
//
//  private abstract class PowerStateSwitcher {
//
//    public static final String WIFI_STATE_KEY = "wifi_disabled_by_cpms";
//    public static final String BT_STATE_KEY = "bt_disabled_by_cpms";
//    public static final String HOTSPOT_STATE_KEY = "hotspot_disabled_by_cpms";
//
//    public void putState(String key, boolean val) {
//      if (mSharedPreferences != null) {
//        mSharedPreferences.edit().putBoolean(key, val).commit();
//      }
//    }
//
//    public boolean getState(String key) {
//      if (mSharedPreferences != null) {
//        return mSharedPreferences.getBoolean(key, false);
//      }
//      return false;
//    }
//
//    abstract void power(boolean on);
//  }
//
//  private class WifiSwitcher extends PowerStateSwitcher {
//
//    private final WifiManager mWifiManager;
//
//    public WifiSwitcher(Context context) {
//      mWifiManager = context.getSystemService(WifiManager.class);
//    }
//
//    @Override
//    public void power(boolean on) {
//      Slog.i(TAG, "WifiSwitcher power : " + on);
//      if (on && !mWifiManager.isWifiEnabled() && getState(WIFI_STATE_KEY)) {
//        try {
//          mWifiManager.setWifiEnabled(true);
//          putState(WIFI_STATE_KEY, false);
//        } catch (Exception e) {
//          Slog.w(TAG, "fail enable wifi with : " + e.getMessage());
//        }
//      } else if (!on && mWifiManager.isWifiEnabled()) {
//        try {
//          mWifiManager.setWifiEnabled(false);
//          putState(WIFI_STATE_KEY, true);
//        } catch (Exception e) {
//          Slog.w(TAG, "fail enable wifi with : " + e.getMessage());
//        }
//      }
//    }
//
//    public boolean isEnabled() {
//      return mWifiManager.isWifiEnabled();
//    }
//  }
//
//  private class AudioSwitcher extends PowerStateSwitcher {
//
//    public AudioSwitcher(Context context) {
//    }
//
//    @Override
//    public void power(boolean on) {
//      Slog.i(TAG, "AudioSwitcher power : " + on);
//    }
//  }
//
//  private class BluetoothSwitcher extends PowerStateSwitcher {
//
//    private final BluetoothManager mBluetoothManager;
//
//    public BluetoothSwitcher(Context context) {
//      mBluetoothManager = mContext.getSystemService(BluetoothManager.class);
//    }
//
//    private boolean isBtEnable() {
//      BluetoothAdapter adapter = mBluetoothManager.getAdapter();
//      return (adapter != null && adapter.isEnabled());
//    }
//
//    @Override
//    public void power(boolean on) {
//      Slog.i(TAG, "BluetoothSwitcher power : " + on);
//      BluetoothAdapter adapter = mBluetoothManager.getAdapter();
//      if (adapter == null) {
//        Slog.e(TAG, "get BluetoothAdapter fail");
//        return;
//      }
//
//      if (on && !adapter.isEnabled() && getState(BT_STATE_KEY)) {
//        try {
//          if (adapter.enable()) {
//            Slog.i(TAG, "BluetoothAdapter enable");
//            putState(BT_STATE_KEY, false);
//          } else {
//            Slog.w(TAG, "BluetoothAdapter enable fail");
//          }
//        } catch (Exception e) {
//          Slog.w(TAG, "fail enable bt with : " + e.getMessage());
//        }
//      } else if (!on && adapter.isEnabled()) {
//        try {
//          if (adapter.disable()) {
//            Slog.i(TAG, "BluetoothAdapter disable");
//            putState(BT_STATE_KEY, true);
//          } else {
//            Slog.w(TAG, "BluetoothAdapter disable fail");
//          }
//        } catch (Exception e) {
//          Slog.w(TAG, "fail disable bt with : " + e.getMessage());
//        }
//      }
//    }
//  }
//
//  public FordCarPowerManagementService(Context context, FordPowerHalService powerHal,
//      SystemInterface systemInterface, CarUserService carUserService,
//      CarPropertyService propertyService) {
//    Context dct = context.createDeviceProtectedStorageContext();
//    mSharedPreferences = dct.getSharedPreferences(TAG, 0);
//    mContext = context;
//    mHal = powerHal;
//    mSystemInterface = systemInterface;
//
//    mMaxSuspendWaitDurationMs = Math.max(0L,
//        Math.min(getMaxSuspendWaitDurationConfig(), MAX_SUSPEND_WAIT_DURATION_MS));
//
//    mWifiSwitcher = new WifiSwitcher(context);
//    mBtSwitcher = new BluetoothSwitcher(context);
//    mAudioSwitcher = new AudioSwitcher(context);
//  }
//
//  private void registerBroadcastReceiver() {
//    IntentFilter filter = new IntentFilter();
//    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//    mContext.registerReceiver(mBroadcastReceiver, filter);
//  }
//
//  private void unregisterBroadcastReceiver() {
//    mContext.unregisterReceiver(mBroadcastReceiver);
//  }
//
//  @Override
//  public void init() {
//    Log.i(TAG, "init");
//    mHal.setListener(this);
//    if (mHal.isPowerStateSupported()) {
//      // Initialize CPMS in WAIT_FOR_VHAL state
//      onApPowerStateChange(CpmsState.WAIT_FOR_VHAL,
//          FordCarPowerManager.CarPowerStateListener.WAIT_FOR_VHAL);
//    } else {
//      Slog.w(TAG, "Vehicle hal does not support power state yet.");
//      onApPowerStateChange(CpmsState.ON, FordCarPowerManager.CarPowerStateListener.ON);
//    }
//
//    registerBroadcastReceiver();
//
//    setCurrentState(
//        new FordPowerHalService.PowerState(FordCarPowerStateListener.POWER_STATE_INVALID, 0));
//  }
//
//  @Override
//  public void release() {
//    synchronized (mLock) {
//      releaseTimerLocked();
//      mCurrentFordPowerState = null;
//      mCurrentPowerState = null;
//      mHandler.cancelAll();
//      mListenersWeAreWaitingFor.clear();
//    }
//
//    mHandlerThread.quitSafely();
//    try {
//      mHandlerThread.join(MAX_JOIN_THREAD_MS);
//    } catch (InterruptedException e) {
//      Log.e(TAG, "Timeout while joining for handler thread to join.");
//    }
//
//    unregisterBroadcastReceiver();
//
//    mFordPowerManagerListeners.kill();
//    mPowerManagerListeners.kill();
//    mPowerManagerListenerTokens.clear();
//    mSystemInterface.releaseAllWakeLocks();
//  }
//
//  @Override
//  public void dump(PrintWriter writer) {
//    writer.println("*FordPowerManagementService*");
//    writer.print("Ford->mCurrentFordPowerState:" + mCurrentFordPowerState);
//    writer.print(",mProcessingStartTime:" + mProcessingStartTime);
//    writer.println(",mLastSleepEntryTime:" + mLastSleepEntryTime);
//    writer.println("**PowerEventProcessingHandlers");
//  }
//
//  @Override
//  public void onBootReasonReceived(int bootReason) {
//    mBootReason = bootReason;
//    Log.i(TAG, "onBootReasonReceived " + bootReason);
//  }
//
//
//  @Override
//  public int getFordPowerState() {
//    return mCurrentFordPowerState.mState;
//  }
//
//  @Override
//  public void syncPhoneState(int state) {
//    ICarImpl.assertPermission(this.mContext, Manifest.permission.CAR_POWER);
//    mHal.sendPhoneState(state);
//  }
//
//  @Override
//  public void requestLoadShedShutDownNow() {
//    Log.d(TAG, "requestLoadShedShutDownNow");
//    ICarImpl.assertPermission(this.mContext, Manifest.permission.CAR_POWER);
//    mHal.sendLoadShedShutDownNow();
//  }
//
//  @Override
//  public void requestReset() {
//    mHal.sendResetRequest();
//  }
//
//
//  private void notifyFordListeners(int newState) {
//    forEachFordListener(listener -> listener.onStateChanged(newState));
//  }
//
//  private void goHome() {
//    // todo
//  }
//
//  private void handleSwitcher(boolean on, PowerStateSwitcher... switchers) {
//    for (PowerStateSwitcher switcher : switchers) {
//      switcher.power(on);
//    }
//  }
//
//  private boolean needFordPowerStateChange(FordPowerHalService.PowerState newState) {
//    return mCurrentFordPowerState == null || !mCurrentFordPowerState.equals(newState);
//  }
//
//  private void doHandleShutdown() {
//    mHal.sendShutdownStart(0); // fixme
//    mSystemInterface.shutdown();
//  }
//
//  private void setCurrentState(FordPowerHalService.PowerState state) {
//    synchronized (mLock) {
//      mCurrentFordPowerState = state;
//    }
//  }
//
//  @Override
//  public void onPhoneModePopUp(int state, int leftSeconds) {
//    Log.i(TAG, "onPhoneModePopUp -> state: " + state + ", left seconds: " + leftSeconds);
//    forEachFordListener(listener -> listener.onPopChanged(state, leftSeconds));
//  }
//
//  @Override
//  public void onPowerOffRequest() {
//    Log.i(TAG, "onPowerOffRequest");
//    forEachFordListener(listener -> listener.onPowerOff());
//  }
//
//  @Override
//  public void on10sReset() {
//
//  }
//
//  @Override
//  public void setPowerOffIndicator(String name) {
//    ICarImpl.assertPermission(this.mContext, Manifest.permission.CAR_POWER);
//    Log.i(TAG, "registerPowerOffListener: name = " + name);
//
//    if (!TextUtils.isEmpty(name)) {
//      mPowerOffIndicators.add(name);
//    }
//  }
//
//  @Override
//  public void clearPowerOffIndicator(String name) {
//    ICarImpl.assertPermission(this.mContext, Manifest.permission.CAR_POWER);
//    Log.i(TAG, "unregisterPowerOffListener: name = " + name);
//
//    if (!TextUtils.isEmpty(name)) {
//      mPowerOffIndicators.remove(name);
//    }
//  }
//
//  @Override
//  public void indicatePowerOff(String name) {
//    Log.i(TAG, "indicatePowerOff: name = " + name);
//    if (mPowerOffIndicators.contains(name)) {
//      mAppCounter++;
//    }
//    if (mAppCounter == this.mPowerOffIndicators.size()) {
//      Log.i(TAG, "all apps are ready to shutdown");
//      doHandleShutdown();
//    }
//  }
//
//  @Override
//  public void registerFordListener(IFordCarPowerStateListener listener) {
//    ICarImpl.assertPermission(mContext, Car.PERMISSION_CAR_POWER);
//    Log.i(TAG, "registerListener");
//    mFordPowerManagerListeners.register(listener);
//  }
//
//  @Override
//  public void unregisterFordListener(IFordCarPowerStateListener listener) {
//    ICarImpl.assertPermission(this.mContext, Manifest.permission.CAR_POWER);
//    doUnregisterFordListener(listener);
//  }
//
//  private void forEachFordListener(MyConsumer<IFordCarPowerStateListener> action) {
//    int idx = mFordPowerManagerListeners.beginBroadcast();
//    Log.e(TAG, "forEachFordListener idx: " + idx);
//    while (idx-- > 0) {
//      IFordCarPowerStateListener listener = mFordPowerManagerListeners.getBroadcastItem(idx);
//      try {
//        action.accept(listener);
//      } catch (RemoteException e) {
//        // It's likely the connection snapped. Let binder death handle the situation.
//        Log.e(TAG, "Listener callback failed: " + e.getMessage());
//      }
//    }
//    mFordPowerManagerListeners.finishBroadcast();
//  }
//
//  private void doUnregisterFordListener(IFordCarPowerStateListener listener) {
//    mFordPowerManagerListeners.unregister(listener);
//  }
//
//  public void requestShutdownOnNextSuspend() {
//    ICarImpl.assertPermission(this.mContext, Manifest.permission.CAR_POWER);
//    this.mShutdownOnNextSuspend = true;
//  }
//
//  public int getBootReason() {
//    ICarImpl.assertPermission(this.mContext, Manifest.permission.CAR_POWER);
//    return this.mBootReason;
//  }
//
//  @Override
//  public void onFordPowerStateChange(FordPowerHalService.PowerState state) {
//    PowerHandler handler;
//    synchronized (mLock) {
//      mPendingFordPowerStates.addFirst(state);
//      handler = mHandler;
//    }
//    handler.handleFordPowerStateChange();
//  }
//
//  private void doHandleFordPowerStateChange() {
//    synchronized (mLock) {
//      FordPowerHalService.PowerState state = mPendingFordPowerStates.peekFirst();
//      if (state == null) {
//        return;
//      }
//      mPendingFordPowerStates.clear();
//
//      if (needFordPowerStateChange(state)) {
//        releaseTimerLocked();
//        mHandler.cancelProcessingComplete();
//        Log.i(TAG, "Ford Power state change:" + state);
//        switch (state.mState) {
//          case FordCarPowerStateListener.POWER_STATE_SHUTDOWN:
//            Log.i(TAG, "Ford Power shutdown");
//            forEachFordListener(listener -> listener.onPowerOff());
//            break;
//          case FordCarPowerStateListener.POWER_STATE_STANDBY:
//            handleSwitcher(false, mAudioSwitcher, mWifiSwitcher, mBtSwitcher);
//            break;
//          case FordCarPowerStateListener.POWER_STATE_FUNCTIONAL:
//            handleSwitcher(true, mAudioSwitcher, mWifiSwitcher, mBtSwitcher);
//            break;
//          case FordCarPowerStateListener.POWER_STATE_EXTENDED_PLAY:
//            handleSwitcher(true, mAudioSwitcher, mWifiSwitcher, mBtSwitcher);
//            break;
//          case FordCarPowerStateListener.POWER_STATE_PHONE_MODE:
//          case FordCarPowerStateListener.POWER_STATE_KOL:
//          case FordCarPowerStateListener.POWER_STATE_STR:
//            // todo
//            break;
//          case FordCarPowerStateListener.POWER_STATE_LOADSHED:
//            handleSwitcher(false, mAudioSwitcher, mWifiSwitcher, mBtSwitcher);
//            break;
//          case FordCarPowerStateListener.POWER_STATE_ABNORMAL:
//            handleSwitcher(false, mAudioSwitcher, mWifiSwitcher, mBtSwitcher);
//            break;
//          case FordCarPowerStateListener.POWER_STATE_TRANSPORT:
//            handleSwitcher(false, mAudioSwitcher, mWifiSwitcher, mBtSwitcher);
//          default:
//            break;
//        }
//
//        setCurrentState(state);
//        notifyFordListeners(state.mState);
//      }
//    }
//  }
//
//  /***************************** porting from CarPowerManagementService ***************************/
//  @Override
//  public void onApPowerStateChange(FordPowerHalService.PowerState state) {
//    synchronized (mLock) {
//      mPendingApPowerStates.addFirst(new CpmsState(state));
//      mLock.notify();
//    }
//    mHandler.handlePowerStateChange();
//  }
//
//  /**
//   * Initiate state change from CPMS directly.
//   */
//  private void onApPowerStateChange(int apState, int carPowerStateListenerState) {
//    Slog.d(TAG, "FordCarPowerManagementService -> onCarPowerStateChange()");
//    CpmsState newState = new CpmsState(apState, carPowerStateListenerState);
//    synchronized (mLock) {
//      mPendingApPowerStates.addFirst(newState);
//      mLock.notify();
//    }
//    mHandler.handlePowerStateChange();
//  }
//
//  private void doHandleApPowerStateChange() {
//    CpmsState state;
//    synchronized (mLock) {
//      state = mPendingApPowerStates.peekFirst();
//      mPendingApPowerStates.clear();
//      if (state == null) {
//        Slog.e(TAG, "Null power state was requested");
//        return;
//      }
//      Slog.i(TAG, "doHandlePowerStateChange: newState=" + state.name());
//      if (!needApPowerStateChangeLocked(state)) {
//        return;
//      }
//      // now real power change happens. Whatever was queued before should be all cancelled.
//      releaseTimerLocked();
//    }
//    mHandler.cancelProcessingComplete();
//    Slog.i(TAG, "setCurrentState " + state);
//    CarStatsLogHelper.logPowerState(state.mState);
//    mCurrentPowerState = state;
//    switch (state.mState) {
//      case CpmsState.WAIT_FOR_VHAL:
//        handleWaitForVhal(state);
//        break;
//      case CpmsState.ON:
//        handleOn();
//        break;
//      case CpmsState.SHUTDOWN_PREPARE:
//        handleShutdownPrepare(state);
//        break;
//      case CpmsState.SIMULATE_SLEEP:
//        simulateShutdownPrepare();
//        break;
//      case CpmsState.WAIT_FOR_FINISH:
//        handleWaitForFinish(state);
//        break;
//      case CpmsState.SUSPEND:
//        // Received FINISH from VHAL
//        handleFinish();
//        break;
//      default:
//        // Illegal state
//        break;
//    }
//  }
//
//  private void handleWaitForVhal(CpmsState state) {
//    int carPowerStateListenerState = state.mCarPowerStateListenerState;
//    sendPowerManagerEvent(carPowerStateListenerState);
//    // Inspect CarPowerStateListenerState to decide which message to send via VHAL
//    switch (carPowerStateListenerState) {
//      case FordCarPowerManager.CarPowerStateListener.WAIT_FOR_VHAL:
//        mHal.sendWaitForVhal();
//        break;
//      case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_CANCELLED:
//        mShutdownOnNextSuspend = false; // This cancels the "NextSuspend"
//        mHal.sendShutdownCancel();
//        break;
//      case FordCarPowerManager.CarPowerStateListener.SUSPEND_EXIT:
//        mHal.sendSleepExit();
//        break;
//    }
//  }
//
//  private void handleOn() {
//    synchronized (mLock) {
//      if (mIsBooting) {
//        mIsBooting = false;
//        Slog.i(TAG, "User switch disallowed while booting");
//      }
//      mIsResuming = false;
//    }
//
//    sendPowerManagerEvent(FordCarPowerManager.CarPowerStateListener.ON);
//    mHal.sendOn();
//  }
//
//  // Simulate system shutdown to Deep Sleep
//  private void simulateShutdownPrepare() {
//    Slog.i(TAG, "starting shutdown prepare");
//    sendPowerManagerEvent(FordCarPowerManager.CarPowerStateListener.SHUTDOWN_PREPARE);
//    mHal.sendShutdownPrepare();
//    doHandlePreprocessing();
//  }
//
//  private void handleWaitForFinish(CpmsState state) {
//    sendPowerManagerEvent(state.mCarPowerStateListenerState);
//    int wakeupSec;
//    synchronized (mLock) {
//      // If we're shutting down immediately, don't schedule
//      // a wakeup time.
//      wakeupSec = mGarageModeShouldExitImmediately ? 0 : mNextWakeupSec;
//    }
//    switch (state.mCarPowerStateListenerState) {
//      case FordCarPowerManager.CarPowerStateListener.SUSPEND_ENTER:
//        mHal.sendSleepEntry(wakeupSec);
//        break;
//      case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_ENTER:
//        mHal.sendShutdownStart(wakeupSec);
//        break;
//    }
//  }
//
//  private void handleFinish() {
//    Log.d(TAG, "FordCarPowerManagementService -> handleFinish()");
//    boolean simulatedMode;
//    synchronized (mSimulationWaitObject) {
//      simulatedMode = mInSimulatedDeepSleepMode;
//    }
//    boolean mustShutDown;
//    boolean forceReboot;
//    synchronized (mLock) {
//      mustShutDown = mShutdownOnFinish && !simulatedMode;
//      forceReboot = mRebootAfterGarageMode;
//      mRebootAfterGarageMode = false;
//    }
//    if (forceReboot) {
//      PowerManager powerManager = mContext.getSystemService(PowerManager.class);
//      if (powerManager == null) {
//        Slog.wtf(TAG, "No PowerManager. Cannot reboot.");
//      } else {
//        Slog.i(TAG, "GarageMode has completed. Forcing reboot.");
//        powerManager.reboot("GarageModeReboot");
//        throw new AssertionError("Should not return from PowerManager.reboot()");
//      }
//    }
//
//    Log.d(TAG, "FordCarPowerManagementService.handleFinish -> mustShutDown = " + mustShutDown);
//    if (mustShutDown) {
//      // shutdown HU
//      mSystemInterface.shutdown();
//    } else {
//      doHandleDeepSleep(simulatedMode);
//    }
//    mShutdownOnNextSuspend = false;
//  }
//
//  private void handleShutdownPrepare(CpmsState newState) {
//    synchronized (mLock) {
//      mShutdownOnFinish = mShutdownOnNextSuspend
//          || !mHal.isDeepSleepAllowed()
//          || !mSystemInterface.isSystemSupportingDeepSleep()
//          || !newState.mCanSleep;
//      mGarageModeShouldExitImmediately = !newState.mCanPostpone;
//    }
//    Slog.i(TAG,
//        (newState.mCanPostpone
//            ? "starting shutdown prepare with Garage Mode"
//            : "starting shutdown prepare without Garage Mode"));
//    sendPowerManagerEvent(CarPowerManager.CarPowerStateListener.SHUTDOWN_PREPARE);
//    mHal.sendShutdownPrepare();
//    doHandlePreprocessing();
//  }
//
//  @GuardedBy("mLock")
//  private void releaseTimerLocked() {
//    Log.d(TAG, "FordCarPowerManagementService -> releaseTimerLocked()");
//    if (mTimer != null) {
//      mTimer.cancel();
//      mTimer = null;
//      mTimerActive = false;
//    }
//  }
//
//  private void doHandlePreprocessing() {
//    int intervalMs;
//    int pollingCount;
//    synchronized (mLock) {
//      intervalMs = mShutdownPollingIntervalMs;
//      pollingCount = (mShutdownPrepareTimeMs / mShutdownPollingIntervalMs) + 1;
//    }
//    if (Build.IS_USERDEBUG || Build.IS_ENG) {
//      int shutdownPrepareTimeOverrideInSecs =
//          SystemProperties.getInt(PROP_MAX_GARAGE_MODE_DURATION_OVERRIDE, -1);
//      if (shutdownPrepareTimeOverrideInSecs >= 0) {
//        pollingCount =
//            (shutdownPrepareTimeOverrideInSecs * 1000 / intervalMs)
//                + 1;
//        Slog.i(TAG, "Garage mode duration overridden secs:"
//            + shutdownPrepareTimeOverrideInSecs);
//      }
//    }
//    Slog.i(TAG, "processing before shutdown expected for: "
//        + mShutdownPrepareTimeMs + " ms, adding polling:" + pollingCount);
//    synchronized (mLock) {
//      mProcessingStartTime = SystemClock.elapsedRealtime();
//      releaseTimerLocked();
//      mTimer = new Timer();
//      mTimerActive = true;
//      mTimer.scheduleAtFixedRate(
//          new ShutdownProcessingTimerTask(pollingCount),
//          0 /*delay*/,
//          intervalMs);
//    }
//  }
//
//  /**
//   * reference to CarPowerManagementService#sendPowerManagerEvent
//   *
//   * @param newState new power state of AAOS CPMS
//   */
//  private void sendPowerManagerEvent(int newState) {
//    // Broadcast to the listeners that do not signal completion
//    notifyListeners(mPowerManagerListeners, newState);
//
//    // SHUTDOWN_PREPARE is the only state where we need
//    // to maintain callbacks from listener components.
//    boolean allowCompletion = (newState
//        == FordCarPowerManager.CarPowerStateListener.SHUTDOWN_PREPARE);
//
//    // Fully populate mListenersWeAreWaitingFor before calling any onStateChanged()
//    // for the listeners that signal completion.
//    // Otherwise, if the first listener calls finish() synchronously, we will
//    // see the list go empty and we will think that we are done.
//    boolean haveSomeCompleters = false;
//    PowerManagerCallbackList completingListeners = new PowerManagerCallbackList();
//    synchronized (mLock) {
//      mListenersWeAreWaitingFor.clear();
//      int idx = mPowerManagerListenersWithCompletion.beginBroadcast();
//      while (idx-- > 0) {
//        ICarPowerStateListener listener =
//            mPowerManagerListenersWithCompletion.getBroadcastItem(idx);
//        completingListeners.register(listener);
//        if (allowCompletion) {
//          mListenersWeAreWaitingFor.add(listener.asBinder());
//          haveSomeCompleters = true;
//        }
//      }
//      mPowerManagerListenersWithCompletion.finishBroadcast();
//    }
//    // Broadcast to the listeners that DO signal completion
//    notifyListeners(completingListeners, newState);
//
//    if (allowCompletion && !haveSomeCompleters) {
//      // No jobs need to signal completion. So we are now complete.
//      signalComplete();
//    }
//  }
//
//  private void notifyListeners(PowerManagerCallbackList listenerList, int newState) {
//    int idx = listenerList.beginBroadcast();
//    while (idx-- > 0) {
//      ICarPowerStateListener listener = listenerList.getBroadcastItem(idx);
//      try {
//        listener.onStateChanged(newState);
//      } catch (RemoteException e) {
//        // It's likely the connection snapped. Let binder death handle the situation.
//        Slog.e(TAG, "onStateChanged() call failed", e);
//      }
//    }
//    listenerList.finishBroadcast();
//  }
//
//  private void doHandleDeepSleep(boolean simulatedMode) {
//    // keep holding partial wakelock to prevent entering sleep before enterDeepSleep call
//    // enterDeepSleep should force sleep entry even if wake lock is kept.
//    Log.d(TAG, "doForHandleDeepSleep() -> simulatedMode = " + simulatedMode);
//    mSystemInterface.switchToPartialWakeLock();
//    mHandler.cancelProcessingComplete();
//    synchronized (mLock) {
//      mLastSleepEntryTime = SystemClock.elapsedRealtime();
//    }
//    int nextListenerState;
//    if (simulatedMode) {
//      simulateSleepByWaiting();
//      nextListenerState = FordCarPowerManager.CarPowerStateListener.SHUTDOWN_CANCELLED;
//    } else {
//      boolean sleepSucceeded = suspendWithRetries();
//      if (!sleepSucceeded) {
//        // Suspend failed and we shut down instead.
//        // We either won't get here at all or we will power off very soon.
//        return;
//      }
//      // We suspended and have now resumed
//      nextListenerState = FordCarPowerManager.CarPowerStateListener.SUSPEND_EXIT;
//    }
//    synchronized (mLock) {
//      mIsResuming = true;
//      // Any wakeup time from before is no longer valid.
//      mNextWakeupSec = 0;
//    }
//    Slog.i(TAG, "Resuming after suspending");
//    onApPowerStateChange(CpmsState.WAIT_FOR_VHAL, nextListenerState);
//  }
//
//  /**
//   * Powers off the device, considering the given options.
//   *
//   * <p>The final state can be "suspend-to-RAM" or "shutdown". Attempting to go to suspend-to-RAM
//   * on devices which do not support it may lead to an unexpected system state.
//   */
//  public void powerOffFromCommand(boolean skipGarageMode, boolean shutdown) {
//    ICarImpl.assertPermission(mContext, Car.PERMISSION_CAR_POWER);
//    int param = 0;
//    if (shutdown) {
//      param = skipGarageMode ? VehicleApPowerStateShutdownParam.SHUTDOWN_IMMEDIATELY
//          : VehicleApPowerStateShutdownParam.SHUTDOWN_ONLY;
//    } else {
//      param = skipGarageMode ? VehicleApPowerStateShutdownParam.SLEEP_IMMEDIATELY
//          : VehicleApPowerStateShutdownParam.CAN_SLEEP;
//    }
//    FordPowerHalService.PowerState state = new FordPowerHalService.PowerState(
//        VehicleApPowerStateReq.SHUTDOWN_PREPARE, param);
//    synchronized (mLock) {
//      mRebootAfterGarageMode = false;
//      mPendingApPowerStates.addFirst(new CpmsState(state));
//      mLock.notify();
//    }
//    mHandler.handlePowerStateChange();
//  }
//
//  // In a real Deep Sleep, the hardware removes power from the CPU (but retains power
//  // on the RAM). This puts the processor to sleep. Upon some external signal, power
//  // is re-applied to the CPU, and processing resumes right where it left off.
//  // We simulate this behavior by calling wait().
//  // We continue from wait() when forceSimulatedResume() is called.
//  private void simulateSleepByWaiting() {
//    Slog.i(TAG, "Starting to simulate Deep Sleep by waiting");
//    synchronized (mSimulationWaitObject) {
//      while (!mWakeFromSimulatedSleep) {
//        try {
//          mSimulationWaitObject.wait();
//        } catch (InterruptedException ignored) {
//          Thread.currentThread().interrupt(); // Restore interrupted status
//        }
//      }
//      mInSimulatedDeepSleepMode = false;
//    }
//    Slog.i(TAG, "Exit Deep Sleep simulation");
//  }
//
//  private int getMaxSuspendWaitDurationConfig() {
//    return mContext.getResources().getInteger(R.integer.config_maxSuspendWaitDuration);
//  }
//
//  /**
//   * Manually enter simulated suspend (Deep Sleep) mode, trigging Garage mode. If the parameter is
//   * 'true', reboot the system when Garage Mode completes.
//   * <p>
//   * Invoked using "adb shell dumpsys activity service com.android.car suspend" or "adb shell
//   * dumpsys activity service com.android.car garage-mode reboot". This is similar to
//   * 'onApPowerStateChange()' except that it needs to create a CpmsState that is not directly
//   * derived from a VehicleApPowerStateReq.
//   */
//  @VisibleForTesting
//  void forceSuspendAndMaybeReboot(boolean shouldReboot) {
//    synchronized (mSimulationWaitObject) {
//      mInSimulatedDeepSleepMode = true;
//      mWakeFromSimulatedSleep = false;
//      mGarageModeShouldExitImmediately = false;
//    }
//    PowerHandler handler;
//    synchronized (mLock) {
//      mRebootAfterGarageMode = shouldReboot;
//      mPendingApPowerStates.addFirst(new CpmsState(CpmsState.SIMULATE_SLEEP,
//          FordCarPowerManager.CarPowerStateListener.SHUTDOWN_PREPARE));
//      handler = mHandler;
//    }
//    handler.handlePowerStateChange();
//  }
//
//  private boolean needApPowerStateChangeLocked(@NonNull CpmsState newState) {
//    if (mCurrentPowerState == null) {
//      return true;
//    } else if (mCurrentPowerState.equals(newState)) {
//      Slog.d(TAG, "Requested state is already in effect: " + newState.name());
//      return false;
//    }
//
//    // The following switch/case enforces the allowed state transitions.
//    boolean transitionAllowed;
//    switch (mCurrentPowerState.mState) {
//      case CpmsState.WAIT_FOR_VHAL:
//        transitionAllowed = (newState.mState == CpmsState.ON)
//            || (newState.mState == CpmsState.SHUTDOWN_PREPARE);
//        break;
//      case CpmsState.SUSPEND:
//        transitionAllowed = newState.mState == CpmsState.WAIT_FOR_VHAL;
//        break;
//      case CpmsState.ON:
//        transitionAllowed = (newState.mState == CpmsState.SHUTDOWN_PREPARE)
//            || (newState.mState == CpmsState.SIMULATE_SLEEP);
//        break;
//      case CpmsState.SHUTDOWN_PREPARE:
//        // If VHAL sends SHUTDOWN_IMMEDIATELY or SLEEP_IMMEDIATELY while in
//        // SHUTDOWN_PREPARE state, do it.
//        transitionAllowed =
//            ((newState.mState == CpmsState.SHUTDOWN_PREPARE) && !newState.mCanPostpone)
//                || (newState.mState == CpmsState.WAIT_FOR_FINISH)
//                || (newState.mState == CpmsState.WAIT_FOR_VHAL);
//        break;
//      case CpmsState.SIMULATE_SLEEP:
//        transitionAllowed = true;
//        break;
//      case CpmsState.WAIT_FOR_FINISH:
//        transitionAllowed = (newState.mState == CpmsState.SUSPEND
//            || newState.mState == CpmsState.WAIT_FOR_VHAL);
//        break;
//      default:
//        Slog.e(TAG, "Unexpected current state:  currentState="
//            + mCurrentPowerState.name() + ", newState=" + newState.name());
//        transitionAllowed = true;
//    }
//    if (!transitionAllowed) {
//      Slog.e(TAG, "Requested power transition is not allowed: "
//          + mCurrentPowerState.name() + " --> " + newState.name());
//    }
//    return transitionAllowed;
//  }
//
//  private void doHandleProcessingComplete() {
//    int listenerState;
//    synchronized (mLock) {
//      releaseTimerLocked();
//      if (!mShutdownOnFinish && mLastSleepEntryTime > mProcessingStartTime) {
//        // entered sleep after processing start. So this could be duplicate request.
//        Slog.w(TAG, "Duplicate sleep entry request, ignore");
//        return;
//      }
//      listenerState = mShutdownOnFinish
//          ? FordCarPowerManager.CarPowerStateListener.SHUTDOWN_ENTER
//          : FordCarPowerManager.CarPowerStateListener.SUSPEND_ENTER;
//    }
//
//    Slog.d(TAG, "doHandleCarPowerProcessingComplete() -> listenerState = " + listenerState);
//    onApPowerStateChange(CpmsState.WAIT_FOR_FINISH, listenerState);
//  }
//
//  // Binder interface for general use.
//  // The listener is not required (or allowed) to call finished().
//  @Override
//  public void registerListener(ICarPowerStateListener listener) {
//    ICarImpl.assertPermission(mContext, Car.PERMISSION_CAR_POWER);
//    mPowerManagerListeners.register(listener);
//  }
//
//  // Binder interface for Car services only.
//  // After the listener completes its processing, it must call finished().
//  @Override
//  public void registerListenerWithCompletion(ICarPowerStateListener listener) {
//    ICarImpl.assertPermission(mContext, Car.PERMISSION_CAR_POWER);
//    ICarImpl.assertCallingFromSystemProcessOrSelf();
//
//    mPowerManagerListenersWithCompletion.register(listener);
//  }
//
//  @Override
//  public void unregisterListener(ICarPowerStateListener listener) {
//    ICarImpl.assertPermission(mContext, Car.PERMISSION_CAR_POWER);
//    doUnregisterListener(listener);
//  }
//
//  private void doUnregisterListener(ICarPowerStateListener listener) {
//    mPowerManagerListeners.unregister(listener);
//    boolean found = mPowerManagerListenersWithCompletion.unregister(listener);
//    if (found) {
//      // Remove this from the completion list (if it's there)
//      finishedImpl(listener.asBinder());
//    }
//  }
//
//  @Override
//  public void finished(ICarPowerStateListener listener) {
//    Log.i(TAG, "FordPowerManagementService -> finished");
//    ICarImpl.assertPermission(mContext, Car.PERMISSION_CAR_POWER);
//    // ICarImpl.assertCallingFromSystemProcessOrSelf();
//    finishedImpl(listener.asBinder());
//  }
//
//  @Override
//  public void scheduleNextWakeupTime(int seconds) {
//    ICarImpl.assertPermission(mContext, Car.PERMISSION_CAR_POWER);
//    if (seconds < 0) {
//      Slog.w(TAG, "Next wake up time is negative. Ignoring!");
//      return;
//    }
//    boolean timedWakeupAllowed = mHal.isTimedWakeupAllowed();
//    synchronized (mLock) {
//      if (!timedWakeupAllowed) {
//        Slog.w(TAG, "Setting timed wakeups are disabled in HAL. Skipping");
//        mNextWakeupSec = 0;
//        return;
//      }
//      if (mNextWakeupSec == 0 || mNextWakeupSec > seconds) {
//        // The new value is sooner than the old value. Take the new value.
//        mNextWakeupSec = seconds;
//      } else {
//        Slog.d(TAG, "Tried to schedule next wake up, but already had shorter "
//            + "scheduled time");
//      }
//    }
//  }
//
//  @Override
//  public int getPowerState() {
//    return mCurrentPowerState.mState;
//  }
//
//  private void finishedImpl(IBinder binder) {
//    boolean allAreComplete;
//    Log.i(TAG, "CarPowerManagementService -> finishedImpl()");
//    synchronized (mLock) {
//      mListenersWeAreWaitingFor.remove(binder);
//      allAreComplete = mListenersWeAreWaitingFor.isEmpty();
//    }
//    if (allAreComplete) {
//      signalComplete();
//    }
//  }
//
//  private void signalComplete() {
//    Log.i(TAG,
//        "FordCarPowerManagementService.signalComplete -> state = " + mCurrentPowerState.mState);
//    if (mCurrentPowerState.mState == CpmsState.SHUTDOWN_PREPARE
//        || mCurrentPowerState.mState == CpmsState.SIMULATE_SLEEP) {
//      PowerHandler powerHandler;
//      // All apps are ready to shutdown/suspend.
//      synchronized (mLock) {
//        if (!mShutdownOnFinish) {
//          if (mLastSleepEntryTime > mProcessingStartTime
//              && mLastSleepEntryTime < SystemClock.elapsedRealtime()) {
//            Slog.i(TAG, "signalComplete: Already slept!");
//            return;
//          }
//        }
//        powerHandler = mHandler;
//      }
//      Slog.i(TAG, "Apps are finished, call handleProcessingComplete()");
//      powerHandler.handleProcessingComplete();
//    }
//  }
//
//  private boolean verifyDeepSleepRetryAllowed(boolean result) {
//    if (result) {
//      this.mDeepSleepRetryCount = 0;
//      return false;
//    }
//    int i = this.mDeepSleepRetryCount;
//    this.mDeepSleepRetryCount = i + 1;
//    if (i < 50) {
//      return true;
//    }
//    Log.e(TAG, "Exceed max deep sleep retry count 50");
//    return false;
//  }
//
//  private static class PowerHandler extends Handler {
//
//    private static final int MSG_POWER_STATE_CHANGE = 0;
//    private static final int MSG_PROCESSING_COMPLETE = 3;
//
//    private static final int MSG_FORD_POWER_STATE_CHANGE = 4;
//
//    private final WeakReference<FordCarPowerManagementService> mService;
//
//    private PowerHandler(Looper looper, FordCarPowerManagementService service) {
//      super(looper);
//      mService = new WeakReference<>(service);
//    }
//
//    private void handlePowerStateChange() {
//      Message msg = obtainMessage(MSG_POWER_STATE_CHANGE);
//      sendMessage(msg);
//    }
//
//    private void handleProcessingComplete() {
//      removeMessages(MSG_PROCESSING_COMPLETE);
//      Message msg = obtainMessage(MSG_PROCESSING_COMPLETE);
//      sendMessage(msg);
//    }
//
//    private void handleFordPowerStateChange() {
//      Message msg = obtainMessage(MSG_FORD_POWER_STATE_CHANGE);
//      sendMessage(msg);
//    }
//
//    private void cancelProcessingComplete() {
//      removeMessages(MSG_PROCESSING_COMPLETE);
//    }
//
//    private void cancelAll() {
//      removeMessages(MSG_POWER_STATE_CHANGE);
//      removeMessages(MSG_FORD_POWER_STATE_CHANGE);
//    }
//
//    @Override
//    public void handleMessage(Message msg) {
//      FordCarPowerManagementService service = mService.get();
//      if (service == null) {
//        Slog.i(TAG, "handleMessage null service");
//        return;
//      }
//
//      switch (msg.what) {
//        case MSG_POWER_STATE_CHANGE:
//          service.doHandleApPowerStateChange();
//          break;
//
//        case MSG_PROCESSING_COMPLETE:
//          service.doHandleProcessingComplete();
//          break;
//
//        case MSG_FORD_POWER_STATE_CHANGE:
//          service.doHandleFordPowerStateChange();
//          break;
//      }
//    }
//  }
//
//  private class ShutdownProcessingTimerTask extends TimerTask {
//
//    private final int mExpirationCount;
//    private int mCurrentCount;
//
//    private ShutdownProcessingTimerTask(int expirationCount) {
//      mExpirationCount = expirationCount;
//      mCurrentCount = 0;
//    }
//
//    @Override
//    public void run() {
//      synchronized (mLock) {
//        if (!mTimerActive) {
//          // Ignore timer expiration since we got cancelled
//          return;
//        }
//        mCurrentCount++;
//        if (mCurrentCount > mExpirationCount) {
//          PowerHandler handler;
//          releaseTimerLocked();
//          handler = mHandler;
//          handler.handleProcessingComplete();
//        } else {
//          mHal.sendShutdownPostpone(SHUTDOWN_EXTEND_MAX_MS);
//        }
//      }
//    }
//  }
//
//  // Send the command to enter Suspend to RAM.
//  // If the command is not successful, try again with an exponential back-off.
//  // If it fails repeatedly, send the command to shut down.
//  // If we decide to go to a different power state, abort this retry mechanism.
//  // Returns true if we successfully suspended.
//  private boolean suspendWithRetries() {
//    long retryIntervalMs = INITIAL_SUSPEND_RETRY_INTERVAL_MS;
//    long totalWaitDurationMs = 0;
//
//    while (true) {
//      Slog.i(TAG, "Entering Suspend to RAM");
//      boolean suspendSucceeded = mSystemInterface.enterDeepSleep();
//      if (suspendSucceeded) {
//        return true;
//      }
//      if (totalWaitDurationMs >= mMaxSuspendWaitDurationMs) {
//        break;
//      }
//      // We failed to suspend. Block the thread briefly and try again.
//      synchronized (mLock) {
//        if (mPendingApPowerStates.isEmpty()) {
//          Slog.w(TAG, "Failed to Suspend; will retry after " + retryIntervalMs + "ms.");
//          try {
//            mLock.wait(retryIntervalMs);
//          } catch (InterruptedException ignored) {
//            Thread.currentThread().interrupt();
//          }
//          totalWaitDurationMs += retryIntervalMs;
//          retryIntervalMs = Math.min(retryIntervalMs * 2, MAX_RETRY_INTERVAL_MS);
//        }
//        // Check for a new power state now, before going around the loop again
//        if (!mPendingApPowerStates.isEmpty()) {
//          Slog.i(TAG, "Terminating the attempt to Suspend to RAM");
//          return false;
//        }
//      }
//    }
//    // Too many failures trying to suspend. Shut down.
//    Slog.w(TAG, "Could not Suspend to RAM after " + totalWaitDurationMs
//        + "ms long trial. Shutting down.");
//    mSystemInterface.shutdown();
//    return false;
//  }
//
//  private static class CpmsState {
//
//    // NOTE: When modifying states below, make sure to update CarPowerStateChanged.State in
//    //   frameworks/base/cmds/statsd/src/atoms.proto also.
//    public static final int WAIT_FOR_VHAL = 0;
//    public static final int ON = 1;
//    public static final int SHUTDOWN_PREPARE = 2;
//    public static final int WAIT_FOR_FINISH = 3;
//    public static final int SUSPEND = 4;
//    public static final int SIMULATE_SLEEP = 5;
//
//    /* Config values from AP_POWER_STATE_REQ */
//    public final boolean mCanPostpone;
//    public final boolean mCanSleep;
//    /* Message sent to CarPowerStateListener in response to this state */
//    public final int mCarPowerStateListenerState;
//    /* One of the above state variables */
//    public final int mState;
//
//    /**
//     * This constructor takes a PowerHalService.PowerState object and creates the corresponding CPMS
//     * state from it.
//     */
//    CpmsState(FordPowerHalService.PowerState halPowerState) {
//      switch (halPowerState.mState) {
//        case VehicleApPowerStateReq.ON:
//          this.mCanPostpone = false;
//          this.mCanSleep = false;
//          this.mCarPowerStateListenerState = cpmsStateToPowerStateListenerState(ON);
//          this.mState = ON;
//          break;
//        case VehicleApPowerStateReq.SHUTDOWN_PREPARE:
//          this.mCanPostpone = halPowerState.canPostponeShutdown();
//          this.mCanSleep = halPowerState.canEnterDeepSleep();
//          this.mCarPowerStateListenerState = cpmsStateToPowerStateListenerState(
//              SHUTDOWN_PREPARE);
//          this.mState = SHUTDOWN_PREPARE;
//          break;
//        case VehicleApPowerStateReq.CANCEL_SHUTDOWN:
//          this.mCanPostpone = false;
//          this.mCanSleep = false;
//          this.mCarPowerStateListenerState = FordCarPowerManager.CarPowerStateListener.SHUTDOWN_CANCELLED;
//          this.mState = WAIT_FOR_VHAL;
//          break;
//        case VehicleApPowerStateReq.FINISHED:
//          this.mCanPostpone = false;
//          this.mCanSleep = false;
//          this.mCarPowerStateListenerState = cpmsStateToPowerStateListenerState(SUSPEND);
//          this.mState = SUSPEND;
//          break;
//        default:
//          // Illegal state from PowerState.  Throw an exception?
//          this.mCanPostpone = false;
//          this.mCanSleep = false;
//          this.mCarPowerStateListenerState = 0;
//          this.mState = 0;
//          break;
//      }
//    }
//
//    CpmsState(int state) {
//      this(state, cpmsStateToPowerStateListenerState(state));
//    }
//
//    CpmsState(int state, int carPowerStateListenerState) {
//      this.mCanPostpone = (state == SIMULATE_SLEEP);
//      this.mCanSleep = (state == SIMULATE_SLEEP);
//      this.mCarPowerStateListenerState = carPowerStateListenerState;
//      this.mState = state;
//    }
//
//    public String name() {
//      String baseName;
//      switch (mState) {
//        case WAIT_FOR_VHAL:
//          baseName = "WAIT_FOR_VHAL";
//          break;
//        case ON:
//          baseName = "ON";
//          break;
//        case SHUTDOWN_PREPARE:
//          baseName = "SHUTDOWN_PREPARE";
//          break;
//        case WAIT_FOR_FINISH:
//          baseName = "WAIT_FOR_FINISH";
//          break;
//        case SUSPEND:
//          baseName = "SUSPEND";
//          break;
//        case SIMULATE_SLEEP:
//          baseName = "SIMULATE_SLEEP";
//          break;
//        default:
//          baseName = "<unknown>";
//          break;
//      }
//      return baseName + "(" + mState + ")";
//    }
//
//    private static int cpmsStateToPowerStateListenerState(int state) {
//      int powerStateListenerState = 0;
//
//      // Set the CarPowerStateListenerState based on current state
//      switch (state) {
//        case ON:
//          powerStateListenerState = FordCarPowerManager.CarPowerStateListener.ON;
//          break;
//        case SHUTDOWN_PREPARE:
//          powerStateListenerState = FordCarPowerManager.CarPowerStateListener.SHUTDOWN_PREPARE;
//          break;
//        case SUSPEND:
//          powerStateListenerState = FordCarPowerManager.CarPowerStateListener.SUSPEND_ENTER;
//          break;
//        case WAIT_FOR_VHAL:
//        case WAIT_FOR_FINISH:
//        default:
//          // Illegal state for this constructor.  Throw an exception?
//          break;
//      }
//      return powerStateListenerState;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//      if (this == o) {
//        return true;
//      }
//      if (!(o instanceof CpmsState)) {
//        return false;
//      }
//      CpmsState that = (CpmsState) o;
//      return this.mState == that.mState
//          && this.mCanSleep == that.mCanSleep
//          && this.mCanPostpone == that.mCanPostpone
//          && this.mCarPowerStateListenerState == that.mCarPowerStateListenerState;
//    }
//
//    @Override
//    public String toString() {
//      return "CpmsState canSleep:" + mCanSleep + ", canPostpone=" + mCanPostpone
//          + ", carPowerStateListenerState=" + mCarPowerStateListenerState
//          + ", CpmsState=" + this.name();
//    }
//  }
//
//  /**
//   * Resume after a manually-invoked suspend. Invoked using "adb shell dumpsys activity service
//   * com.android.car resume".
//   */
//  public void forceSimulatedResume() {
//    PowerHandler handler;
//    synchronized (mLock) {
//      // Cancel Garage Mode in case it's running
//      mPendingApPowerStates.addFirst(new CpmsState(CpmsState.WAIT_FOR_VHAL,
//          FordCarPowerManager.CarPowerStateListener.SHUTDOWN_CANCELLED));
//      mLock.notify();
//      handler = mHandler;
//    }
//    handler.handlePowerStateChange();
//
//    synchronized (mSimulationWaitObject) {
//      mWakeFromSimulatedSleep = true;
//      mSimulationWaitObject.notify();
//    }
//  }
//}
