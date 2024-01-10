package com.exa.baselib.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

/**
 * @author Administrator
 */
public class ScreenUtils {

    /**
     * 全屏适配
     *
     * @param activity
     */
    public static void hideStatusBars(Activity activity) {
        L.dd();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                delayCheckSystemBarsStatus(activity);
                // 手机自动隐藏状态栏导航栏
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
//                controller.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                controller.hide(WindowInsets.Type.systemBars());
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void hideStatusBar(Activity activity) {
        delayCheckSystemBarsStatus(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                // 手机自动隐藏状态栏导航栏
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.statusBars());
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void delayCheckSystemBarsStatus(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            activity.getWindow().getDecorView().postDelayed(() -> {
                WindowInsets insets = activity.getWindow().getDecorView().getRootWindowInsets();
                int top = insets.getInsets(WindowInsets.Type.systemBars()).top;
                int bottom = insets.getInsets(WindowInsets.Type.systemBars()).bottom;
                boolean vis = insets.isVisible(WindowInsets.Type.statusBars());
                boolean vis1 = insets.isVisible(WindowInsets.Type.navigationBars());
                L.d("onControllableInsetsChanged：top=" + top + ",bottom=" + bottom + ",status=" + vis + ",navi=" + vis1);
            }, 300);
        } else {
            int flags = activity.getWindow().getAttributes().flags;
            int sysUiVis = activity.getWindow().getDecorView().getSystemUiVisibility();
            L.df("activity arr=%d, sysUiVis=%d", flags, sysUiVis);
            if ((sysUiVis & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0
                    || (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) {
                L.dd("状态栏 已隐藏");
            } else {
                L.dd("状态栏 显示");
            }
            if ((sysUiVis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) {
                L.dd("导航栏 已隐藏");
            } else {
                L.dd("导航栏 显示");
            }
        }
    }

    /**
     * 全屏适配
     */
    public static void hideStatusBars(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                /**
                 * setSystemBarsBehavior 设置状态栏与Window的关系，覆盖在Window上/挤压Window大小来显示SystemUI
                 * 全屏——粘性沉浸模式——上拉下滑显示出半透明SystemUI,SystemUI覆盖在Activity上面，Activity大小不变
                 * SystemUI 会延时隐藏 setOnSystemUiVisibilityChangeListener不回调状态改变
                 * BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                 * 全屏——沉浸模式——上拉下滑显示出SystemUI,SystemUI会挤压Activity高度
                 * BEHAVIOR_SHOW_BARS_BY_TOUCH
                 * 全屏——沉浸模式——上拉下滑显示出SystemUI,SystemUI会挤压Activity高度
                 * BEHAVIOR_SHOW_BARS_BY_SWIPE
                 */
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                // 亮色状态栏
                // controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                // 非亮色状态栏
                // controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            window.getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void hideOnlyStatusBars(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            controller.hide(WindowInsets.Type.statusBars());
        }
    }

    public static void showStatusBars(Activity activity) {
        L.dd();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                delayCheckSystemBarsStatus(activity);
                controller.show(WindowInsets.Type.systemBars());
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_VISIBLE;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void showStatusBars(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.show(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_VISIBLE;
            window.getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void showLightStatusBars(Activity activity, boolean showLightBars) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.show(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                if (showLightBars) {
                    controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                    controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
                } else {
                    controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                    controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
                }
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_VISIBLE;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    /**
     * 侵入导航栏和状态栏
     *
     * @param activity
     */
    public static void setStatusBarAndNavigationBarInvasion(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);
        }
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    public static void setStatusBarInvasion(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }
}
