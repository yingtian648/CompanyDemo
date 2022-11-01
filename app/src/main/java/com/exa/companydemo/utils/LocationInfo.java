package com.exa.companydemo.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class LocationInfo {
    public byte gNSSValidFlag;
    public double longitude;
    public double latitude;
    public double altitude;
    public float speed;
    public float spdAccuracy;
    public float direction;
    public byte satAvailable;
    public float hDOP;
    public float pDOP;
    public float vDOP;
    public double uTCTime;
    public int uTCDate;
    public float magDeclination;
    public byte directionofMagDec;
    public float horizontalAccuracy;
    public float verticalAccuracy;
    public float bearingAccuracy;
    public byte satSystem;
    public byte satVisible;
    public SatInfo_t[] satInfoList;

    public LocationInfo() {
    }

    public byte getGNSSValidFlag() {
        return this.gNSSValidFlag;
    }

    public void setGNSSValidFlag(byte var1) {
        this.gNSSValidFlag = var1;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double var1) {
        this.longitude = var1;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double var1) {
        this.latitude = var1;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public void setAltitude(double var1) {
        this.altitude = var1;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float var1) {
        this.speed = var1;
    }

    public float getSpdAccuracy() {
        return this.spdAccuracy;
    }

    public void setSpdAccuracy(float var1) {
        this.spdAccuracy = var1;
    }

    public float getDirection() {
        return this.direction;
    }

    public void setDirection(float var1) {
        this.direction = var1;
    }

    public byte getSatAvailable() {
        return this.satAvailable;
    }

    public void setSatAvailable(byte var1) {
        this.satAvailable = var1;
    }

    public float getHDOP() {
        return this.hDOP;
    }

    public void setHDOP(float var1) {
        this.hDOP = var1;
    }

    public float getPDOP() {
        return this.pDOP;
    }

    public void setPDOP(float var1) {
        this.pDOP = var1;
    }

    public float getVDOP() {
        return this.vDOP;
    }

    public void setVDOP(float var1) {
        this.vDOP = var1;
    }

    public double getUTCTime() {
        return this.uTCTime;
    }

    public void setUTCTime(double var1) {
        this.uTCTime = var1;
    }

    public int getUTCDate() {
        return this.uTCDate;
    }

    public void setUTCDate(int var1) {
        this.uTCDate = var1;
    }

    public float getMagDeclination() {
        return this.magDeclination;
    }

    public void setMagDeclination(float var1) {
        this.magDeclination = var1;
    }

    public byte getDirectionofMagDec() {
        return this.directionofMagDec;
    }

    public void setDirectionofMagDec(byte var1) {
        this.directionofMagDec = var1;
    }

    public float getHorizontalAccuracy() {
        return this.horizontalAccuracy;
    }

    public void setHorizontalAccuracy(float var1) {
        this.horizontalAccuracy = var1;
    }

    public float getVerticalAccuracy() {
        return this.verticalAccuracy;
    }

    public void setVerticalAccuracy(float var1) {
        this.verticalAccuracy = var1;
    }

    public float getBearingAccuracy() {
        return this.bearingAccuracy;
    }

    public void setBearingAccuracy(float var1) {
        this.bearingAccuracy = var1;
    }

    public byte getSatSystem() {
        return this.satSystem;
    }

    public void setSatSystem(byte var1) {
        this.satSystem = var1;
    }

    public byte getSatVisible() {
        return this.satVisible;
    }

    public void setSatVisible(byte var1) {
        this.satVisible = var1;
    }

    public SatInfo_t[] getSatInfoList() {
        return this.satInfoList;
    }

    public void setSatInfoList(SatInfo_t[] var1) {
        this.satInfoList = var1;
    }
}

