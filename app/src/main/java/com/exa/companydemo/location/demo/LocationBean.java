/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.exa.companydemo.location.demo;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import com.exa.baselib.utils.GpsConvertUtil;

public class LocationBean {
    private Location location;
    private int date;
    private long time = System.currentTimeMillis();
    private long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
    private double elapsedRealtimeUncertaintyNanos = 0;
    private double latitude = 0;
    private double longitude = 0;
    private double altitude = 0;
    // 单位 m/s
    private float speed = 0;
    //方向
    private float bearing = 0;
    //水平精度
    private float horizontalAccuracyMeters = 0;
    //垂直精度
    private float verticalAccuracyMeters = 0;
    private float speedAccuracyMetersPerSecond = 0;
    //方向精度
    private float bearingAccuracyDegrees = 0;
    private String manufacturer = "";
    //定位是否有效
    private boolean isValid;
    //卫星总数
    private int satelliteNum;
    //使用卫星个数
    private int satAvailable;
    // 精度因子
    private float HDOP;
    private float PDOP;
    private float VDOP;
    //磁偏角
    private float magDeclination;
    //定位模式
    private int satSystem;
    //卫星列表
    private SatInfo_t[] satInfoList;
    private String provider = LocationManager.GPS_PROVIDER;

    public Location getLocation() {
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setBearing(bearing);
        location.setAltitude(altitude);
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        location.setBearingAccuracyDegrees(bearingAccuracyDegrees);
        location.setElapsedRealtimeNanos(elapsedRealtimeNanos);
        location.setElapsedRealtimeUncertaintyNanos(elapsedRealtimeUncertaintyNanos);
        location.setSpeedAccuracyMetersPerSecond(speedAccuracyMetersPerSecond);
        location.setVerticalAccuracyMeters(verticalAccuracyMeters);
        location.setAccuracy(horizontalAccuracyMeters);
        location.setSpeed(speed);
        location.setProvider(provider);
        location.setTime(time);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isValid", isValid);
        bundle.putInt("satellites", satelliteNum);
        bundle.putInt("satAvailable", (satInfoList==null? 0: satInfoList.length));
        bundle.putInt("m_used_sat", satelliteNum);
        bundle.putFloat("m_hdop", HDOP);
        bundle.putFloat("m_pdop", PDOP);
        bundle.putFloat("m_vdop", VDOP);
        bundle.putFloat("magDeclination", magDeclination);
        bundle.putInt("mode", satSystem);
        location.setExtras(bundle);

        return location;
    }

    public SatInfo_t[] getSatInfoList() {
        return satInfoList;
    }

    public void setSatInfoList(SatInfo_t[] satInfoList) {
        this.satInfoList = satInfoList;
    }

    public int getSatAvailable() {
        return satAvailable;
    }

    public void setSatAvailable(int satAvailable) {
        this.satAvailable = satAvailable;
    }

    public float getHDOP() {
        return HDOP;
    }

    public void setHDOP(float HDOP) {
        this.HDOP = HDOP;
    }

    public float getPDOP() {
        return PDOP;
    }

    public void setPDOP(float PDOP) {
        this.PDOP = PDOP;
    }

    public float getVDOP() {
        return VDOP;
    }

    public void setVDOP(float DDOP) {
        this.VDOP = VDOP;
    }

    public float getMagDeclination() {
        return magDeclination;
    }

    public void setMagDeclination(float magDeclination) {
        this.magDeclination = magDeclination;
    }

    public int getSatSystem() {
        return satSystem;
    }

    public void setSatSystem(int satSystem) {
        this.satSystem = satSystem;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getSatelliteNum() {
        return satelliteNum;
    }

    public void setSatelliteNum(int satelliteNum) {
        this.satelliteNum = satelliteNum;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTime(int date, double time) {
        this.time = GpsConvertUtil.getCurrentTimeZoneTimeMillis(date, time);
    }

    public long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public double getElapsedRealtimeUncertaintyNanos() {
        return elapsedRealtimeUncertaintyNanos;
    }

    public void setElapsedRealtimeUncertaintyNanos(double elapsedRealtimeUncertaintyNanos) {
        this.elapsedRealtimeUncertaintyNanos = elapsedRealtimeUncertaintyNanos;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = GpsConvertUtil.convertCoordinates(latitude / 10000);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = GpsConvertUtil.convertCoordinates(longitude / 10000);
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = (float) (speed / 3.6);
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getHorizontalAccuracyMeters() {
        return horizontalAccuracyMeters;
    }

    public void setHorizontalAccuracyMeters(float horizontalAccuracyMeters) {
        this.horizontalAccuracyMeters = horizontalAccuracyMeters;
    }

    public float getVerticalAccuracyMeters() {
        return verticalAccuracyMeters;
    }

    public void setVerticalAccuracyMeters(float verticalAccuracyMeters) {
        this.verticalAccuracyMeters = verticalAccuracyMeters;
    }

    public float getSpeedAccuracyMetersPerSecond() {
        return speedAccuracyMetersPerSecond;
    }

    public void setSpeedAccuracyMetersPerSecond(float speedAccuracyMetersPerSecond) {
        this.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond;
    }

    public float getBearingAccuracyDegrees() {
        return bearingAccuracyDegrees;
    }

    public void setBearingAccuracyDegrees(float bearingAccuracyDegrees) {
        this.bearingAccuracyDegrees = bearingAccuracyDegrees;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "LocationBean{" +
                "date=" + date +
                ", time=" + time +
                ", elapsedRealtimeNanos=" + elapsedRealtimeNanos +
                ", elapsedRealtimeUncertaintyNanos=" + elapsedRealtimeUncertaintyNanos +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", bearing=" + bearing +
                ", horizontalAccuracyMeters=" + horizontalAccuracyMeters +
                ", verticalAccuracyMeters=" + verticalAccuracyMeters +
                ", speedAccuracyMetersPerSecond=" + speedAccuracyMetersPerSecond +
                ", bearingAccuracyDegrees=" + bearingAccuracyDegrees +
                ", manufacturer=" + manufacturer +
                ", provider='" + provider + '\'' +
                ", isValid='" + isValid + '\'' +
                ", satelliteNum=" + satelliteNum +
                '}';
    }

}
