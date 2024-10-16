/**
 * @file FordFnvService
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @date 2024/8/2
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Provides FordFnvService
 * <p>
 * launched by Application onCreate
 **/
public class FordFnvService extends Service {

  private FordFnvImpl mFordFnvImpl;

  @Override
  public void onCreate() {
    super.onCreate();
    mFordFnvImpl = new FordFnvImpl(this);
    mFordFnvImpl.init();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mFordFnvImpl;
  }

  @Override
  protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
    super.dump(fd, writer, args);
    mFordFnvImpl.dump(fd, writer, args);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mFordFnvImpl.release();
  }
}
