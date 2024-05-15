package com.exa.companydemo.accessibility;

import android.annotation.SuppressLint;
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
 *
 * 安装python后执行下面命名来安装和启动获取Android应用元素树及元素id
 * 1.安装获取Android app元素的插件
 * pip install weditor
 * 2.打开weditor同步手机界面元素网页
 * python -m weditor
 */
public class AccessibilityHelper {

    /**
     * 启用无障碍服务
     */
    public static void setMyAccessibilityEnable(Context context){
        android.provider.Settings.Secure.putString(context.getContentResolver(),
                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                context.getPackageName() + "/" + MAccessibility.class.getName());
        android.provider.Settings.Secure.putInt(context.getContentResolver(),
                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }

    /**
     * 校验是否已打开无障碍服务
     * 如果未打开 —— 则打开【打不开则打开设置界面】
     */
    public static void startAccessibilitySettingPage(Activity activity) {
        if (!MAccessibility.isStart()) {
            try {
                activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                L.de(e);
            }
        }
    }

    /**
     * 通过广播接收手势操作
     * <p>
     * 测试aab命令
     * adb shell am broadcast -a com.exa.companydemo.accessibility.gesture -e operation bigger
     * adb shell am broadcast -a com.exa.companydemo.accessibility.gesture -e operation smaller
     * adb shell am broadcast -a com.exa.companydemo.accessibility.gesture -e operation right3
     * adb shell am broadcast -a com.exa.companydemo.accessibility.gesture -e operation left3
     */
    private static final String ACTION_ACCESSIBILITY_GESTURE = "com.exa.companydemo.accessibility.gesture";

    private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String opr = intent.getStringExtra("operation");
            L.w("onReceive: " + intent.getAction() + " operation=" + opr);
            assert MAccessibility.isStart() && opr != null;
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

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
