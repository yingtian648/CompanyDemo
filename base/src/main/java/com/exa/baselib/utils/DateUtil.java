package com.exa.baselib.utils;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * @author Administrator
 */
public class DateUtil {
    public static final String PATTERN_FULL_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_FULL_NUM = "yyyyMMddHHmmss";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_TIME_NUM = "HHmmss";
    public static final String PATTERN_TIME_HM = "HH:mm";

    public static String getNowDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_DATE);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowDateFull() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowDateHM() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_TIME_HM);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowDateFullMs() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL_MS);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_TIME);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowDateFullNum() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL_NUM);
        return simpleDateFormat.format(new Date());
    }

    public static String getFullTime(long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL);
        return simpleDateFormat.format(new Date(time));
    }

    public static String getNowTimeNum() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_TIME_NUM);
        return simpleDateFormat.format(new Date());
    }

    public static String getFullSTime(long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL_MS);
        return simpleDateFormat.format(new Date(time));
    }

    public static long getFullTimeMillis(String fullTime) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL);
        try {
            return simpleDateFormat.parse(fullTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getTimeStr(long time) {
        long s = time / 1000;
        if (s < 60) {
            return s + "s";
        } else if (s < 60 * 60) {
            return s / 60 + ":" + s % 60;
        } else if (s < 60 * 60 * 24) {
            long h = s / 3600;
            long min = s % 3600 / 60;
            return h + ":" + min + ":" + s % 3600 % 60;
        } else {
            long last = s % (60 * 60 * 24);
            long h = last / 3600;
            long min = last % 3600 / 60;
            return s/(60 * 60 * 24) + "天" + h + ":" + min + ":" + last % 3600 % 60;
        }

    }

    public static Date getLocalDateFromUtcTime() {
        return new Date();
    }
}
