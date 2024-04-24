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
import java.text.SimpleDateFormat;
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
     * int date = 150322; 20220315
     * double time = 3333.11;
     *
     * @param utcDate GMT ddMMyyyy
     * @param utcTime GMT hhmmss.SSS
     */
    public static long getTimeZoneMillis(int utcDate, double utcTime) {
        long result = System.currentTimeMillis();
        if (utcDate > 0 && utcTime >= 0) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("ddMMyyHHmmss");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                String date = String.format("%06d", utcDate);
                String time = String.format("%06d", (int) utcTime);
                result = format.parse(date + time).getTime();
            } catch (Exception e) {
                L.w("time parse err!!! utcDate,utcTime=" + utcDate + "," + utcTime);
            }
        }
        return result;
    }
}
