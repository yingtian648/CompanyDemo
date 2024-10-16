/**
 * @file ServiceConnectProxy.java
 * @author xiarupeng
 * @email xia.rupeng@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice.utils;

import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import com.ford.sync.fnvservice.utils.ConsumerWithRemoteException;
import com.ford.sync.fnvservice.utils.FunctionWithRemoteException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class ServiceConnectProxy<T extends android.os.IInterface> {

  private static final int RETRY_DELAY_MILLIS = 1000;
  private static final long TASK_TIMEOUT_MS = 5000;
  private static final int MSG_CONNECT_SERVICE = 1;

  private String mServiceName;

  private Handler mHandler;

  public T mService;

  private ServiceConnectionListener<T> mServiceListener;

  public Function<String, T> getServiceFunction;

  private final ExecutorService mTaskExecutor = Executors.newSingleThreadExecutor();

  public ServiceConnectProxy(String serviceName, ServiceConnectionListener<T> serviceListener,
      Function<String, T> getServiceFunction) {
    mServiceName = serviceName;
    this.getServiceFunction = getServiceFunction;
    this.mServiceListener = serviceListener;
    mHandler = new ServiceConnectHandler(Looper.getMainLooper());
  }

  private final DeathRecipient deathRecipient = new DeathRecipient() {
    @Override
    public void binderDied() {
      LogUtils.e("%s died.", mServiceName);
      if (mService != null) {
        mService.asBinder().unlinkToDeath(this, 0);
      }
      getNativeServiceDelay();
    }
  };

  public void connect() {
    mHandler.sendEmptyMessage(MSG_CONNECT_SERVICE);
  }

  /**
   * Safely invokes a function on the Native service with a timeout.
   *
   * @param <V>                     the type of the result of the function
   * @param nativeServiceInvocation the function to invoke on the Native service
   * @return the result of the function or null if an exception occurs or the service is not
   * connected
   */
  public <V> V invokeHalServiceSync(
      FunctionWithRemoteException<V, T> nativeServiceInvocation) {
    try {
      return CompletableFuture.supplyAsync(() -> {
        if (mService == null) {
          LogUtils.w("Native service not connected.");
          return null;
        } else {
          try {
            return nativeServiceInvocation.accept(mService);
          } catch (RemoteException e) {
            LogUtils.e("Invoke Native service method error:%s.", e.getMessage());
            return null;
          }
        }
      }, mTaskExecutor).get(TASK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      LogUtils.e("Invoke Native service method error:%s", e.getMessage());
      return null;
    }
  }

  public void invokeHalServiceAsync(
      ConsumerWithRemoteException<T> nativeServiceInvocation, Consumer<Boolean> callback) {
    try {
      mTaskExecutor.execute(() -> {
        if (mService == null) {
          LogUtils.w("Native service not connected.");
          callback.accept(false);
        } else {
          try {
            callback.accept(true);
            nativeServiceInvocation.accept(mService);
          } catch (RemoteException e) {
            LogUtils.e("Invoke Native service method error:%s.", e.getMessage());
            callback.accept(false);
          }
        }
      });
    } catch (Exception e) {
      LogUtils.e("Invoke Native service method error:%s", e.getMessage());
      callback.accept(false);
    }
  }

  private void getNativeService() {
    mService = getServiceFunction.apply(mServiceName);
    if (mService == null) {
      LogUtils.d("%s not found, retrying...", mServiceName);
      getNativeServiceDelay();
    } else {
      try {
        if (mServiceListener != null) {
          mServiceListener.onServiceConnected(this);
        }
        mService.asBinder().linkToDeath(deathRecipient, 0);
      } catch (RemoteException e) {
        LogUtils.e("Error linking to death recipient for %s:%s", mServiceName,
            e.getMessage());
      }
      LogUtils.d("%s connected.", mServiceName);
    }
  }

  /**
   * Schedules a delayed attempt to connect to the Native service.
   */
  private void getNativeServiceDelay() {
    mHandler.removeMessages(MSG_CONNECT_SERVICE);
    mHandler.sendEmptyMessageDelayed(MSG_CONNECT_SERVICE, RETRY_DELAY_MILLIS);
  }

  public void release() {
    mHandler.removeMessages(MSG_CONNECT_SERVICE);
    mTaskExecutor.shutdown();
    mService = null;
  }

  private class ServiceConnectHandler extends Handler {

    public ServiceConnectHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case MSG_CONNECT_SERVICE:
          getNativeService();
          break;
      }
    }
  }

  public interface ServiceConnectionListener<T> {

    void onServiceConnected(ServiceConnectProxy proxy);
  }
}
