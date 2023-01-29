/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.exa.companydemo.location.demo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;

import com.android.server.location.gnss.IExtLocationCallback;
import com.android.server.location.gnss.IExtLocationInterface;
import com.exa.baselib.utils.GpsConvertUtil;
import com.exa.baselib.utils.LogUtil;
import com.exa.companydemo.location.demo.utils.SOAListener;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.exa.companydemo.location.demo.utils.Constants.COUNTDOWN_TIME;
import static com.exa.companydemo.location.demo.utils.Constants.ID_CONNECT;
import static com.exa.companydemo.location.demo.utils.Constants.MAX_DUMP_NUM;
import static com.exa.companydemo.location.demo.utils.Constants.OVERTIME;
import static com.exa.companydemo.location.demo.utils.Constants.REFIND;
import static com.exa.companydemo.location.demo.utils.Constants.THREAD_NAME;
import static com.exa.companydemo.location.demo.utils.Constants.TIMEOUT_LIMIT;


public class LocationServiceImpl extends IExtLocationInterface.Stub {
    private Context mContext;
    private final LocationBean mLocationBean = new LocationBean();
    private HandlerThread mHandlerThread;
    private Handler mAsyncHandler;

    private Location soaMessage;
    private long lastReceivedTime;
    private int overTimeCount = 0;
    private IExtLocationCallback mExtLocationCallback;
    private final List<Location> locationInfoDumpList = new ArrayList<>();
    private final List<Long> timeDumpList = new ArrayList<>();
    // GnssStatus normal config,prn neet Shift Left 12
    private static final int SVID_SHIFT_WIDTH = 12;

    private final SOAListener mSoaListener = new SOAListener() {
        @Override
        public void onMessageReceive(Object message) {
            // receive message  every 1000ms
            mAsyncHandler.removeMessages(OVERTIME);
            if (message == null) {
                LogUtil.debug("receive null LocationInfo message!!!");
                sendOverTimeMessage();
                return;
            }

            LogUtil.debug("on SOA onMessageReceive: " + message +
                    "--------current system time is " + System.currentTimeMillis());

            if (message instanceof Location) {
                soaMessage = (Location) message;
            } else {
                throw new ClassCastException("can not cast object to LocationInfo");
            }

            if (locationInfoDumpList.size() < MAX_DUMP_NUM) {
                locationInfoDumpList.add(soaMessage);
                timeDumpList.add(System.currentTimeMillis());
            } else {
                locationInfoDumpList.clear();
                timeDumpList.clear();
            }

            LogUtil.debug("time gap is: " + (System.currentTimeMillis() - lastReceivedTime));
            lastReceivedTime = System.currentTimeMillis();
            if (System.currentTimeMillis() - lastReceivedTime < COUNTDOWN_TIME) {
                overTimeCount = 0;
                updateLocation();
                try {
                    if (mExtLocationCallback != null) {
                        LogUtil.info("call onLocation callback");
                        mExtLocationCallback.onLocation(mLocationBean.getLocation());
                    } else {
                        LogUtil.debug("callback has not been set!!!");
                    }
                    reportSvStatus();
                } catch (RemoteException e) {
                    LogUtil.error("catch remote exception, message is:" + e.getMessage());
                    e.printStackTrace();
                }
            }
            sendOverTimeMessage();
        }

        @Override
        public void onTimeOut() {
            LogUtil.error("SOA service timeout");
        }
    };

    public LocationServiceImpl(Context context) {
        mContext = context;
    }

    public void onCreate() {
        LogUtil.debug("on create LocationServiceImpl");
        mHandlerThread = new HandlerThread(THREAD_NAME);
        mHandlerThread.start();
        mAsyncHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                asyncHandleMessage(msg);
            }
        };

        mAsyncHandler.sendEmptyMessage(ID_CONNECT);
    }

    private void updateLocation() {
        mLocationBean.setAltitude(soaMessage.getAltitude());
        mLocationBean.setLongitude(soaMessage.getLongitude());
        mLocationBean.setLatitude(soaMessage.getLatitude());
        mLocationBean.setSpeed(soaMessage.getSpeed());
        mLocationBean.setHorizontalAccuracyMeters(soaMessage.getAccuracy());
        mLocationBean.setVerticalAccuracyMeters(soaMessage.getVerticalAccuracyMeters());
        mLocationBean.setSpeedAccuracyMetersPerSecond(soaMessage.getSpeedAccuracyMetersPerSecond());
        mLocationBean.setBearingAccuracyDegrees(soaMessage.getBearingAccuracyDegrees());
        mLocationBean.setDate(10123);
        mLocationBean.setTime(10123, 120101.00);
        mLocationBean.setBearing(soaMessage.getBearing());
        mLocationBean.setValid(true);
        mLocationBean.setSatelliteNum(5);
        mLocationBean.setVDOP(2.123f);
        mLocationBean.setPDOP(3.123f);
        mLocationBean.setHDOP(4.123f);
        mLocationBean.setMagDeclination(25);
        mLocationBean.setSatAvailable(15);
        mLocationBean.setSatSystem(1);
        SatInfo_t[] satInfoTs = new SatInfo_t[5];
        for (int i = 0; i < 5; i++) {
            SatInfo_t satInfoT = new SatInfo_t();
            satInfoT.pRN = 12;
            satInfoT.azimuth = 13;
            satInfoT.elevation = 14;
            satInfoT.sNR = 15;
            satInfoTs[i] = satInfoT;
        }
        mLocationBean.setSatInfoList(satInfoTs);

        LogUtil.debug("updateLocation:" + System.currentTimeMillis() + ":" + mLocationBean.toString());
    }

    private void initSOAClient() {
        subscribeMessage();
    }

    private void subscribeMessage() {
        getRepeatData();
        sendOverTimeMessage();
    }

    double lat = 34.5624251;
    double lon = 104.03663435;
    float beearing = 0;
    int index = 0;

    private void getRepeatData() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                index++;
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setTime(GpsConvertUtil.getCurrentTimeZoneTimeMillis(15122022, 113033.00));
                location.setLongitude(lon);
                location.setLatitude(lat);
                location.setSpeed(20f);//单位：米/秒
                location.setAltitude(600);//海拔
                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());//启动后的纳秒数，包括睡眠时间
                location.setAccuracy(2f);//精度
                location.setBearing(beearing);//方向
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    location.setVerticalAccuracyMeters(3f);
                    location.setSpeedAccuracyMetersPerSecond(4f);
                    location.setBearingAccuracyDegrees(5f);
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("isValid", true);
                bundle.putInt("satellites", 10);
                bundle.putInt("satAvailable", 11);
                bundle.putInt("m_used_sat", 12);
                bundle.putFloat("m_hdop", 2.1354f);
                bundle.putFloat("m_pdop", 3.1354f);
                bundle.putFloat("m_vdop", 4.1354f);
                bundle.putInt("mode", 1);
                location.setExtras(bundle);
                location.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    location.setElapsedRealtimeUncertaintyNanos(10000000.00);
                }
                if (index > 20) {
                    mSoaListener.onMessageReceive(location);
                }
            }
        }, 100, 1300);
    }

    private void asyncHandleMessage(Message msg) {
        int id = msg.what;
        switch (id) {
            case ID_CONNECT:
                LogUtil.debug("connect SOA");
                initSOAClient();
                break;
            case REFIND:
                LogUtil.debug("refind gnss service");
                subscribeMessage();
                break;
            case OVERTIME:
                overTimeCount++;
                LogUtil.debug("Timeout " + overTimeCount);
                lastReceivedTime = System.currentTimeMillis();
                mAsyncHandler.removeMessages(OVERTIME);
                if (mLocationBean.getLatitude() == 0) {
                    LogUtil.warn("In overtime case, never received data!");
                } else {
                    if (overTimeCount > 3) {
                        LogUtil.warn("In overtime case, More than 3 consecutive timeouts!");
                    } else {
                        LogUtil.debug("In overtime case, update last location at: " + lastReceivedTime);
                        mLocationBean.setTime(mLocationBean.getTime() + COUNTDOWN_TIME + 1000);
                        try {
                            LogUtil.info("call onLocation callback");
                            if (mExtLocationCallback != null) {
                                mExtLocationCallback.onLocation(mLocationBean.getLocation());
                            } else {
                                LogUtil.debug("callback has not been set!!!");
                            }
                            reportSvStatus();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                sendOverTimeMessage();
                break;
            default:
                break;
        }
    }

    private void reportSvStatus() throws RemoteException {
        SatInfo_t[] svArray = mLocationBean.getSatInfoList();
        if (svArray == null) {
            LogUtil.warn("svArray is null");
        } else {
            int size = svArray.length;
            int[] prnList = new int[size];
            float[] evelationList = new float[size];
            float[] azimuthList = new float[size];
            float[] cn0List = new float[size];
            float[] svCarrierFreqs = new float[size];
            float[] basebandCn0s = new float[size];
            for (int i = 0; i < size; i++) {
                prnList[i] = svArray[i].getPRN() << SVID_SHIFT_WIDTH;
                evelationList[i] = svArray[i].getElevation();
                azimuthList[i] = svArray[i].getAzimuth();
                cn0List[i] = svArray[i].getSNR();
                svCarrierFreqs[i] = 0;
                basebandCn0s[i] = 0;
            }
            if (mExtLocationCallback != null) {
                mExtLocationCallback.reportSvStatus(size,
                        prnList,
                        cn0List,
                        evelationList,
                        azimuthList,
                        svCarrierFreqs,
                        basebandCn0s
                );
            } else {
                LogUtil.debug("callback has not been set!!!");
            }
        }
    }

    private void sendOverTimeMessage() {
        mAsyncHandler.sendEmptyMessageDelayed(OVERTIME, COUNTDOWN_TIME);
    }

    public void onDestroy() {
        LogUtil.debug("on destroy LocationServiceImpl");
    }

    public void onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.debug("onStartCommand() called!");
    }

    @Override
    public void setCallback(IExtLocationCallback callback) throws RemoteException {
        mExtLocationCallback = callback;
    }

    @Override
    public String getGnssHwInfo() throws RemoteException {
        LogUtil.debug("gnss hw info: " + mLocationBean.getManufacturer());
        return mLocationBean.getManufacturer();
    }

    @Override
    public boolean setEnable() throws RemoteException {
        LogUtil.debug("re init tbox gnss service");
        mAsyncHandler.sendEmptyMessage(REFIND);
        return true;
    }

    @Override
    public boolean setDisable() throws RemoteException {
        LogUtil.debug("recycle tbox gnss service");
        if (mExtLocationCallback != null) {
            mExtLocationCallback.onProviderDisabled();
        } else {
            LogUtil.debug("callback has not been set!!!");
        }
        return true;
    }

    @Override
    public boolean isGnssVisibilityControlSupported() throws RemoteException {
        return false;
    }

    @Override
    public void cleanUp() throws RemoteException {
        LogUtil.info("cleanUp");
    }

    @Override
    public void setPositionMode(int mode, int recurrence, int min_interval, int preferred_accuracy,
                                int preferred_time, boolean lowPowerMode) throws RemoteException {
        LogUtil.info("setPositionMode");
    }

    @Override
    public void deleteAidingData() throws RemoteException {
        LogUtil.info("deleteAidingData");
    }

    @Override
    public int readNmea(byte[] buffer, int bufferSize) throws RemoteException {
        LogUtil.info("readNmea");
        return 0;
    }

    @Override
    public void agpsNiMessage(byte[] msg, int length) throws RemoteException {
        LogUtil.info("agpsNiMessage");
    }

    @Override
    public void setAgpsServer(int type, String hostname, int port) throws RemoteException {
        LogUtil.info("setAgpsServer");
    }

    @Override
    public void sendNiResponse(int notificationId, int userResponse) throws RemoteException {
        LogUtil.info("sendNiResponse");
    }

    @Override
    public void agpsSetRefLocationCellid(int type, int mcc, int mnc, int lac, int cid) throws
            RemoteException {
        LogUtil.info("agpsSetRefLocationCellid");
    }

    @Override
    public void agpsSetId(int type, String setid) throws RemoteException {
        LogUtil.info("agpsSetId");
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) {
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(fd));
        dumpInner(fd, printWriter, args);
        printWriter.close();
    }

    private void dumpInner(FileDescriptor fd, PrintWriter printWriter, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        printWriter.println("!!!dump CarLocationService:start\n");

        stringBuilder.append("location update record:\n");
        for (int i = 0; i < timeDumpList.size(); ++i) {
            stringBuilder.append("time:" + timeDumpList.get(i) + ", locationInfo: "
                    + locationInfoDumpList);
        }
        printWriter.println(stringBuilder);

        printWriter.println("\n!!!dump CarLocationService:end\n");
    }
}
