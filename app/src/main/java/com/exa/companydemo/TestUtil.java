package com.exa.companydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.CarToast;
import android.widget.Toast;

import com.exa.baselib.utils.L;

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
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        CarToast.makeText(context, msg, CarToast.LENGTH_SHORT).show();
    }

    /**
     * 测试 发送广播
     *
     * @param activity
     */
    public static void sendBroadcast(Activity activity,String action) throws Exception {
        L.d("sendBroadcast:" + action);
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("extra_show","wifi");
        intent.putExtra("visible",true);
        activity.sendBroadcast(intent);
    }
}
