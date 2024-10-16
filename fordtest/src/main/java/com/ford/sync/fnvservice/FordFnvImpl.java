/**
 * @file IFordFnvImpl
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @date 2024/8/2
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

import android.content.Context;
import android.os.IBinder;

import com.ford.sync.fnvservice.gnss.GnssManagerService;
import com.ford.sync.fnvservice.utils.LogUtils;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides FordFnvImpl
 **/
public class FordFnvImpl extends IFordFnv.Stub {

  private static final String TAG = "FordFnvImpl";

  private final List<FordFnvServiceBase> mFnvServices;

  private final GnssManagerService mGnssService;

  public FordFnvImpl(Context serviceContext) {
    mFnvServices = new ArrayList<>();
    mGnssService = new GnssManagerService();
    mFnvServices.add(mGnssService);
  }

  public void init() {
    LogUtils.i(" init start");
    for (FordFnvServiceBase fnvService : mFnvServices) {
      fnvService.init();
    }
    LogUtils.i(" init end");
  }

  @Override
  public IBinder getService(String name) {
    switch (name) {
      case FnvConstants.GNSS_SERVICE:
        return mGnssService;
      case FnvConstants.AUTOSAVE_SERVICE:
        return null;
      case FnvConstants.IPPT_SERVICE:
        return null;
      case FnvConstants.V2V_SERVICE:
        return null;
      default:
        LogUtils.w("getService: failed not support this service " + name);
        return null;
    }
  }

  public void release() {
    LogUtils.i(" release start");
    for (FordFnvServiceBase fnvService : mFnvServices) {
      fnvService.release();
    }
    LogUtils.i(" release end");
  }

  public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
    for (FordFnvServiceBase fnvService : mFnvServices) {
      fnvService.dump(fd, writer, args);
    }
  }
}
