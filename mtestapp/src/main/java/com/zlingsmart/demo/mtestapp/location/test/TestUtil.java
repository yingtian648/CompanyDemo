package com.zlingsmart.demo.mtestapp.location.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author lsh
 * @date 2024/9/10 11:19
 * @description
 */
public class TestUtil {
    private static TestUtil util = new TestUtil();
    public static TestUtil get(){
        return util;
    }
    /**
     * GGA/RMC index value follow International standard field sorting
     */
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
    private static final int GGA_INDEX_DIFFER_STATION_ID = 14;
    private static final int GGA_INDEX_CHECK_VALUE = 15;

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
    private String mLastGga;
    private String mLastRmc;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HHmmss.SSS", Locale.ENGLISH);
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("ddMMyy", Locale.ENGLISH);

    public TestUtil(){
        mTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private GgaNmeaData parseGgaNmeaData(String nmea) {
        String[] fields = nmea.split(",");
        GgaNmeaData gga = new GgaNmeaData();
        gga.mSentence = nmea;
        for (int i = 0; i < fields.length; i++) {
            switch (i) {
                case GGA_INDEX_UTC_TIME:
                    if (Objects.equals(nmea, mLastGga)) {
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
                case GGA_INDEX_DIFFER_STATION_ID:
                    gga.mDifferStationId = fields[i];
                    break;
                case GGA_INDEX_CHECK_VALUE:
                    gga.mCheckValue = fields[i];
                    break;
                default:
                    break;
            }
        }
        return gga;
    }

    public RmcNmeaData parseRmcNmeaData(String nmea) {
        String[] fields = nmea.split(",");
        RmcNmeaData rmc = new RmcNmeaData();
        rmc.mSentence = nmea;
        for (int i = 0; i < fields.length; i++) {
            switch (i) {
                case RMC_INDEX_UTC_TIME:
                    if (Objects.equals(nmea, mLastRmc)) {
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
                    if (Objects.equals(nmea, mLastRmc)) {
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
                    rmc.mCheckValue = fields[i];
                    break;
                default:
                    break;
            }
        }
        return rmc;
    }
}
