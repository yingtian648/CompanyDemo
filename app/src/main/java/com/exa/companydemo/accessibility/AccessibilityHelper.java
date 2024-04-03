package com.exa.companydemo.accessibility;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.provider.Settings;
import android.view.Display;

import com.exa.baselib.utils.L;
import com.exa.companydemo.accessibility.util.GestureUtils;

/**
 * @author lsh
 * @date 2021-9-13 14:49
 * 无障碍服务帮助类
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

    /**
     * 通过广播接收手势操作
     * <p>
     * 测试aab命令
     * adb shell am broadcast -a com.exa.companydemo.accessibility.gesture -e operation bigger
     */
    private static final String ACTION_ACCESSIBILITY_GESTURE = "com.exa.companydemo.accessibility.gesture";

    private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String opr = intent.getStringExtra("operation");
            L.w("onReceive: " + intent.getAction() + " operation=" + opr);
            assert MAccessibility.isStart();
            switch (opr) {
                case "bigger":
                    GestureUtils.INSTANCE.scaleInCenter(MAccessibility.service, true);
                    break;
                case "smaller":
                    GestureUtils.INSTANCE.scaleInCenter(MAccessibility.service, false);
                    break;
                case "left3":
                    GestureUtils.INSTANCE.swipeWith3Points(MAccessibility.service,
                            getDisplay(context, false), true);
                    break;
                case "right3":
                    GestureUtils.INSTANCE.swipeWith3Points(MAccessibility.service,
                            getDisplay(context, true), false);
                    break;
            }
        }
    };

    public static void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ACCESSIBILITY_GESTURE);
        context.registerReceiver(mReceiver, filter);
    }

    public static void unRegisterReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }

    private static Display getDisplay(Context context, Boolean isDefaultDisplay) {
        if (isDefaultDisplay) {
            return context.getSystemService(DisplayManager.class)
                    .getDisplay(Display.DEFAULT_DISPLAY);
        } else {
            return context.getSystemService(DisplayManager.class).getDisplay(2);
        }
    }
}
