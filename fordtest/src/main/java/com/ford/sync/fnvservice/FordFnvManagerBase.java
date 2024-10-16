/**
 * @file FordFnvManagerBase.java
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

import android.util.Log;
import com.ford.sync.fnvservice.utils.ConsumerWithRemoteException;
import com.ford.sync.fnvservice.utils.FunctionWithRemoteException;
import android.os.IInterface;
import android.os.IBinder;

/**
 * the FordFnvManagerBase of FnvManager
 * <p>
 * each FnvManager must be extends this class
 **/
public abstract class FordFnvManagerBase<S extends IInterface> {

  private static final String TAG = "FordFnvManagerBase";
  private final FordFnv mFordFnv;
  protected S mService;

  public FordFnvManagerBase(FordFnv fordFnv, IBinder binder) {
    mFordFnv = fordFnv;
    mService = getServiceInterface(binder);
  }

  protected abstract S getServiceInterface(IBinder service);

   /**
   * Checks the current connection status to the service.
   *
   * @return true if connected, false otherwise
   */
  protected boolean checkConnection() {
    if (!mFordFnv.mServiceConnected) {
      Log.e(TAG, "Service not connected");
    }
    return mFordFnv.mServiceConnected;
  }

    /**
   * Safely invokes a function that might throw a RemoteException.
   *
   * @param invocation the function to be invoked
   */
  protected void safeInvoke(ConsumerWithRemoteException<S> invocation) {
    if (checkConnection()) {
      try {
        invocation.accept(mService);
      } catch (Exception e) {
        Log.e(TAG, "FNVService exception:"+ e.getMessage());
      }
    }
  }

  /**
   * Safely invokes a function that might throw a RemoteException.
   *
   * @param <T>        the type of the result of the function
   * @param invocation the function to be invoked
   * @return the result of the function or null if an exception occurs or the connection check fails
   */
  protected <T> T safeInvoke(FunctionWithRemoteException<T, S> invocation) {
    if (checkConnection()) {
      try {
        return invocation.accept(mService);
      } catch (Exception e) {
        Log.e(TAG, "FNVService exception:"+ e.getMessage());
        return null;
      }
    } else {
      return null;
    }
  }

  public void init() {
  }

  public void release() {
    mService = null;
  }
}
