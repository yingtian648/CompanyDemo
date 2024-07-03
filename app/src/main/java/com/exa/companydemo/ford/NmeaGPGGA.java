package com.exa.companydemo.ford;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * @author lsh
 * @date 2024/6/25 15:14
 * @description
 */
public class NmeaGPGGA implements Parcelable {
    private final String mUtcTime;
    private final String mLatitude;
    private final String mLatDirection;
    private final String mLongitude;
    private final String mLonDirection;
    private final String mValidFlag;
    private final String mNumSvUsed;
    private final String mHorizontalAccuracy;
    private final String mAntennaHeight;
    private final String mAntennaHeightUnit;
    private final String mGeoidHeight;
    private final String mGeoidHeightUnit;
    private final String mDifferTime;
    private final String mDifferStationId;
    private final String mCheckValue;

    public NmeaGPGGA(String utcTime, String latitude, String latDirection, String longitude,
                     String lonDirection, String validFlag, String numSvUsed,
                     String horizontalAccuracy, String antennaHeight, String antennaHeightUnit,
                     String geoidHeight, String geoidHeightUnit, String differTime,
                     String differStationId, String checkValue) {
        this.mUtcTime = utcTime;
        this.mLatitude = latitude;
        this.mLatDirection = latDirection;
        this.mLongitude = longitude;
        this.mLonDirection = lonDirection;
        this.mValidFlag = validFlag;
        this.mNumSvUsed = numSvUsed;
        this.mHorizontalAccuracy = horizontalAccuracy;
        this.mAntennaHeight = antennaHeight;
        this.mAntennaHeightUnit = antennaHeightUnit;
        this.mGeoidHeight = geoidHeight;
        this.mGeoidHeightUnit = geoidHeightUnit;
        this.mDifferTime = differTime;
        this.mDifferStationId = differStationId;
        this.mCheckValue = checkValue;
    }

    public String getAntennaHeight() {
        return mAntennaHeight;
    }

    public String getAntennaHeightUnit() {
        return mAntennaHeightUnit;
    }

    public String getCheckValue() {
        return mCheckValue;
    }

    public String getDifferStationId() {
        return mDifferStationId;
    }

    public String getDifferTime() {
        return mDifferTime;
    }

    public String getGeoidHeight() {
        return mGeoidHeight;
    }

    public String getGeoidHeightUnit() {
        return mGeoidHeightUnit;
    }

    public String getHorizontalAccuracy() {
        return mHorizontalAccuracy;
    }

    public String getLatDirection() {
        return mLatDirection;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getValidFlag() {
        return mValidFlag;
    }

    public String getLonDirection() {
        return mLonDirection;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getNumSvUsed() {
        return mNumSvUsed;
    }

    public String getUtcTime() {
        return mUtcTime;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(this.mUtcTime);
        parcel.writeString(this.mLatitude);
        parcel.writeString(this.mLatDirection);
        parcel.writeString(this.mLongitude);
        parcel.writeString(this.mLonDirection);
        parcel.writeString(this.mValidFlag);
        parcel.writeString(this.mNumSvUsed);
        parcel.writeString(this.mHorizontalAccuracy);
        parcel.writeString(this.mAntennaHeight);
        parcel.writeString(this.mAntennaHeightUnit);
        parcel.writeString(this.mGeoidHeight);
        parcel.writeString(this.mGeoidHeightUnit);
        parcel.writeString(this.mDifferTime);
        parcel.writeString(this.mDifferStationId);
        parcel.writeString(this.mCheckValue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "NmeaGPGGA{" +
                "mUtcTime='" + mUtcTime + '\'' +
                ", mLatitude='" + mLatitude + '\'' +
                ", mLatDirection='" + mLatDirection + '\'' +
                ", mLongitude='" + mLongitude + '\'' +
                ", mLonDirection='" + mLonDirection + '\'' +
                ", mValidFlag='" + mValidFlag + '\'' +
                ", mNumSvUsed='" + mNumSvUsed + '\'' +
                ", mHorizontalAccuracy='" + mHorizontalAccuracy + '\'' +
                ", mAntennaHeight='" + mAntennaHeight + '\'' +
                ", mAntennaHeightUnit='" + mAntennaHeightUnit + '\'' +
                ", mGeoidHeight='" + mGeoidHeight + '\'' +
                ", mGeoidHeightUnit='" + mGeoidHeightUnit + '\'' +
                ", mDifferTime='" + mDifferTime + '\'' +
                ", mDifferStationId='" + mDifferStationId + '\'' +
                ", mCheckValue='" + mCheckValue + '\'' +
                '}';
    }
}
