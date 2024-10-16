// IFordFnv.aidl
package com.ford.sync.fnvservice;
import android.os.IBinder;

// Declare any non-default types here with import statements

interface IFordFnv {
  /**
   * Demonstrates some basic types that you can use as parameters
   * and return values in AIDL.
   */

  IBinder getService(String name);
}