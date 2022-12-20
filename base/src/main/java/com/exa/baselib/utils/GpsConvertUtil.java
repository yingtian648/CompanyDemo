/*
 * Copyright (C) 2022 SYNCORE AUTOTECH
 *
 * All Rights Reserved by SYNCORE AUTOTECH Co., Ltd and its affiliates.
 * You may not use, copy, distribute, modify, transmit in any form this file
 * except in compliance with SYNCORE AUTOTECH in writing by applicable law.
 *
 */

package com.exa.baselib.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
            return wdd + wmm;
        } else {
            return res;
        }
    }

    /**
     * GMT date convert to current TimeZone date
     * <p>
     * GMT time
     * int date = 15032022;
     * double time = 3333.11;
     *
     * @param utcDate GMT ddMMyyyy
     * @param utcTime GMT hhmmss.SSS
     */
    public static long getCurrentTimeZoneTimeMillis(int utcDate, double utcTime) {
        long result = 0;
        if (utcDate != 0 && utcTime >= 0) {
            String key = ".";
            String def0 = "0";
            int timeHeadLen = 6;
            String date = String.valueOf(utcDate);
            String time = String.valueOf(utcTime);
            if (date.length() < 7) {
                return result;
            }
            if (date.length() == 7) {
                date = def0 + date;
            }
            if (time.contains(key)) {
                if (time.indexOf(key) < timeHeadLen) {
                    StringBuilder addO = new StringBuilder();
                    for (int i = 0; i < (timeHeadLen - time.indexOf(key)); i++) {
                        addO.append(def0);
                    }
                    time = addO + time;
                }
            } else if (time.length() < timeHeadLen) {
                StringBuilder addO = new StringBuilder();
                for (int i = 0; i < (timeHeadLen - time.length()); i++) {
                    addO.append(def0);
                }
                time = addO + time;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss.SSS",Locale.getDefault());
            try {
                Date dateGmt = sdf.parse(date + time);
                if (dateGmt != null) {
                    result = dateGmt.getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
