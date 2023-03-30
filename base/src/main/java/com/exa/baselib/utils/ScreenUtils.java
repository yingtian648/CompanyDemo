package com.exa.baselib.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getDecorView().getWindowInsetsController();
            if (controller != null) {
                // 手机自动隐藏状态栏导航栏
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
//                controller.addOnControllableInsetsChangedListener(new WindowInsetsController.OnControllableInsetsChangedListener() {
//                    @Override
//                    public void onControllableInsetsChanged(@NonNull WindowInsetsController controller, int typeMask) {
//                        L.d("onControllableInsetsChanged：setFullScreen");
//                    }
//                });
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    /**
     * 全屏适配
     */
    public static void hideStatusBars(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getDecorView().getWindowInsetsController();
            if (controller != null) {
                // 手机自动隐藏状态栏导航栏
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            window.getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void showStatusBars(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getDecorView().getWindowInsetsController();
            if (controller != null) {
                controller.show(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
//                controller.addOnControllableInsetsChangedListener(new WindowInsetsController.OnControllableInsetsChangedListener() {
//                    @Override
//                    public void onControllableInsetsChanged(@NonNull WindowInsetsController controller, int typeMask) {
//                        L.d("onControllableInsetsChanged：showStatusBars");
//                    }
//                });
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_VISIBLE;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void showStatusBars(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getDecorView().getWindowInsetsController();
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
            WindowInsetsController controller = activity.getWindow().getDecorView().getWindowInsetsController();
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
}
