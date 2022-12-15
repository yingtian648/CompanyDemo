package com.exa.baselib.utils;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * @author Administrator
 */
public class DateUtil {
    public static final String PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_FULL_NUM = "yyyyMMddHHmmss";
    public static final String PATTERN_TIME = "HH:mm:ss";

    public static String getNowDate(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_DATE);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowDateFull(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_TIME);
        return simpleDateFormat.format(new Date());
    }

    public static String getNowDateFullNum(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL_NUM);
        return simpleDateFormat.format(new Date());
    }

    public static long getFullTimeMillis(String fullTime){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_FULL);
        try {
            return simpleDateFormat.parse(fullTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Date getLocalDateFromUtcTime(){
        return new Date();
    }
}
