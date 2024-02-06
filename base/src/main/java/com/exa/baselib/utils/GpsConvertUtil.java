/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.exa.baselib.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.TimeZone;

public class GpsConvertUtil {
    /**
     * ddmm.mmmm to dd.dddd
     *
     * @param res 2302.4545412/11325.45451212
     * @return
     */
    public static double convertCoordinates(double res) {
        res = res * 100;
        if (res != 0) {
            int wdd = (int) (res / 100);
            double wmm = res % 100 / 60;
            L.dd(res / 100 + " wdd=" + wdd + ", wmm=" + wmm);
            return wdd + wmm;
        } else {
            return res;
        }
    }

    /**
     * dd.dddd to ddmm.mmmm
     *
     * @param res 2302.4545412/11325.45451212
     * @return
     */
    public static double unConvertCoordinates(double res) {
        String splitTag = ".";
        if (res != 0) {
            String temp = String.valueOf(res);
            L.dd("temp=" + temp);
            if (temp.contains(splitTag)) {
                int head = Integer.parseInt(temp.substring(0, temp.indexOf(splitTag)));
                double last = Double.parseDouble("0."
                        + temp.substring(temp.indexOf(splitTag) + 1));
                last = last * 60 / 100;
                L.dd("head=" + head + " last=" + last);
                return head + last;
            }
        }
        return res;
    }

    /**
     * Get the corresponding precision value
     *
     * @param value
     * @param newScale target digits
     * @return
     */
    public static double getDoubleAcc(double value, int newScale) {
        return BigDecimal.valueOf(value).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * GMT date convert to current TimeZone date
     * <p>
     * GMT time
     * int date = 150322; 211123
     * double time = 3333.11;
     *
     * @param utcDate GMT ddMMyy
     * @param utcTime GMT hhmmss.SSS
     */
    public static long getCurrentTimeZoneTimeMillis(int utcDate, double utcTime) {
        final String split = ".";
        final String nor0 = "0";
        final int timeMaxLength = 6;
        long result = System.currentTimeMillis();
        if (utcDate > 0 && utcTime >= 0) {
            String date = String.valueOf(utcDate);
            StringBuilder time = new StringBuilder(String.valueOf(utcTime));
            if (time.toString().contains(split)) {
                time = new StringBuilder(time.substring(0, time.indexOf(split)));
            }
            while (time.length() < timeMaxLength) {
                time.insert(0, nor0);
            }
            try {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                int year = 2000 + Integer.parseInt(date.substring(date.length() - 2));
                int mon = Integer.parseInt(date.substring(date.length() - 4, date.length() - 2));
                int day = Integer.parseInt(date.substring(0, date.length() - 4));
                int hh = Integer.parseInt(time.substring(0, time.length() - 4));
                int mm = Integer.parseInt(time.substring(time.length() - 4, time.length() - 2));
                int ss = Integer.parseInt(time.substring(time.length() - 2));
                calendar.set(year, mon - 1, day, hh, mm, ss);
                L.d("hh=" + hh + ",mm=" + mm + ",ss=" + ss);
                result = calendar.getTimeInMillis();
            } catch (Exception e) {
                e.printStackTrace();
                L.w("getCurrentTimeZoneTimeMillis time parse err!!! utcDate,utcTime="
                        + utcDate + "," + utcTime);
            }
        }
        return result;
    }
}
