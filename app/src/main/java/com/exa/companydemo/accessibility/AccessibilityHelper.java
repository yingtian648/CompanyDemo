package com.exa.companydemo.accessibility;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;

import com.exa.baselib.utils.L;
import com.exa.companydemo.accessibility.util.GestureUtils;

/**
 * @作者 Liushihua
 * @创建日志 2021-9-13 14:49
 * @描述
 */
public class AccessibilityHelper {
    /**
     * 安装python后执行下面命名来安装和启动获取Android应用元素树及元素id
     * 1.安装获取Android app元素的插件
     * pip install weditor
     * 2.打开weditor同步手机界面元素网页
     * python -m weditor
     */

    /**
     * 校验是否已打开无障碍服务
     * 如果未打开 —— 则打开【打不开则打开设置界面】
     */
    public static void checkToOpenAccessibility(Activity activity) {
        if (!MAccessibility.isStart()) {
            try {
                activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
//                activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                L.de(e);
                e.printStackTrace();
            }
        }
    }

    private static final String ACTION_SCALE_IN_CENTER = "com.exa.companydemo.accessibility.scaleInCenter";

    private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.w("onReceive: " + intent.getAction());
            assert MAccessibility.isStart();
            switch (intent.getAction()) {
                case ACTION_SCALE_IN_CENTER:
                    GestureUtils.INSTANCE.scaleAtScreenCenter(MAccessibility.service, 200);
                    break;
            }
        }
    };

    public static void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SCALE_IN_CENTER);
        context.registerReceiver(mReceiver, filter);
    }

    public static void unRegisterReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }
}
