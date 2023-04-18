package com.exa.baselib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.exa.baselib.bean.AppInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

public class Tools {

    private static Tools sTools;
    private static Application context;

    //获取进程名称
    public static String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return "";
    }

    /**
     * md5加密
     */
    public static String MD5(String params) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(params.getBytes("utf-8"));
            StringBuffer buf = new StringBuffer();
            for (byte b : md.digest()) {
                buf.append(String.format("%02x", b & 0xff));
            }
            return buf.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取APP版本名称
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取系统版本号
    public static String getOsVersion() {//设备系统版本号：7.0.1
        return Build.VERSION.RELEASE;
    }

    public static int getScreenW(Context context) {
        Point point = new Point();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    public static int getScreenH(Context context) {
        Point point = new Point();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    /**
     * 获取double对应精度得值
     *
     * @param value
     * @param newScale
     * @return
     */
    public static double getDoubleAcc(double value, int newScale) {
        return BigDecimal.valueOf(value).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param res
     * @return 双精度的double字符串
     */
    private String getDoubleFormat(double res) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        return df.format(res);
    }

    /**
     * @param res
     * @return 双精度值
     */
    public static String getDoubleFormatStr(double res) {
        String ress = String.valueOf(res);
        if (!ress.contains(".")) {
            return ress;
        } else {
            if (ress.indexOf(".") >= ress.length() - 3) {
                return ress;
            } else {
                return ress.substring(0, ress.indexOf(".") + 3);
            }
        }
    }

    /**
     * 获取dpi 屏幕密度
     *
     * @param context
     * @return
     */
    public static Float getScreenDensity(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().density;
    }

    /**
     * 获取dpi 分辨率
     * 中分辨率，160DPI，也就是1x
     * 高分辨率，240DPI，换算为1.5x
     *
     * @param context
     * @return
     */
    public static int getScreenDensityInt(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * 获取控件的高度
     *
     * @param view
     * @return
     */
    public static int getViewHeight(View view) {
        //获取View的高度
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }

    //JSON字符串转单个对象
    @Nullable
    public static <T> T getResult(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.equals("")) return null;
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonStr, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Tools", "Tool.getResult Format JSON Exception");
        }
        return t;
    }

    //JSON字符串转对象数组
    @Nullable
    public static <T> ArrayList<T> getResultList(String data, Class<T> clazz) {
        if (data == null || data.equals("")) return null;
        ArrayList<T> members = null;
        try {
            members = new ArrayList<>();
            Gson gson = new Gson();
            JsonElement parser = JsonParser.parseString(data);
            JsonArray Jarray = parser.getAsJsonArray();
            for (JsonElement obj : Jarray) {
                T member = gson.fromJson(obj, clazz);
                members.add(member);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Tools", "Tool.getResultList Format JSON Exception");
        }
        return members;
    }

    //JSON字符串转Map
    public static Map getResultMap(String jsonStr) {
        if (jsonStr == null || jsonStr.equals("")) return null;
        Map t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonStr, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Tools", "Tools.getResultMap Err:" + e.getMessage());
        }
        return t;
    }

    //判断为空和空字符串,全空格
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || Pattern.matches("\\s*", str)) {
            return true;
        }
        return false;
    }

    //判断是否有安装APP
    public static boolean isAppInstall(Context context, String appPackageName) {
        if (appPackageName == null) return false;
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(appPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    //生成uuid
    public String createUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static void setScreenLight(Activity activity, int paramInt) {
        try {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    public static void setScreenBrightness(Activity activity, int paramInt) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    // 启动三方app
    public static void startApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent it = packageManager.getLaunchIntentForPackage(packageName);
        if (it != null) {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);
        }
    }

    // 启动App
    public static void startAppByClassName(Context context, String className) {
        Intent intent = new Intent();
        intent.setClassName(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //使用手机默认浏览器加载url
    public static void startMobileBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }

    /**
     * 显示google 订阅情况【进入Google play订阅页面/Google play 订阅web页】
     *
     * @param activity
     * @param productId
     * @param packageName
     */
    public void showGoogleSub(Activity activity, String productId, String packageName) {
        String subDeepLink = "https://play.google.com/store/account/subscriptions";
        if (productId != null && packageName != null) {
            subDeepLink = "https://play.google.com/store/account/subscriptions?sku=" + productId + "&package=" + packageName;
        }
        Uri uri = Uri.parse(subDeepLink);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(it);
    }

    /**
     * @param context
     * @return 是否已开启通知权限
     */
    public boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return isEnableV26(context);
        } else {
            return isEnableV19(context);
        }
    }

    /**
     * Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
     * 针对8.0及以上设备
     *
     * @param context
     * @return
     */
    private static boolean isEnableV26(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
            sServiceField.setAccessible(true);
            Object sService = sServiceField.invoke(notificationManager);

            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            Method method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                    , String.class, Integer.TYPE);
            method.setAccessible(true);
            return (boolean) method.invoke(sService, pkg, uid);
        } catch (Exception e) {
            //sTAG, e);
        }
        return false;
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
     * 19及以上
     *
     * @param context
     * @return
     */
    private static boolean isEnableV19(Context context) {
        AppOpsManager mAppOps = null;

        mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            //sTAG, e);
        } catch (NoSuchMethodException e) {
            //sTAG, e);
        } catch (NoSuchFieldException e) {
            //sTAG, e);
        } catch (InvocationTargetException e) {
            //sTAG, e);
        } catch (IllegalAccessException e) {
            //sTAG, e);
        }

        return false;
    }

    //进入APP 推送设置
    public void gotoNotificationSetting(Activity activity) {
        ApplicationInfo appInfo = activity.getApplicationInfo();
        String pkg = activity.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid);
                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                intent.putExtra("app_package", pkg);
                intent.putExtra("app_uid", uid);
                activity.startActivityForResult(intent, 1);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, 1);
            } else {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivityForResult(intent, 1);
            }
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivityForResult(intent, 1);
            //sTAG, e);
        }
    }

    /**
     * 判断当前应用是否是后台运行
     *
     * @param context
     * @return
     */
    public boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packname = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0之后getRunningTasks废弃
            //当前程序是否前台运行
            //详见ActivityManager.START_TASK_TO_FRONT(hide),前台任务
            int START_TASK_TO_FRONT = 2;
            Field field = null;
            try {
                field = ActivityManager.RunningAppProcessInfo.class.getField("processState");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfoList) {
                if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    //如果当前是运行在前台的UI
                    try {
                        Integer processState = field.getInt(runningAppProcessInfo);
                        if (START_TASK_TO_FRONT == processState) {
                            packname = runningAppProcessInfo.processName;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            //5.0之前可直接使用getRunningTasks
            //<uses-permission android:name="android.permission.GET_TASKS"/>
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
            packname = runningTaskInfoList.get(0).topActivity.getPackageName();
        }
        return !context.getPackageName().equals(packname);
    }

    //获取当前时区
    public String getTimeZone() {
        return TimeZone.getDefault().getID();
    }

    //手机是否root
    public boolean isDeviceRooted() {
        return checkRoot();
    }


    //手机是否root - 方法3
    private static boolean checkRoot() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{
                    "/system/xbin/which", "su"
            });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    //adb是否打开
    public static boolean isAdbOpen(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0;//判断adb调试模式是否打开
    }

    /**
     * 打开默认应用市场的app
     *
     * @param activity
     */
    public static void openAppInMarket(Activity activity) {
        // 做跳转到谷歌play做好评的业务逻辑
        //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + "com.sol.novel")); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(activity.getPackageManager()) != null) { //可以接收
            activity.startActivity(intent);
        } else { //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "com.sol.novel"));
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(activity.getPackageManager()) != null) { //有浏览器
                activity.startActivity(intent);
            } else {
//                Toast.makeText(activity, "You don't have an app market installed, not even a browser!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 打开Google play中的当前应用
     * 未安装Google play则打开默认浏览器 → 打开Google play商店
     *
     * @param activity
     */
    public static void openAppInGoolgePlay(Activity activity) {
        final String appPkg = activity.getPackageName();
        final String googlePlayPg = "com.android.vending";//这里对应的是谷歌商店，跳转别的商店改成对应的即可
        try {
            Intent intent;
            if (isAppInstall(activity, googlePlayPg)) {
                Uri uri = Uri.parse("market://details?id=" + appPkg);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage(googlePlayPg);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "com.sol.novel"));
                //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
                if (intent.resolveActivity(activity.getPackageManager()) != null) { //有浏览器
                    activity.startActivity(intent);
                } else {
//                Toast.makeText(activity, "You don't have an app market installed, not even a browser!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (Exception e) {
            Log.e("Tools", "Lanuch Goolge Play exception");
            e.printStackTrace();
        }
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
     * GenricManager<Book>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or <code>Object.class</code> if cannot be determined
     */
    public static Class getSuperClassGenricType(Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager<Book>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     */
    public static Class getSuperClassGenricType(Class clazz, int index)
            throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    //Draw 获取文本宽度
    public static int getDrawTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
//        L.d("文本（" + str + "）宽度：" + iRet);
        return iRet;
    }

    //Draw 获取文本高度
    public static float getDrawTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        float h = fm.descent - fm.ascent;
        L.d("文本高度：" + h);
        return h;
    }

    //Draw 获取文本高度
    public static float getDrawTextFullHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        float h = fm.bottom - fm.top;
        L.d("文本高度：" + h);
        return h;
    }

    //将文本复制到剪贴板
    public static void copyText(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null, text);
        clip.setPrimaryClip(data);
    }

    /**
     * 校验是否已启动无障碍服务
     *
     * @param context
     * @return
     * @throws RuntimeException
     */
    public static boolean isAccessibilityEnabled(Context context) {
        if (context == null) {
            return false;
        }
        // 检查AccessibilityService是否开启
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        boolean isAccessibilityEnabled_flag = am.isEnabled();
        boolean isExploreByTouchEnabled_flag = false;
        // 检查无障碍服务是否以语音播报的方式开启
        isExploreByTouchEnabled_flag = isScreenReaderActive(context);
        return (isAccessibilityEnabled_flag && isExploreByTouchEnabled_flag);

    }

    private static boolean isScreenReaderActive(Context context) {
        final String SCREEN_READER_INTENT_ACTION = "android.accessibilityservice.AccessibilityService";
        final String SCREEN_READER_INTENT_CATEGORY = "android.accessibilityservice.category.FEEDBACK_SPOKEN";
        // 通过Intent方式判断是否存在以语音播报方式提供服务的Service，还需要判断开启状态
        Intent screenReaderIntent = new Intent(SCREEN_READER_INTENT_ACTION);
        screenReaderIntent.addCategory(SCREEN_READER_INTENT_CATEGORY);
        List<ResolveInfo> screenReaders = context.getPackageManager().queryIntentServices(screenReaderIntent, 0);
        // 如果没有，返回false
        if (screenReaders.size() <= 0) {
            return false;
        }
        boolean hasActiveScreenReader = false;
        if (Build.VERSION.SDK_INT >= 26) {
            // 高版本可以直接判断服务是否处于开启状态
            for (ResolveInfo screenReader : screenReaders) {
                hasActiveScreenReader |= isAccessibilitySettingsOn(context, screenReader.serviceInfo.packageName + "/" + screenReader.serviceInfo.name);
            }

        } else {
            // 判断正在运行的Service里有没有上述存在的Service
            List<String> runningServices = new ArrayList<String>();

            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                runningServices.add(service.service.getPackageName());
            }

            for (ResolveInfo screenReader : screenReaders) {
                if (runningServices.contains(screenReader.serviceInfo.packageName)) {
                    hasActiveScreenReader = true;
                }
            }
        }

        return hasActiveScreenReader;
    }

    // To check if service is enabled
    private static boolean isAccessibilitySettingsOn(Context context, String service) {
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        String settingValue = Settings.Secure.getString(
                context.getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue);
            while (mStringColonSplitter.hasNext()) {
                String accessibilityService = mStringColonSplitter.next();
                if (accessibilityService.equalsIgnoreCase(service)) {
                    return true;
                }
            }
        }
        return false;
    }

    //获取所有包名和对应的App名称
    public static List<AppInfo> getMobileAppsInfo(Context context, boolean isLog) {
        List<AppInfo> infos = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pkgm = context.getPackageManager();
        List<ResolveInfo> apps = pkgm.queryIntentActivities(intent, 0);
        //for循环遍历ResolveInfo对象获取包名和类名
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            int versionCode = 0;
            String versionName = "";
            String apkSourceDir = null;
            String packageName = info.activityInfo.packageName;//包名
            try {
                versionCode = pkgm.getPackageInfo(packageName, 0).versionCode;//包名
                versionName = pkgm.getPackageInfo(packageName, 0).versionName;//包名
                apkSourceDir = pkgm.getApplicationInfo(packageName, 0).sourceDir;//包名
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            CharSequence name = info.activityInfo.loadLabel(context.getPackageManager());//app名称
            if (isLog) {
                L.e(name + "    " + packageName);
            }
            if (name != null && packageName != null)
                infos.add(new AppInfo(name.toString(), packageName, versionCode, versionName, apkSourceDir));
        }
        return infos;
    }

    public static String encryptToSHA(String info) {
        byte[] digesta = null;
        try {
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            alga.update(info.getBytes());
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byte2hex(digesta);
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        if (b != null && b.length > 0)
            for (int n = 0; n < b.length; n++) {
                stmp = (Integer.toHexString(b[n] & 0XFF));
                if (stmp.length() == 1) {
                    hs = hs + "0" + stmp;
                } else {
                    hs = hs + stmp;
                }
            }
        return hs;
    }

    /**
     * 隐藏输入法
     *
     * @param editW
     */
    public static void hideKeyboard(@NonNull EditText editW) {
        editW.clearFocus();
        try {
            InputMethodManager imm = (InputMethodManager) editW.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editW.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("hideKeyboard:" + e.getMessage());
        }
    }

    /**
     * 隐藏输入法
     *
     * @param editW
     */
    public static void showKeyboard(@NonNull EditText editW) {
        try {
            InputMethodManager imm = (InputMethodManager) editW.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editW, 0);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("showKeyboard:" + e.getMessage());
        }
    }

    /**
     * 时间格式化 05:11
     *
     * @param millis
     * @return
     */
    public static String timeFormatMS(long millis) {
        if (millis < 1000) return "00:00";
        long s = millis / 1000;
        if (millis < 1000 * 60) {
            return "00:".concat(s < 10 ? ("0" + s) : String.valueOf(s));
        } else {
            long m = s / 60;
            s = s % 60;
            return (m < 10 ? ("0" + m) : String.valueOf(m)).concat(":").concat(s < 10 ? ("0" + s) : String.valueOf(s));
        }
    }

    /**
     * 获得系统亮度
     *
     * @return
     */
    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 改变App当前Window亮度
     *
     * @param brightness
     */
    public static void changeAppBrightness(Activity context, int brightness) {
        Window window = context.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }


    /**
     * 将activity显示在指定的displayid上
     *
     * @param activity
     * @param displayId
     */
    @RequiresApi(api = Build.VERSION_CODES.R)//android 8.0
    public static void changeDisplayScreen(Activity activity, int displayId, String targetClassName) {
        Display currDisplay = activity.getDisplay();//获取当前显示设备的display
        if (currDisplay == null) {
            L.e("UnKnownException : currDisplay is Null");
            return;
        }
        L.d("currDisplay:" + currDisplay);
        DisplayManager manager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
        Display display = manager.getDisplay(displayId);//
        L.d("TagDisplay:" + (display == null ? "null" : display));
        if (display != null && display.getDisplayId() != currDisplay.getDisplayId()) {
            activity.finish();
            startActivityOnDisplayId(activity, displayId, targetClassName);
        }
    }

    /**
     * 将activity显示在指定的displayid上
     *
     * @param context
     * @param displayId
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startActivityOnDisplayId(Context context, int displayId, String tagClassName) {
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchDisplayId(displayId);        //这里一直display0是第一块屏；display1是第二块屏
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(context.getPackageName(), tagClassName);
        intent.setComponent(cn);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent, options.toBundle());
    }

    /**
     * 呼出电话(直接拨打电话）
     * <uses-permission android:name="android.permission.CALL_PHONE" /> 添加权限
     *
     * @param num
     */
    public static void callPhone(Context context, String num) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + num);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 跳转到拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhonePage(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 转无符号字节
     *
     * @param b
     * @return
     */
    public static short string2UnsignedByte(byte b) {
        return (short) ((short) b & 0xFF);
    }

    /**
     * 转无符号字节数组
     *
     * @param b
     * @return
     */
    public static short[] string2UnsignedBytes(byte[] b) {
        short[] result = new short[b.length];
        for (int i = 0; i < b.length; i++) {
            result[i] = string2UnsignedByte(b[i]);
        }
        return result;
    }

    /**
     * 试探 IP 地址是否可达
     * 注：在子线程使用
     *
     * @param ip
     * @param timeoutMillis
     * @return
     */
    public boolean isIpArrived(String ip, int timeoutMillis) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            boolean isArrived = inetAddress.isReachable(timeoutMillis);
            L.w(ip + "\u3000arrive status:" + isArrived);
            return isArrived;
        } catch (Exception e) {
            e.printStackTrace();
            L.w(ip + "\u3000isIpArrived err" + e.getMessage());
        }
        return false;
    }

    public static void openApp(Context mContext,String packageName) {
        if (packageName != null) {
            try {
                PackageManager packageManager = mContext.getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    mContext.startActivity(intent);
                } else {
                    L.e(String.format("openApp err: has not found %s launcher activity", packageName));
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e("openApp err",e);
            }
        }
    }
}
