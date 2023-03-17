package com.exa.baselib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

/**
 * 沉侵式
 * 用法： 1. setStatusBarInvasion 2.setStatusBar
 */
public class StatubarUtil {

    //设置沉侵式
    public static void setStatusBarInvasion(Activity activity) {
        if (activity == null) return;
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    //设置沉侵式
    public static void setStatusBarInvasion(Activity activity, boolean isFitsSystemWindows) {
        if (activity == null) return;
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.getDecorView().setFitsSystemWindows(isFitsSystemWindows);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //半透明导航栏，APP的Layout会扩充至屏幕最小端，导航栏绘制到APP的Layout前面
//        window.addFlags(FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 设置状态栏文字黑/白色
     */
    public static void setStatusBar(Activity activity, boolean showLightBar) {
        if (activity == null) return;
        View decorView = activity.getWindow().getDecorView();
        int vis = decorView.getSystemUiVisibility();
        if (showLightBar) {
            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decorView.setSystemUiVisibility(vis);
    }

    //设置沉侵式
    public static void setStatusBarInvasion(Activity activity, Window window) {
        if (activity == null) return;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 设置状态栏文字黑/白色
     */
    public static void setStatusBar(Activity activity, Window window, boolean showLightBar) {
        if (activity == null) return;
        View decorView = window.getDecorView();
        int vis = decorView.getSystemUiVisibility();
        if (showLightBar) {
            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decorView.setSystemUiVisibility(vis);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity
     * @param colorResourceId
     */
    public static void setStatusBarBgColor(Activity activity, int colorResourceId) {
        if (activity == null) return;
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(colorResourceId, activity.getTheme()));
    }

    /**
     * 设置导航栏颜色
     *
     * @param activity
     */
    public static void setNavigationBarColorSingle(Activity activity, int colorId) {
        if (activity == null) return;
        activity.getWindow().setNavigationBarColor(activity.getResources().getColor(colorId, activity.getTheme()));
    }

    /**
     * 设置导航栏颜色
     *
     * @param activity
     */
    public static void setNavigationBarColor(Activity activity, int colorId) {
        if (activity == null) return;
        L.dd("color:" + activity.getResources().getColor(colorId, activity.getTheme()));
        Window window = activity.getWindow();
        L.dd("window.FLAG_TRANSLUCENT_NAVIGATION:" + ((window.getAttributes().flags & FLAG_TRANSLUCENT_NAVIGATION) != 0));
        L.dd("window.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS:" + ((window.getAttributes().flags & FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) == 0));

        if (Build.VERSION.SDK_INT >= 29) {
            window.setNavigationBarContrastEnforced(false);
            L.dd("isNavigationBarContrastEnforced:" + window.isNavigationBarContrastEnforced());
            L.dd("----" + (window.isNavigationBarContrastEnforced() && Color.alpha(activity.getResources().getColor(colorId, activity.getTheme())) == 0));
        }
        window.setNavigationBarColor(activity.getResources().getColor(colorId, activity.getTheme()));
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
