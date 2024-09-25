package com.exa.companydemo.ford.test;

import android.annotation.TestApi;

import com.exa.baselib.utils.L;
import com.exa.companydemo.App;
import com.exa.companydemo.ford.FordCarLocationService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import vendor.zlsmart.gnssext.V1_0.CaliStatus;
import vendor.zlsmart.gnssext.V1_0.CompassDir;
import vendor.zlsmart.gnssext.V1_0.Coordinates;
import vendor.zlsmart.gnssext.V1_0.FaultInfo;
import vendor.zlsmart.gnssext.V1_0.FixDimension;
import vendor.zlsmart.gnssext.V1_0.GnssExtData;
import vendor.zlsmart.gnssext.V1_0.TechMaskBits;

/**
 * @author lsh
 * @date 2024/9/10 11:19
 * @description
 */
public class FordTest {
    private static final FordTest util = new FordTest();

    public static FordTest get() {
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
    private String mLastGga;
    private String mLastRmc;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HHmmss.SSS", Locale.ENGLISH);
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("ddMMyy", Locale.ENGLISH);

    public FordTest() {
        mTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void test() {
        testUpdate();
    }

    public void testParseMethod(){
        String gga = "$GPGGA,073644.00,3204.787249,N,11846.786558,E,6,00,38.8,0.8,M,5.2,M,,*6C";
        String rmc = "$GNRMC,070823.116,V,3032.5386,N,10403.6020,E,0.000,0.00,100924,,,N,V*2C";
        GgaNmeaData ggaNmeaData = parseGgaNmeaData(gga);
        RmcNmeaData rmcNmeaData = parseRmcNmeaData(rmc);
        L.dd("publish gga: " + ggaNmeaData.mSentence);
        L.dd("publish rmc: " + rmcNmeaData.mSentence);
        L.dd("gga.getCheckValue: " + getCheckValue(ggaNmeaData.mSentence));
        L.dd("rmc.getCheckValue: " + getCheckValue(rmcNmeaData.mSentence));
        mLastGga = gga;
        mLastRmc = rmc;
    }

    public void testUpdate() {
        L.dd();
        FordCarLocationService mCarLocationService = new FordCarLocationService(App.getContext());
        GnssExtData extData = new GnssExtData();
        // 2024-01-01 00:00:00
        extData.UTC = 1704038400000L;
        Coordinates coordFinal = new Coordinates();
        // beijing university point
        coordFinal.latitude = 39.992100;
        coordFinal.longitude = 116.313256900;
        extData.coordFinal = coordFinal;
        Coordinates coordRaw = new Coordinates();
        // beijing university point
        coordRaw.latitude = 39.992188;
        coordRaw.longitude = 116.313256949;
        extData.coordRaw = coordRaw;
        extData.altitude = 1.0;
        extData.dataGoodToUse = true;
        FaultInfo faultInfo = new FaultInfo();
        faultInfo.gyro = true;
        extData.faultInfo = faultInfo;
        extData.pDop = 1.0F;
        extData.hDop = 2.0F;
        extData.vDop = 3.0F;
        extData.velocity = 10.0F;
        extData.heading = 90.0F;
        extData.compassDir = CompassDir.W;
        extData.accelCaliStatus = CaliStatus.GOOD;
        extData.gyroCaliStatus = CaliStatus.FAULT;
        extData.fixDimension = FixDimension.DIMENSION_3D;
        extData.techMask = TechMaskBits.DR_GNSS;
        extData.numGpsSvUsed = 5;
        extData.numGloSvUsed = 6;
        extData.numGalSvUsed = 7;
        extData.numBdsSvUsed = 8;
        try {
            Field field = FordCarLocationService.class.getDeclaredField("mLocationStorage");
            field.setAccessible(true);
            // 获取属性的类型
            Class<?> type = field.getType();
            // 获取方法名对应的Method对象
            Method method = type.getMethod("update", GnssExtData.class, double.class, double.class);
            // 调用方法
            method.invoke(field.get(mCarLocationService), extData, 39.992188, 116.313256949);
        } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            L.w("testUpdate error:" + e);
        }
    }

    private GgaNmeaData parseGgaNmeaData(String nmea) {
        final boolean isLastKnown = Objects.equals(nmea, mLastGga);
        String[] fields = nmea.split(",");
        L.dd(fields.length);
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
        L.dd(fields.length);
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
     *
     * @return checkvalue contains *
     */
    private String getCheckValue(String nmea) {
        if (nmea == null || !nmea.contains("$") || !nmea.contains("*")) {
            return "*0";
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
}
