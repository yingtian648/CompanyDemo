package com.exa.companydemo.ford;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * @author lsh
 * @date 2024/6/25 15:15
 * @description
 */
public class NmeaGPRMC implements Parcelable {

    private final String mUtcTime;
    private final String mValidFlag;
    private final String mLatitude;
    private final String mLatDirection;
    private final String mLongitude;
    private final String mLonDirection;
    private final String mSpeed;
    private final String mHeading;
    private final String mUtcDate;
    private final String mMagneticDeclination;
    private final String mDeclinationDirection;
    private final String mModeIndex;
    private final String mCheckValue;

    public NmeaGPRMC(String utcTime, String validFlag, String latitude, String latDirection,
                     String longitude, String lonDirection, String speed, String heading,
                     String utcDate, String magneticDeclination, String declinationDirection,
                     String modeIndex, String checkValue) {
        this.mUtcTime = utcTime;
        this.mValidFlag = validFlag;
        this.mLatitude = latitude;
        this.mLatDirection = latDirection;
        this.mLongitude = longitude;
        this.mLonDirection = lonDirection;
        this.mSpeed = speed;
        this.mHeading = heading;
        this.mUtcDate = utcDate;
        this.mMagneticDeclination = magneticDeclination;
        this.mDeclinationDirection = declinationDirection;
        this.mModeIndex = modeIndex;
        this.mCheckValue = checkValue;
    }

    public String getNMEA(){
        return  "" + System.currentTimeMillis();
    }

    public String getmCheckValue() {
        return mCheckValue;
    }

    public String getmDeclinationDirection() {
        return mDeclinationDirection;
    }

    public String getmHeading() {
        return mHeading;
    }

    public String getmLatDirection() {
        return mLatDirection;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public String getmValidFlag() {
        return mValidFlag;
    }

    public String getmLonDirection() {
        return mLonDirection;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public String getmMagneticDeclination() {
        return mMagneticDeclination;
    }

    public String getmModeIndex() {
        return mModeIndex;
    }

    public String getmSpeed() {
        return mSpeed;
    }

    public String getmUtcDate() {
        return mUtcDate;
    }

    public String getmUtcTime() {
        return mUtcTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(this.mUtcTime);
        parcel.writeString(this.mValidFlag);
        parcel.writeString(this.mLatitude);
        parcel.writeString(this.mLatDirection);
        parcel.writeString(this.mLongitude);
        parcel.writeString(this.mLonDirection);
        parcel.writeString(this.mSpeed);
        parcel.writeString(this.mHeading);
        parcel.writeString(this.mUtcDate);
        parcel.writeString(this.mMagneticDeclination);
        parcel.writeString(this.mDeclinationDirection);
        parcel.writeString(this.mModeIndex);
        parcel.writeString(this.mCheckValue);
    }

    @Override
    public String toString() {
        return "NmeaGPRMC{" +
                "mUtcTime='" + mUtcTime + '\'' +
                ", mValidFlag='" + mValidFlag + '\'' +
                ", mLatitude='" + mLatitude + '\'' +
                ", mLatDirection='" + mLatDirection + '\'' +
                ", mLongitude='" + mLongitude + '\'' +
                ", mLonDirection='" + mLonDirection + '\'' +
                ", mSpeed='" + mSpeed + '\'' +
                ", mHeading='" + mHeading + '\'' +
                ", mUtcDate='" + mUtcDate + '\'' +
                ", mMagneticDeclination='" + mMagneticDeclination + '\'' +
                ", mDeclinationDirection='" + mDeclinationDirection + '\'' +
                ", mModeIndex='" + mModeIndex + '\'' +
                ", mCheckValue='" + mCheckValue + '\'' +
                '}';
    }
}
