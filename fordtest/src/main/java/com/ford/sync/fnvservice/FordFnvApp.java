/**
 * @file FordApp
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @date 2024/8/13
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * Provides FordFnvApp
 **/
public class FordFnvApp extends Application {

  private static final String TAG = "FordApp";
  public static final String COM_FORD_SYNC_FNVSERVICE = "com.ford.sync.fnvservice";
  public static final String COM_FORD_SYNC_ANALYTICS = "com.ford.sync.fnvservice.analytics";
  private static FordFnvApp sApp;

  @Override
  public void onCreate() {
    super.onCreate();
    sApp = this;
    launchFordFnvService();
  }

  private void launchFordFnvService() {
    String processName = getProcessName();
    if (COM_FORD_SYNC_FNVSERVICE.equals(processName)) {
      Log.i(TAG, "launchFordFnvService: start " + processName);
      startService(new Intent(this, FordFnvService.class));
      Log.i(TAG, "launchFordFnvService: end");
      Log.i(TAG, "launchAnalyticsService: start " + processName);
      Log.i(TAG, "launchAnalyticsService: end");
    }
  }

  public static FordFnvApp getApplication() {
    return sApp;
  }
}
