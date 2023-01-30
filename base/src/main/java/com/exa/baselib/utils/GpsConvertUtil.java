/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.exa.baselib.utils;

import java.util.Calendar;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GpsConvertUtil {
    /**
     * ddmm.mmmm to dd.dddd
     *
     * @param res 2302.4545412/11325.45451212
     * @return
     */
    public static double convertCoordinates(double res) {
        if (res != 0) {
            int wdd = (int) (res / 100);
            double wmm = res % 100 / 60;
            return getDoubleAcc(wdd + wmm,7);
        } else {
            return res;
        }
    }

    /**
     * Get the corresponding precision value
     * @param value
     * @param newScale target digits
     * @return
     */
    public static double getDoubleAcc(double value,int newScale) {
        return BigDecimal.valueOf(value).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * GMT date convert to current TimeZone date
     * <p>
     * GMT time
     * int date = 150322; 20220315
     * double time = 3333.11;
     *
     * @param utcDate GMT ddMMyyyy
     * @param utcTime GMT hhmmss.SSS
     */
    public static long getCurrentTimeZoneTimeMillis(int utcDate, double utcTime) {
        long result = System.currentTimeMillis();
        if (utcDate > 0 && utcTime >= 0) {
            String date = String.valueOf(utcDate);
            String time = String.valueOf(utcTime);
            if (time.contains(".")) {
                time = time.substring(0, time.indexOf("."));
            }
            try {
                Calendar calendar = Calendar.getInstance();
                int year = 2000 + Integer.parseInt(date.substring(date.length() - 2));
                int mon = Integer.parseInt(date.substring(date.length() - 4, date.length() - 2));
                int day = Integer.parseInt(date.substring(0, date.length() - 4));
                int hh = Integer.parseInt(time.substring(0, time.length() - 4));
                int mm = Integer.parseInt(time.substring(time.length() - 4, time.length() - 2));
                int ss = Integer.parseInt(time.substring(time.length() - 2));
                calendar.set(year, mon - 1, day, hh, mm, ss);
                result = calendar.getTimeInMillis();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.warn("getCurrentTimeZoneTimeMillis time parse err!!!");
            }
        }
        return result;
    }
}
