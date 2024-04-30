package com.exa.lsh.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

/**
 * @author Administrator
 */
public class SystemBarUtil {
    private static final String TAG = "SystemBarUtil";
    public static final int INSETS_TYPE_STATUS_BAR = 1;
    public static final int INSETS_TYPE_NAVIGATION_BAR = 2;

    /**
     * 全屏适配
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
            } else {
                L.dd("controller is null");
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void hideStatusBar(Activity activity) {
        delayCheckSystemBarsStatus(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.statusBars());
            } else {
                L.dd("controller is null");
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
    }

    /**
     * 获取可见的系统栏类型
     *
     * @return 0 都不可见，1 状态栏可见，2 导航栏可见，3 状态栏和导航栏都可见
     */
    public static int getShowingInsets(Window window) {
        if (window == null) return 0;
        int resultInsets = 0;
        //状态栏是否可见
        boolean svis = true;
        //导航栏是否可见
        boolean nvis = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets insets = window.getDecorView().getRootWindowInsets();
            if (insets != null) {
                svis = insets.isVisible(WindowInsets.Type.statusBars());
                nvis = insets.isVisible(WindowInsets.Type.navigationBars());
            } else {
                L.w("getShowingInsets RootWindowInsets is null");
            }
        } else {
            int flags = window.getAttributes().flags;
            int sysUiVis = window.getDecorView().getSystemUiVisibility();
            if ((sysUiVis & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0 || (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) {
                svis = false;
            }
            if ((sysUiVis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) {
                nvis = false;
            }
        }
        if (svis) {
            resultInsets |= INSETS_TYPE_STATUS_BAR;
        }
        if (nvis) {
            resultInsets |= INSETS_TYPE_NAVIGATION_BAR;
        }
        L.d("isFullScreen：StatusBar-Showing=" + svis + ",NavigationBar-Showing=" + nvis);
        return resultInsets;
    }

    /**
     * 判断是否全屏
     * 判断状态栏导航栏显示OR隐藏
     */
    public static void delayCheckSystemBarsStatus(Activity activity) {
        activity.getWindow().getDecorView().postDelayed(() -> {
            int showingInsets = getShowingInsets(activity.getWindow());
            L.d("delayCheckSystemBarsStatus：" + showingInsets);
        }, 500);
    }

    public static void hideNavigationBar(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.navigationBars());
            } else {
                L.dd("controller is null");
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            window.getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void registerInsetsListener(Window window) {
        ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView().getRootView(), (v, insets) -> {
            boolean imeState = insets.isVisible(WindowInsetsCompat.Type.ime());
            boolean statusState = insets.isVisible(WindowInsetsCompat.Type.statusBars());
            boolean naviState = insets.isVisible(WindowInsetsCompat.Type.navigationBars());
            L.dd("statusVisible=" + statusState + " naviVisible=" + naviState + " imeVisible=" + imeState);
            return insets;
        });
    }

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
    public static void hideStatusBars(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                // 亮色状态栏
                // controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                // 非亮色状态栏
                // controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            } else {
                L.dd("controller is null");
            }
        } else {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            window.getDecorView().setSystemUiVisibility(option);
        }
    }

    public static void hideOnlyStatusBars(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            assert controller != null;
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
            } else {
                L.dd("controller is null");
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
            } else {
                L.dd("controller is null");
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
     */
    public static void setInvasionSystemBars(Activity activity) {
        L.dd();
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setDecorFitsSystemWindows(false);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                window.setNavigationBarContrastEnforced(false);
            }
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 侵入导航栏和状态栏
     * 适用于Focusable窗口
     * floating-window
     */
    public static void setInvasionSystemBars(Window window) {
        L.dd();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setDecorFitsSystemWindows(false);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                window.setNavigationBarContrastEnforced(false);
            }
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }


    /**
     * 设置非沉浸式
     */
    public static void setInvasionNone(Window window) {
        setInvasionNone(window, 0, 0);
    }

    /**
     * 设置非沉浸式
     */
    public static void setInvasionNone(Window window, int statusColor, int naviColor) {
        L.dd();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true);
            if (statusColor != 0) {
                window.setStatusBarColor(statusColor);
            }
            if (naviColor != 0) {
                window.setNavigationBarColor(naviColor);
            }
        } else {
            window.clearFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                window.setNavigationBarContrastEnforced(false);
            }
        }
    }

    public static void setInvasionStatusBar(Activity activity) {
        L.dd();
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setDecorFitsSystemWindows(false);
            WindowManager.LayoutParams params = window.getAttributes();
            params.setFitInsetsTypes(WindowInsets.Type.systemBars() & ~WindowInsets.Type.statusBars());
            params.setFitInsetsSides(WindowInsets.Side.all() & ~WindowInsets.Side.TOP);
            window.setAttributes(params);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 获取状态栏和导航栏是否可见
     *
     * @return Pair<Boolean, Boolean>  first:状态栏是否隐藏  second:导航栏是否隐藏 true:隐藏  false:显示
     */
    public static Pair<Boolean, Boolean> isSystemUiHide(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // Check if the system UI is visible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets insets = windowManager.getMaximumWindowMetrics().getWindowInsets();
            Insets navi = insets.getInsets(WindowInsets.Type.navigationBars());
            Insets status = insets.getInsets(WindowInsets.Type.statusBars());
            Log.d(TAG, String.format("naviBars:%d statusBars:%d", navi.bottom, status.top));
            Log.d(TAG, "是否隐藏状态栏：" + (status.top == 0) + ",是否隐藏导航栏：" + (navi.bottom == 0));
            return new Pair<>(status.top == 0, navi.bottom == 0);
        }
        return new Pair<>(false, false);
    }
}
