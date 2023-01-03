package com.exa.companydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.widget.CarToast;
import android.os.UserHandle;
import android.text.TextUtils;
import android.widget.CarToast;
import android.widget.Toast;

import com.exa.baselib.utils.GpsConvertUtil;
import com.exa.baselib.utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TestUtil {

    /**
     * 测试 Toast
     *
     * @param context
     */
    public static void showToast(Context context) {
        L.d("showToast");
        String msg = "撒谎吉萨号登机口啥叫啊十大建设大家";
        msg = "撒谎吉萨号登机口啥叫啊十大建设大家好刷道具卡啥叫看到啥就大数据的卡斯卡迪肯定会刷卡机打算结婚的卡";
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        CarToast.makeText(context, msg, CarToast.LENGTH_SHORT).show();
    }

    /**
     * 测试 发送广播
     *
     * @param activity
     * @param action
     * @param packageName 接收包名
     */
    public static void sendBroadcast(Activity activity, String action, String packageName) throws Exception {
        L.d("sendBroadcast:" + action);
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("extra_show", "wifi");
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName);//发给指定包名
        }
//        activity.sendBroadcast(intent);
        // UserHandle.ALL  UID = -1
        activity.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(-1));
    }

    /**
     * SystemUI 发送广播
     * public static final void sendAppBroadcast(Context context, String str, String str2) {
     *         Intrinsics.checkNotNullParameter(context, "<this>");
     *         Intrinsics.checkNotNullParameter(str, "pkg");
     *         Intrinsics.checkNotNullParameter(str2, "action");
     *         try {
     *             Logger.info("Context.openOutPanel", "pkg: " + str + " action: " + str2);
     *             Intent intent = new Intent();
     *             intent.setAction(str2);
     *             intent.setPackage(str);
     *             context.sendBroadcastAsUser(intent, UserHandle.ALL);
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *             Logger.info("Context.openOutPanel", String.valueOf(Unit.INSTANCE));
     *         }
     *     }
     */

    // 转换经纬度和UTC时间
    public static void convertGpsAndUtcTimeTest(){
        double w = 102.5962240000;
        double lon = 129.7029650020;
        L.d("convert lat:" + GpsConvertUtil.convertCoordinates(lon));
        L.d("convert1 lat:" + ddmmTodddd1(lon));

        //20220315163333
        int date = 1122022;
        double time = 0.12345;
        L.d("-------------------------");
        long result = GpsConvertUtil.getCurrentTimeZoneTimeMillis(date, time);
        SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        L.d(String.valueOf(result));
        L.d(sdfOut.format(new Date(result)));
        L.d("-------------------------");
        setTime(date, time);
        L.d("-------------------------");
    }

    private static void setTime(int utcDate, double utcTime) {
        long result = 0;
        if (utcDate != 0) {
            String date = String.valueOf(utcDate);
            String time = String.valueOf(utcTime);
            if (time.contains(".")) {
                if (time.indexOf(".") < 6) {
                    StringBuilder addO = new StringBuilder();
                    for (int i = 0; i < (6 - time.indexOf(".")); i++) {
                        addO.append("0");
                    }
                    time = addO + time;
                }
            } else if (time.length() < 6) {
                StringBuilder addO = new StringBuilder();
                for (int i = 0; i < (6 - time.length()); i++) {
                    addO.append("0");
                }
                time = addO + time;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss.SSS", Locale.getDefault());
            SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                Date dateGmt = sdf.parse(date + time);
                if (dateGmt != null) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    calendar.setTime(dateGmt);
                    calendar.setTimeZone(TimeZone.getDefault());
                    sdfOut.format(calendar.getTime());
                    result = calendar.getTimeInMillis();
                    L.d(String.valueOf(result));
                    L.d(sdfOut.format(calendar.getTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param res 2302.4545412/11325.45451212
     * @return
     */
    private static double ddmmTodddd1(double res) {
        if (res == 0.0) {
            return res;
        }
        String resStr = String.valueOf(res);
        if (resStr.indexOf(".") < 2) {
            return res;
        }
        String wmm = resStr.substring(resStr.indexOf(".") - 2);
        String wdd = resStr.substring(0, resStr.indexOf(".") - 2);
        double wmm_a = Double.parseDouble(wmm) / 60;
        return Double.parseDouble(wdd) + wmm_a;
    }
}
