/**
 * @file GnssManager.java
 * @author Chen Guangming
 * @email chen.guangming@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice.gnss;

import com.ford.sync.fnvservice.FordFnv;
import com.ford.sync.fnvservice.FordFnvManagerBase;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Provides additional Gnss API for CarPlay to get RMC/GGA with DR data
 * <p>
 * -- how to get GnssManager ? <br> 1. FordFnv.createFordFnv(Context context, FordFnvServiceListener
 * listener) <br> 2. FordFnvServiceListener listener = new FordFnvServiceListener(){  <br> void
 * onServiceConnect(FordFnv fordFnv){  <br> GnssManager gnssManager =
 * fordFnv.getFnvManager(FnvConstants.GNSS_SERVICE)); <br> } <br> } <br> -- main function
 * description
 * <br>
 **/
public class GnssManager extends FordFnvManagerBase<IGnss> {

  private static final String TAG = "GnssManager";
  private final Object mLock = new Object();
  private final List<GnssNmeaDataListener> mListeners = new ArrayList<>();

  public interface GnssNmeaDataListener {
    void onGnssGgaDataChanged(GgaNmeaData ggaNmeaData);

    void onGnssRmcDataChanged(RmcNmeaData rmcNmeaData);
  }

  private final IGnssNmeaDataListener mListenerToService = new IGnssNmeaDataListener.Stub() {
    @Override
    public void onGnssGgaDataChanged(GgaNmeaData ggaNmeaData) throws RemoteException {
      synchronized (mLock) {
        for (GnssNmeaDataListener listener : mListeners) {
          listener.onGnssGgaDataChanged(ggaNmeaData);
        }
      }
    }

    @Override
    public void onGnssRmcDataChanged(RmcNmeaData rmcNmeaData) throws RemoteException {
      synchronized (mLock) {
        for (GnssNmeaDataListener listener : mListeners) {
          listener.onGnssRmcDataChanged(rmcNmeaData);
        }
      }
    }
  };

  public GnssManager(FordFnv fordFnv, IBinder binder) {
    super(fordFnv, binder);
  }

  @Override
  protected IGnss getServiceInterface(IBinder binder) {
    return IGnss.Stub.asInterface(binder);
  }

  @Override
  public void init() {
    if (mService != null) {
      try {
        mService.registerNmeaDataListener(mListenerToService);
      } catch (RemoteException e) {
        Log.e(TAG, "init fail: " + e.getMessage());
      }
    }
  }

  @Override
  public void release() {
    if (mService != null) {
      try {
        mService.unregisterNmeaDataListener(mListenerToService);
      } catch (RemoteException e) {
        Log.e(TAG, "release fail: " + e.getMessage());
      }
    }
    super.release();
    synchronized (mLock) {
      mListeners.clear();
    }
  }

  /**
   * The map application must call this method every time it receives the location data.
   *
   * @param bundle include shifted location data from MAP, the key and type in bundle as follow:
   *               <ul>
   *                 <li>pairingKey - long, came from we added in android Location</li>
   *                 <li>ChinaShiftedLongitude - double, shifted longitude from WGS84 to GCJ-02 in decimal degrees, range is -180 to 180</li>
   *                 <li>ChinaShiftedLatitude - double, shifted latitude from WGS84 to GCJ-02 in decimal degrees, range is -90 to 90</li>
   *               </ul>
   * @deprecated Use FordCarLocationManager#sendLocationShiftedData in car-lib instead.
   */
  @Deprecated
  public void pushLocationShiftedData(Bundle shiftedData) {
    if (shiftedData == null) {
      Log.w(TAG, "not pass null shiftedData");
      return;
    }
    if (mService != null) {
      try {
        mService.pushLocationShiftedData(shiftedData);
      } catch (RemoteException e) {
        Log.e(TAG, "pushLocationShiftedData: " + e.getMessage());
      }
    } else {
      Log.w(TAG, "FNV Service maybe dead, please try again later");
    }
  }

  /**
   * registerNmeaDataListener
   *
   * @param listener to receive GPRMC and GPGGA data
   * @return void
   */
  public void registerNmeaDataListener(GnssNmeaDataListener listener) {
    if (listener == null) {
      Log.w(TAG, "not pass null gnssNmeaDataListener");
      return;
    }

    Log.i(TAG, "registerNmeaDataListener " + listener);
    synchronized (mLock) {
      if (!mListeners.contains(listener)) {
        mListeners.add(listener);
      }
    }
  }

  /**
   * unregisterNmeaDataListener
   *
   * @param listener which registered
   * @return void
   */
  public void unregisterNmeaDataListener(GnssNmeaDataListener listener) {
    if (listener == null) {
      Log.w(TAG, "not pass null gnssNmeaDataListener");
      return;
    }

    Log.i(TAG, "unregisterNmeaDataListener " + listener);
    synchronized (mLock) {
      mListeners.remove(listener);
    }
  }
}
