/**
 * @file FordFnv.java
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @date 2024/8/2
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.ford.sync.fnvservice.gnss.GnssManager;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provides FordFnv APIs to get fnv manger
 * <p>
 * we can get any FNV Manager form this class {@link #getFnvManager(String)} and
 * {@link FnvConstants}
 * <p>
 *   eg: <code>
 *     CcsManager ccsManager = (CcsManager)fordFnv.getFnvManager(FnvConstants.CCS_SERVICE));
 *   </code>
 **/
public class FordFnv {

  private static final String TAG = "FordFnv";
  private static final String RETRY = "retry_thread";
  private static final String PACKAGE_NAME = "com.ford.sync.fnvservice";
  private static final String CLASS_NAME = "com.ford.sync.fnvservice.FordFnvService";

  private static final Object mLock = new Object();
  private static final int MSG_NOTIFY_LISTENER = 1;
  private static final int MSG_RETRY_BIND_SERVICE = 0;
  private static final int TIME_RETRY_BIND_SERVICE = 1000;

  private final Context mContext;

  private IBinder mFnvBinder;
  @GuardedBy("mLock")
  private static volatile FordFnv sFordFnv;
  private static RetryHandler sRetryHandler;
  public volatile boolean mServiceConnected;

  @GuardedBy("mLock")
  private final HashMap<String, FordFnvManagerBase> mServiceMap = new HashMap<>();
  private final static List<FordFnvServiceListener> mFordFnvServiceListeners = new CopyOnWriteArrayList();
  private final ServiceConnection mServiceConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName name, IBinder service) {
      if (service == null) {
        Log.i(TAG, "onServiceConnected: service is null!");
      } else {
        mFnvBinder = service;
        mServiceConnected = true;
        for (FordFnvServiceListener mFordFnvServiceListener : mFordFnvServiceListeners) {
          mFordFnvServiceListener.onServiceConnect(sFordFnv);
        }
      }
    }

    public void onServiceDisconnected(ComponentName name) {
      mFnvBinder = null;
      mServiceConnected = false;
      for (FordFnvServiceListener fordFnvServiceListener : mFordFnvServiceListeners) {
        fordFnvServiceListener.onServiceDisconnect();
      }
      sendRetryMessage();
    }
  };


  /**
   * Constructor private must be use crateFordFnv to get FordFnv instance
   *
   * @param context context
   */
  private FordFnv(Context context) {
    mContext = context.getApplicationContext();
    HandlerThread handlerThread = new HandlerThread(RETRY);
    handlerThread.start();
    sRetryHandler = new RetryHandler(handlerThread.getLooper());
    bindService();
  }

  /**
   * App use FordFnv.createFordFnv create instance get callback from listener when FordFnvService
   * connected or disconnected
   *
   * @param context  context not null
   * @param listener listener not null
   */
  public static void createFordFnv(Context context, FordFnvServiceListener listener) {
    if (sFordFnv == null) {
      synchronized (mLock) {
        if (sFordFnv == null) {
          sFordFnv = new FordFnv(context);
        }
      }
    } else {
      Message message = sRetryHandler.obtainMessage(MSG_NOTIFY_LISTENER, listener);
      sRetryHandler.sendMessage(message);
    }
    if (!mFordFnvServiceListeners.contains(listener)) {
      mFordFnvServiceListeners.add(listener);
    }
  }

  /**
   * App use FordFnv.getService get module service manager by Service name.
   * eg:
   * mCcsManager = (CcsManager)mFordFnv.getFnvManager(FnvConstants.CCS_SERVICE);
   *
   * @param name {@link FnvConstants} get service name
   * @return Service Manager who extends FordFnvManagerBase maybe null but always not
   */
  public FordFnvManagerBase getFnvManager(String name) {
    if (mFnvBinder == null) {
      Log.w(TAG, " FordFnvManagerBase mBinder is null");
      return null;
    }
    if (TextUtils.isEmpty(name)) {
      Log.w(TAG, " FordFnvManagerBase name is null");
      return null;
    }

    synchronized (mLock) {
      FordFnvManagerBase managerBase = mServiceMap.get(name);
      if (managerBase == null) {
        try {
          IBinder mangerBinder = IFordFnv.Stub.asInterface(mFnvBinder).getService(name);
          if (mangerBinder == null) {
            Log.w(TAG, "getFordFnvManager could not get binder for service:" + name);
            return null;
          }
          managerBase = createManagerLocked(name, mangerBinder);
          if (managerBase == null) {
            Log.w(TAG, "getCarManager could not create manager for service:" + name);
            return null;
          }
          managerBase.init();
          mServiceMap.put(name, managerBase);
        } catch (Exception e) {
          Log.w(TAG, "getFnvManager: " + e.getMessage());
        }
      }
      return managerBase;
    }
  }

  private FordFnvManagerBase createManagerLocked(String name, IBinder iBinder) {
    FordFnvManagerBase manager = null;
    switch (name) {
      case FnvConstants.CCS_SERVICE:
//        manager = new CcsManager(this, iBinder);
        break;
      case FnvConstants.GNSS_SERVICE:
        manager = new GnssManager(this, iBinder);
        break;
      case FnvConstants.AUTOSAVE_SERVICE:
        break;
      case FnvConstants.BEZEL_SERVICE:
//        manager = new BezelDiagnosticsManager(this, iBinder);
        break;
      case FnvConstants.DIAGNOSTICS_SERVICE:
//        manager = new DiagnosticsManager(this, iBinder);
        break;
      case FnvConstants.IPPT_SERVICE:
        break;
      case FnvConstants.V2V_SERVICE:
        break;
      default:
        Log.w(TAG, "createManagerLocked: failed not support this service " + name);
        break;
    }
    return manager;
  }

  private void bindService() {
    if (mServiceConnected) {
      Log.w(TAG, "bindService: skip!!! mServiceConnected " + mServiceConnected);
      return;
    }
    Intent intent = new Intent();
    intent.setComponent(new ComponentName(PACKAGE_NAME, CLASS_NAME));
    boolean bindService =
        this.mContext.bindService(intent, this.mServiceConnection, Context.BIND_AUTO_CREATE);
    if (!bindService) {
      sendRetryMessage();
    }
  }

  private void sendRetryMessage() {
    if (sRetryHandler.hasMessages(MSG_RETRY_BIND_SERVICE)) {
      sRetryHandler.removeMessages(MSG_RETRY_BIND_SERVICE);
    }
    sRetryHandler.sendEmptyMessageDelayed(MSG_RETRY_BIND_SERVICE, TIME_RETRY_BIND_SERVICE);
    Log.w(TAG, " bind ford fnv failed -> sendRetryMessage ");
  }

  /**
   * It is not recommended to call this method and use
   *
   * @deprecated use #unbindListener(FordFnvServiceListener) instead; If all FNV child mangers fail
   * in the current process once called
   */
  @Deprecated
  public void unbindService() {
    Log.w(TAG, " unbindService is Deprecated please use unbindListener(listener)!!!");
//    throw new Exception("unbindService is Deprecated please use unbindListener(listener)");
  }

  public void unbindListener(FordFnvServiceListener listener) {
    mFordFnvServiceListeners.remove(listener);
    if (mFordFnvServiceListeners.isEmpty()) {
      if (this.mServiceConnected) {
        this.mContext.unbindService(this.mServiceConnection);
        this.mServiceConnected = false;
      }
    }
  }


  private final class RetryHandler extends Handler {

    private RetryHandler(Looper looper) {
      super(looper);
    }

    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_RETRY_BIND_SERVICE:
          bindService();
          break;
        case MSG_NOTIFY_LISTENER:
          if (mServiceConnected) {
            FordFnvServiceListener listener = (FordFnvServiceListener) msg.obj;
            listener.onServiceConnect(sFordFnv);
          } else {
            bindService();
          }
          break;
      }
    }
  }
}
