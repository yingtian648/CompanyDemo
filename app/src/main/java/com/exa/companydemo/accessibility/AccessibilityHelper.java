package com.exa.companydemo.accessibility;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

import com.exa.baselib.utils.L;

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
     *
     * @param activity
     */
    public static void checkToOpenAccessibilityService(Activity activity) {
        if (!MyAccessibilityService.isStart()) {
            try {
                activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
//                activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                L.de(e);
                e.printStackTrace();
            }
        }
    }
}
