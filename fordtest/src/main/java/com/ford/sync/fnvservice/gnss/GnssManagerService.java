/**
 * @file GnssManagerService.java
 * @author Chen Guangming
 * @email chen.guangming@zlingsmart.com
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice.gnss;

import android.content.Context;
import android.os.Bundle;
import android.os.IHwBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import com.ford.sync.fnvservice.gnss.IGnss;
import com.ford.sync.fnvservice.gnss.IGnssNmeaDataListener;
import com.ford.sync.fnvservice.gnss.GgaNmeaData;
import com.ford.sync.fnvservice.gnss.RmcNmeaData;
import com.ford.sync.fnvservice.FordFnvServiceBase;
import com.ford.sync.fnvservice.FordFnvApp;

import vendor.zlsmart.gnssext.V1_0.IGnssExt;
import vendor.zlsmart.gnssext.V1_0.INmeaDataCallback;
import vendor.zlsmart.gnssext.V1_0.LocationShiftedData;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.SharedPreferences;

/**
 * Implement additional Gnss API for CarPlay to get RMC/GGA with DR data
 **/
public class GnssManagerService extends IGnss.Stub implements FordFnvServiceBase {
  private static final String TAG = "FNV.GNSS";
  private static final String CHINA_LATITUDE = "ChinaShiftedLatitude";
  private static final String CHINA_LONGITUDE = "ChinaShiftedLongitude";
  private static final String PAIRING_KEY = "pairingKey";
  private static final int RETRY_DELAY_MILLIS = 200;
  private static final int PUBLIST_INTERVAL = 1000;
  private static final int MSG_RETRY_INIT_HAL = 1;
  private static final int MSG_START_PUBLISH = 2;
  private final NmeaRemoteCallbackList mNmeaCallbackList = new NmeaRemoteCallbackList();
  private final boolean mIsFNV2 = true; // fixme use prop
  private IGnssExt mGnssExtService;

  private HandlerThread mThread;
  private Handler mHandler;
  private Timer mTimer;
  private String mLastGga;
  private String mLastRmc;
  private String mCurrGga;
  private String mCurrRmc;
  private final Object mNmeaDataLock = new Object();

  /** GGA/RMC index value follow International standard field sorting */
  private static final int GGA_INDEX_UTC_TIME = 1;
  private static final int GGA_INDEX_LATITUDE = 2;
  private static final int GGA_INDEX_LATITUDE_DIRECTION = 3;
  private static final int GGA_INDEX_LONGITUDE = 4;
  private static final int GGA_INDEX_LONGITUDE_DIRECTION = 5;
  private static final int GGA_INDEX_LOC_VALIDITY = 6;
  private static final int GGA_INDEX_NUM_SV_USED = 7;
  private static final int GGA_INDEX_HORIZONTAL_ACCURACY = 8;
  private static final int GGA_INDEX_ANTENNA_HEIGHT = 9;
  private static final int GGA_INDEX_ANTENNA_HEIGHT_UNIT = 10;
  private static final int GGA_INDEX_GEO_ID_HEIGHT = 11;
  private static final int GGA_INDEX_GEO_ID_HEIGHT_UNIT = 12;
  private static final int GGA_INDEX_DIFFER_TIME = 13;
  /** differ station id and checkvalue in the same field */
  private static final int GGA_INDEX_DIFFER_STATION_ID_AND_CHECK_VALUE = 14;

  private static final int RMC_INDEX_UTC_TIME = 1;
  private static final int RMC_INDEX_LOC_VALIDITY = 2;
  private static final int RMC_INDEX_LATITUDE = 3;
  private static final int RMC_INDEX_LATITUDE_DIRECTION = 4;
  private static final int RMC_INDEX_LONGITUDE = 5;
  private static final int RMC_INDEX_LONGITUDE_DIRECTION = 6;
  private static final int RMC_INDEX_SPEED = 7;
  private static final int RMC_INDEX_HEADING = 8;
  private static final int RMC_INDEX_UTC_DATE = 9;
  private static final int RMC_INDEX_MAGNETIC_DECLINATION = 10;
  private static final int RMC_INDEX_DECLINATION_DIRECTION = 11;
  private static final int RMC_INDEX_MODE_INDEX = 12;
  private static final int RMC_INDEX_CHECK_VALUE = 13;

  private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HHmmss.SSS", Locale.ENGLISH);
  private final SimpleDateFormat mDateFormat = new SimpleDateFormat("ddMMyy", Locale.ENGLISH);

  /**
   * use to save local nmea data
   */
  private static final String SP_LOCAL_NAME = "fnv_car_location";
  private static final String SP_KEY_GGA = "nmea_gga";
  private static final String SP_KEY_RMC = "nmea_rmc";
  private SharedPreferences mSp;

  private final IHwBinder.DeathRecipient mDeathRecipient = new IHwBinder.DeathRecipient() {
    @Override
    public void serviceDied(long l) {
      Log.w(TAG, "GnssExt HAL Service died.");
      try {
        mGnssExtService.unlinkToDeath(this);
      } catch (RemoteException err) {
        Log.e(TAG, "Failed to unlinkToDeath", err);
      }

      Log.d(TAG, "reconnect to GnssExt HAL Service");
      mGnssExtService = getGnssExtService();
    }
  };

  private class NmeaRemoteCallbackList extends RemoteCallbackList<IGnssNmeaDataListener> {
    @Override
    public void onCallbackDied(IGnssNmeaDataListener callback) {
      super.onCallbackDied(callback);
      if (getRegisteredCallbackCount() == 0) {
        releaseTimer();
      }
    }
  }

  @FunctionalInterface
  interface MyConsumer<T> {
    void accept(T listener) throws RemoteException;
  }

  private INmeaDataCallback.Stub mNmeaDataCallback = new INmeaDataCallback.Stub() {
    @Override
    public void gnssNmeaCb(long systemTime, String nmea) {
      Log.i(TAG, "gnssNmeaCb : " + nmea);
      synchronized (mNmeaDataLock) {
        if (nmea.startsWith("$GPGGA")) {
          mCurrGga = nmea;
        } else if (nmea.startsWith("$GPRMC")) {
          mCurrRmc = nmea;
        }
      }
    }
  };

  private final Handler.Callback mCallback = msg -> {
    switch (msg.what) {
      case MSG_RETRY_INIT_HAL:
        mGnssExtService = getGnssExtService();
        if (mGnssExtService == null) {
          mHandler.removeMessages(MSG_RETRY_INIT_HAL);

          mHandler.sendEmptyMessageDelayed(MSG_RETRY_INIT_HAL, RETRY_DELAY_MILLIS);
        }
        break;
      case MSG_START_PUBLISH:
        startPublishTimer();
        break;
    }
    return false;
  };

  private void forEachListener(MyConsumer<IGnssNmeaDataListener> action) {
    int idx = mNmeaCallbackList.beginBroadcast();
    while (idx-- > 0) {
      IGnssNmeaDataListener listener = mNmeaCallbackList.getBroadcastItem(idx);
      try {
        action.accept(listener);
      } catch (RemoteException e) {
        // It's likely the connection snapped. Let binder death handle the situation.
        Log.e(TAG, "Listener callback failed: " + e.getMessage());
      }
    }
    mNmeaCallbackList.finishBroadcast();
  }

  @Override
  synchronized public void init() {
    Log.d(TAG, "init");
    mSp = FordFnvApp.getApplication().getSharedPreferences(SP_LOCAL_NAME, Context.MODE_PRIVATE);
    mCurrGga = getLocalNmea(SP_KEY_GGA);
    mCurrRmc = getLocalNmea(SP_KEY_RMC);
    mLastGga = mCurrGga;
    mLastRmc = mCurrRmc;
    mTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    if (mThread == null) {
      mThread = new HandlerThread("fnv-gnss");
      mThread.start();
      mHandler = new Handler(mThread.getLooper(), mCallback);

      mHandler.sendEmptyMessage(MSG_RETRY_INIT_HAL);
    }
  }

  private void startPublishTimer() {
    if (mTimer == null) {
      mTimer = new Timer();
      mTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          publishNmeaData();
        }
      }, 0 /*delay*/, PUBLIST_INTERVAL);
    }
  }

  private void publishNmeaData() {
    final String gga;
    final String rmc;
    synchronized (mNmeaDataLock) {
      gga = mCurrGga;
      rmc = mCurrRmc;
    }
    if (gga != null) {
      forEachListener(listener -> listener.onGnssGgaDataChanged(parseGgaNmeaData(gga)));
      mLastGga = gga;
      saveNmea(SP_KEY_GGA, gga);
    } else {
      Log.w(TAG, "Failed to publishGgaNmeaData, mCurrGga is null!");
    }
    if (rmc != null) {
      forEachListener(listener -> listener.onGnssRmcDataChanged(parseRmcNmeaData(rmc)));
      mLastRmc = rmc;
      saveNmea(SP_KEY_RMC, rmc);
    } else {
      Log.w(TAG, "Failed to publishRmcNmeaData, mCurrRmc is null!");
    }
  }

  private void releaseTimer() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }

  @Override
  synchronized public void release() {
    if (mThread != null) {
      mThread.quitSafely();
      mThread = null;
    }
    releaseTimer();
  }

  @Override
  public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
  }

  @Override
  public void pushLocationShiftedData(Bundle bundle) {
    if (mGnssExtService != null) {
      LocationShiftedData shiftedData = new LocationShiftedData();
      shiftedData.pairingKey = bundle.getLong(PAIRING_KEY);
      shiftedData.coordShifted.latitude = bundle.getDouble(CHINA_LATITUDE);
      shiftedData.coordShifted.longitude = bundle.getDouble(CHINA_LONGITUDE);

      try {
        mGnssExtService.sendLocationShifted(shiftedData);
      } catch (RemoteException err) {
        Log.e(TAG, "Failed to sendLocationShifted" + err.getMessage());
      }
    }
  }

  @Override
  public void registerNmeaDataListener(IGnssNmeaDataListener listener) {
    if (listener == null) {
      Log.w(TAG, "registerNmeaDataListener null");
      return;
    }
    mNmeaCallbackList.register(listener);
    mHandler.sendEmptyMessage(MSG_START_PUBLISH);
  }

  @Override
  public void unregisterNmeaDataListener(IGnssNmeaDataListener listener) {
    if (listener == null) {
      Log.w(TAG, "unregisterNmeaDataListener null");
      return;
    }
    mNmeaCallbackList.unregister(listener);
  }

  private IGnssExt getGnssExtService() {
    try {
      IGnssExt gnssExt = IGnssExt.getService(mIsFNV2 ? "default" : "fnv3");
      if (gnssExt != null) {

        gnssExt.linkToDeath(mDeathRecipient, 0L);
        gnssExt.registerNmeaDataCallback(mNmeaDataCallback);
      }
      return gnssExt;
    } catch (Exception err) {
      Log.w(TAG, "getGnssExtService fail: " + err.getMessage());
    }

    return null;
  }

  private GgaNmeaData parseGgaNmeaData(String nmea) {
    final boolean isLastKnown = Objects.equals(nmea, mLastGga);
    String[] fields = nmea.split(",");
    GgaNmeaData gga = new GgaNmeaData();
    gga.mSentence = nmea;
    for (int i = 0; i < fields.length; i++) {
      switch (i) {
        case GGA_INDEX_UTC_TIME:
          if (isLastKnown) {
            gga.mUtcTime = mTimeFormat.format(new Date());
            gga.mSentence = gga.mSentence.replace(fields[i], gga.mUtcTime);
          } else {
            gga.mUtcTime = fields[i];
          }
          break;
        case GGA_INDEX_LATITUDE:
          gga.mLatitude = fields[i];
          break;
        case GGA_INDEX_LATITUDE_DIRECTION:
          gga.mLatDirection = fields[i];
          break;
        case GGA_INDEX_LONGITUDE:
          gga.mLongitude = fields[i];
          break;
        case GGA_INDEX_LONGITUDE_DIRECTION:
          gga.mLonDirection = fields[i];
          break;
        case GGA_INDEX_LOC_VALIDITY:
          gga.mLocValidity = fields[i];
          break;
        case GGA_INDEX_NUM_SV_USED:
          gga.mNumSvUsed = fields[i];
          break;
        case GGA_INDEX_HORIZONTAL_ACCURACY:
          gga.mHorizontalAccuracy = fields[i];
          break;
        case GGA_INDEX_ANTENNA_HEIGHT:
          gga.mAntennaHeight = fields[i];
          break;
        case GGA_INDEX_ANTENNA_HEIGHT_UNIT:
          gga.mAntennaHeightUnit = fields[i];
          break;
        case GGA_INDEX_GEO_ID_HEIGHT:
          gga.mGeoidHeight = fields[i];
          break;
        case GGA_INDEX_GEO_ID_HEIGHT_UNIT:
          gga.mGeoidHeightUnit = fields[i];
          break;
        case GGA_INDEX_DIFFER_TIME:
          gga.mDifferTime = fields[i];
          break;
        case GGA_INDEX_DIFFER_STATION_ID_AND_CHECK_VALUE:
          int indexStar = fields[i].indexOf("*");
          if (indexStar <= 0) {
            gga.mDifferStationId = "";
          } else {
            gga.mDifferStationId = fields[i].substring(0, indexStar);
          }
          if (indexStar >= 0) {
            if (isLastKnown) {
              gga.mCheckValue = getCheckValue(gga.mSentence);
              gga.mSentence = gga.mSentence.replace(fields[i], gga.mCheckValue);
            } else {
              gga.mCheckValue = fields[i].substring(indexStar);
            }
          } else {
            gga.mCheckValue = "";
          }
          break;
        default:
          break;
      }
    }
    return gga;
  }

  private RmcNmeaData parseRmcNmeaData(String nmea) {
    final boolean isLastKnown = Objects.equals(nmea, mLastRmc);
    String[] fields = nmea.split(",");
    RmcNmeaData rmc = new RmcNmeaData();
    rmc.mSentence = nmea;
    for (int i = 0; i < fields.length; i++) {
      switch (i) {
        case RMC_INDEX_UTC_TIME:
          if (isLastKnown) {
            rmc.mUtcTime = mTimeFormat.format(new Date());
            rmc.mSentence = rmc.mSentence.replace(fields[i], rmc.mUtcTime);
          } else {
            rmc.mUtcTime = fields[i];
          }
          break;
        case RMC_INDEX_LOC_VALIDITY:
          rmc.mLocValidity = fields[i];
          break;
        case RMC_INDEX_LATITUDE:
          rmc.mLatitude = fields[i];
          break;
        case RMC_INDEX_LATITUDE_DIRECTION:
          rmc.mLatDirection = fields[i];
          break;
        case RMC_INDEX_LONGITUDE:
          rmc.mLongitude = fields[i];
          break;
        case RMC_INDEX_LONGITUDE_DIRECTION:
          rmc.mLonDirection = fields[i];
          break;
        case RMC_INDEX_SPEED:
          rmc.mSpeed = fields[i];
          break;
        case RMC_INDEX_HEADING:
          rmc.mHeading = fields[i];
          break;
        case RMC_INDEX_UTC_DATE:
          if (isLastKnown) {
            rmc.mUtcDate = mDateFormat.format(new Date());
            rmc.mSentence = rmc.mSentence.replace(fields[i], rmc.mUtcDate);
          } else {
            rmc.mUtcDate = fields[i];
          }
          break;
        case RMC_INDEX_MAGNETIC_DECLINATION:
          rmc.mMagneticDeclination = fields[i];
          break;
        case RMC_INDEX_DECLINATION_DIRECTION:
          rmc.mDeclinationDirection = fields[i];
          break;
        case RMC_INDEX_MODE_INDEX:
          rmc.mModeIndex = fields[i];
          break;
        case RMC_INDEX_CHECK_VALUE:
          if (isLastKnown) {
            rmc.mCheckValue = getCheckValue(rmc.mSentence);
            rmc.mSentence = rmc.mSentence.replace(fields[i], rmc.mCheckValue);
          } else {
            rmc.mCheckValue = fields[i];
          }
          break;
        default:
          break;
      }
    }
    return rmc;
  }

  /**
   * CheckValue=bitwise XOR value of all characters between "$" and "*"
   * (excluding these two characters)
   * @return checkvalue contains *
   */
  private String getCheckValue(String nmea) {
    if (nmea == null || !nmea.contains("$") || !nmea.contains("*")) {
      return "";
    }
    String checkStart = "*";
    final int lastIndexField = nmea.lastIndexOf(",");
    final int lastIndexStar = nmea.lastIndexOf("*");
    if (lastIndexStar > lastIndexField + 1) {
      checkStart = nmea.substring(lastIndexField + 1, lastIndexStar) + checkStart;
    }
    String content = nmea.substring(nmea.indexOf("$") + 1, lastIndexStar);
    int value = 0;
    for (int i = 0; i < content.length(); i++) {
      value ^= content.charAt(i);
    }
    return checkStart + Integer.toHexString(value).toUpperCase();
  }

  private void saveNmea(String key, String nmea) {
    mSp.edit().putString(key, nmea).apply();
  }

  private String getLocalNmea(String key) {
    return mSp.getString(key, null);
  }
}
