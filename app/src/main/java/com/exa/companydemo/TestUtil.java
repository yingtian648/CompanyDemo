package com.exa.companydemo;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IInterceptor;
import android.app.PendingIntent;
import android.app.StartupInterceptor;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.RenderNode;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaSession2;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.FileUtils;
import com.exa.baselib.utils.GpsConvertUtil;
import com.exa.baselib.utils.L;
import com.exa.companydemo.utils.LogTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import gxa.car.hardkey.HardKeyPolicyManager;
import gxa.car.hardkey.KeyEventCallback;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static com.exa.baselib.utils.L.TAG;

public class TestUtil {
    private static final String TAG = "TestUtil";
    private static Toast toast;
    private static int index = 0;
    private static final String VR_RES_PATH = "/vendor/etc/data/version.txt";
    private static final String VR_RES_VERSION_NAME = "versionCode";

    public static String timeUpdate(String time) {
        L.dd("getUtcTime = " + getUtcTime());
        L.dd("getUtcDate = " + getUtcDate());
        return time;
    }

    public static String getUtcTime() {
        SimpleDateFormat format = new SimpleDateFormat("HHmmss.SSS", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date());
    }

    public static String getUtcDate() {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date());
    }

    public static void readBytes() {
        byte[] bytes = {32, 75, 48, 49, 53, 83, 71, 51, 52, 53, 49, 82, 55, 50, 54, 48, 50, 52, 54, 48, 70};
        String value = new String(bytes);
        L.dd("result:" + value + ", length=" + value.length());
    }

    private String getVRResVersion() {
        String vrVersion = "";
        File file = new File(VR_RES_PATH);
        if (!file.isFile() || !file.exists()) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            String vrJson = sb.toString();
            if (TextUtils.isEmpty(vrJson)) {
                Log.e(TAG, "vr version is empty");
                return vrVersion;
            }
            JSONArray jsonArray = new JSONArray(vrJson);
            int length = jsonArray.length();
            if (length > 0) {
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                vrVersion = jsonObject.optString(VR_RES_VERSION_NAME);
            }
        } catch (IOException | JSONException e) {
            Log.w(TAG, "getVRResVersion err", e);
        }
        return vrVersion;
    }

    public static void doSurfaceViewAnimation(Context context, FrameLayout frame) {
        SurfaceView surfaceView = new SurfaceView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(params);
        frame.addView(surfaceView);
        Method method = null;
        try {
            method = SurfaceView.class.getMethod("setUseAlpha");
            method.invoke(surfaceView);
        } catch (NoSuchMethodException | RuntimeException | IllegalAccessException |
                 InvocationTargetException e) {
            L.w("doSurfaceViewAnimation setUseAlpha err");
        }
        // 加载要显示的图片资源
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.win_bg);
        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Canvas canvas = holder.lockHardwareCanvas();
                canvas.drawBitmap(bitmap, 0f, 0f, null);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

        frame.postDelayed(() -> {
            doAnimation(surfaceView);
        }, 3000);
    }

    private static void doAnimation(SurfaceView view) {
        L.dd();
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f) {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                super.applyTransformation(interpolatedTime, t);
                L.dd(" interpolatedTime=" + interpolatedTime);
                final float alpha = 1.0f;
                final float target = alpha + ((0.0f - alpha) * interpolatedTime);
                view.setAlpha(target);
            }
        };
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillBefore(true);
        AnimationSet animatorSet = new AnimationSet(true);

        animatorSet.addAnimation(alphaAnimation);
//        animatorSet.addAnimation(scaleAnimation);
        view.startAnimation(animatorSet);
    }

    public static class MKeyEventCallback implements KeyEventCallback {
        private String tag;

        public MKeyEventCallback(String tag) {
            this.tag = tag;
        }

        @Override
        public void onKeyEvent(KeyEvent keyEvent, int sceneType, int targetDisplay) {
            String msg = tag + ", " + keyEvent.toString();
            L.dd(msg);
            Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onKeyLongPress(KeyEvent keyEvent, int sceneType, int targetDisplay) {
            String msg = tag + ", " + keyEvent.toString();
            L.dd(msg);
            Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void hardKeyTest(Context context) {
        L.dd();
        HardKeyPolicyManager hardKeyPolicyManager = HardKeyPolicyManager.getInstance(context);
//        hardKeyPolicyManager.addKeyEventCallBack(
//           new MKeyEventCallback("SCENE_AUTO_PARK"),
//            HardKeyPolicyManager.SCENE_AUTO_PARK
//        )
//        hardKeyPolicyManager.addKeyEventCallBack(
//           new MKeyEventCallback("SCENE_AUTO_PARK"),
//            HardKeyPolicyManager.SCENE_AUTO_PARK
//        )
        hardKeyPolicyManager.addKeyEventCallBack(new MKeyEventCallback("SCENE_CARPLAY_MEDIA_CTRL"), HardKeyPolicyManager.SCENE_CARPLAY_MEDIA_CTRL);
    }

    public static void mockHardKeyEvent(Context context) {
        KeyEvent keyEvent = new KeyEvent(System.currentTimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, 521, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY, InputDevice.SOURCE_KEYBOARD);
//        keyEvent.displayId = 0
//        InputManager.getInstance().injectInputEvent(keyEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        HardKeyPolicyManager.getInstance(context).processHardKeyNoPolicy(keyEvent, HardKeyPolicyManager.SCENE_AVM, false);
    }


//    private static IInterceptor.Stub mInterceptor = new IInterceptor.Stub() {
//
//        @Override
//        public void onIntercepted(Intent intent, Bundle bundle) throws RemoteException {
//            L.dd();
//        }
//
//        @Override
//        public void onInterceptedForResult(Intent intent, Bundle bundle, int i) throws RemoteException {
//            L.dd();
//        }
//
//        @Override
//        public void onInterceptedFromRecents(int i, Bundle bundle) throws RemoteException {
//            L.dd();
//        }
//
//        @Override
//        public void onHomeKeyEvent(KeyEvent keyEvent) throws RemoteException {
//            L.dd();
//        }
//    };

    public static void testHomeKeyInterceptor(Context context) {
        // 获取StartupInterceptor
//        StartupInterceptor mStartupInterceptor = context.getSystemService(StartupInterceptor.class);
        // 通过StartupInterceptor的实例注册home键事件拦截器
        // 注：注册此拦截器后，触发home键事件时，会通过此拦截器的onHomeKeyEvent返回事件
//        mStartupInterceptor.registerHomeKeyInterceptor(mInterceptor);

        // 通过StartupInterceptor的实例注销home键事件拦截器
        // 注：拦截器注销后，触发home键事件时，系统不再拦截home键事件
//        mStartupInterceptor.unregisterHomeKeyInterceptor(mInterceptor)
    }

    /**
     * 测试 Toast
     *
     * @param context
     */
    public static void showToast(Activity context) {
        BaseConstants.getHandler().removeCallbacksAndMessages(null);
        index++;
        L.d("showToast " + index);
//        if (toast == null) {
//            toast = Toast.makeText(context, "12121212", Toast.LENGTH_SHORT);
//            L.d("showToast makeText " + index);
//        }
//        toast.show();
        String msg = "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十";
//        msg = "一二三四五六七Toast " + index;
//        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();


//        BaseConstants.getHandler().postDelayed(() -> {
//            toast.setText("2222222222222222");
//        }, 1000);
//        BaseConstants.getHandler().postDelayed(() -> {
//            toast.setText("333333333333333333");
//        }, 2000);
        BaseConstants.getHandler().postDelayed(() -> {
            Toast.makeText(context,"短文字",Toast.LENGTH_SHORT).show();
        }, 7000);


        toast = Toast.makeText(context,msg,Toast.LENGTH_LONG);
        toast.show();

//        View view = LayoutInflater.from(context).inflate(R.layout.toast_test, null, false);
//        TextView tv = view.findViewById(R.id.message);
//        tv.setText("一二三四五六七八一二三四五六:" + index);
//        toast.setView(view);
//        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.show();
    }

    public static void startShowAnim(View view) {
        PathInterpolator pathInterpolator = new PathInterpolator(0.4f, 0f, 0.2f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 1000f);
        animator.setInterpolator(pathInterpolator);
        animator.setDuration(5000);
        animator.start();
    }

    public static void setFull(Window window, Context context, boolean isFull) {
        try {
            Window.class.getDeclaredMethod("setFullScreen", Context.class, Boolean.TYPE).invoke(window, context, isFull);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            L.dd("err");
        }
    }

    /**
     * 设置提醒
     *
     * @param context
     * @param minute  几秒之后发送广播
     */
    public static void alarmAfterMinute(Context context, int minute) {
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(AlarmReceiver.ALARM_ACTION);
        context.registerReceiver(new AlarmReceiver(), filter);

        //设置定时提醒
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + ((long) minute * 60 * 1000));
//        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Intent intent = new Intent(AlarmReceiver.ALARM_ACTION);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        long ert = SystemClock.elapsedRealtime();
        long when = calendar.getTimeInMillis() - (System.currentTimeMillis() - ert);
        L.d("alarm time: " + calendar.getTimeInMillis() + " -- " + DateUtil.getFullTime(calendar.getTimeInMillis()) + ", " + pIntent + ", when=" + when + " -- " + DateUtil.getFullTime(when));
    }

    private static class AlarmReceiver extends BroadcastReceiver {
        public static final String ALARM_ACTION = "MCompanyDemo.TestUtil.AlarmReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("AlarmReceiver.onReceive: " + intent.getAction());
        }
    }

    // 关联 SystemFonts,TypeFace
    public static void copyAssetsFonts(Context context) {
        // storage/emulated/0/Fonts
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fonts";
        root = "/data/personal_fonts";
        final String target = root + "/etc/fonts_personal.xml";
        final String targetDir = root + "/fonts";
        final String assetsFileName = "fonts_personal.xml";
        final String assetsDirName = "fonts";

        BaseConstants.getFixPool().execute(() -> {
            FileUtils.copyAssetsFile(context, assetsFileName, target, false);
            FileUtils.copyAssetsDir(context, assetsDirName, targetDir, false);
        });
    }

    public static void parsePlatformConfigFile(String PLATFORM_CONFIG_PATH) throws IOException, JSONException {
        Log.d(TAG, "parsePlatformConfigFile start");
        int n;
        FileInputStream in = null;
        StringBuilder builder = new StringBuilder();
        try {
            in = new FileInputStream(PLATFORM_CONFIG_PATH);
            byte[] buffer = new byte[4096];
            while ((n = in.read(buffer, 0, 4096)) > -1) {
                builder.append(new String(buffer, 0, n));
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        Log.d(TAG, "parsePlatformConfigFile:" + builder.toString());
        JSONObject jsonObject = new JSONObject(builder.toString());
        if (jsonObject.has("package_info")) {
            JSONObject pkgInfo = jsonObject.getJSONObject("package_info");
            JSONObject internal = pkgInfo.getJSONObject("internal");
            JSONObject external = pkgInfo.getJSONObject("internal");
            String internalPkgName = internal.getString("name");
            String internalAction = internal.getString("action");
            String externalPkgName = external.getString("name");
            String externalAction = external.getString("action");
        }
        Log.e(TAG, "parsePlatformConfigFile end");
    }

    /**
     * 测试 发送广播
     *
     * @param activity
     * @param action
     * @param packageName 接收包名
     */
    public static void sendBroadcast(Activity activity, String action, String packageName) {
        L.d("sendBroadcast:" + action);
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra("extra_show", "wifi");
            if (!TextUtils.isEmpty(packageName)) {
                intent.setPackage(packageName);//发给指定包名
            }
            activity.sendBroadcast(intent);
            // UserHandle.ALL  UID = -1
//        activity.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(-1));
        } catch (Exception e) {
            e.printStackTrace();
            L.e("sendBroadcast (" + action + ") Exception:" + e.getMessage());
            Toast.makeText(activity, "sendBroadcast (" + action + ") Exception:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * SystemUI 发送广播
     * public static final void sendAppBroadcast(Context context, String str, String str2) {
     * Intrinsics.checkNotNullParameter(context, "<this>");
     * Intrinsics.checkNotNullParameter(str, "pkg");
     * Intrinsics.checkNotNullParameter(str2, "action");
     * try {
     * Logger.info("Context.openOutPanel", "pkg: " + str + " action: " + str2);
     * Intent intent = new Intent();
     * intent.setAction(str2);
     * intent.setPackage(str);
     * context.sendBroadcastAsUser(intent, UserHandle.ALL);
     * } catch (Exception e) {
     * e.printStackTrace();
     * Logger.info("Context.openOutPanel", String.valueOf(Unit.INSTANCE));
     * }
     * }
     */

    // 转换经纬度和UTC时间
    public static void convertGpsAndUtcTimeTest() {
        double w = 102.5962240000;
        double lon = 129.7029650020;
        L.d("convert lat:" + GpsConvertUtil.convertCoordinates(lon));
        L.d("convert1 lat:" + ddmmTodddd1(lon));

        //20220315163333
        int date = 10123;
        double time = 123403.588;
        L.d("-------------------------");
        long result = GpsConvertUtil.getTimeZoneMillis(date, time);
        SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        L.d(String.valueOf(result));
        L.d(sdfOut.format(new Date(result)));
        L.d("-------------------------");
    }

    /**
     * @param res 2302.4545412/11325.45451212
     * @return
     */
    private static double ddmmTodddd1(double res) {
        if (res == 0.0) {
            return res;
        }
        String resStr = String.valueOf(res);
        if (resStr.indexOf(".") < 2) {
            return res;
        }
        String wmm = resStr.substring(resStr.indexOf(".") - 2);
        String wdd = resStr.substring(0, resStr.indexOf(".") - 2);
        double wmm_a = Double.parseDouble(wmm) / 60;
        return Double.parseDouble(wdd) + wmm_a;
    }


    private static TextView tvGtro;
    private static TextView tvAcc;

    /**
     * 获取陀螺仪数据
     *
     * @param context
     */
    public static void testSensorData(Context context, TextView tvAcc, TextView tvGtro) {
        TestUtil.tvAcc = tvAcc;
        TestUtil.tvGtro = tvGtro;
        L.d("getSensorData");
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);//陀螺仪
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度
        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, 1);
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor, 1);

    }

    private static final SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
        private long lastLogTime = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            long now = System.currentTimeMillis();
            if (now - lastLogTime >= 1000) {
                lastLogTime = now;
                String msg;
                if (event.values[0] > 0 || event.values[1] > 0 || event.values[2] > 0) {
                    msg = "gyroscopeSensor onSensorChanged::" + event.values[0] + " " + event.values[1] + " " + event.values[2];
                } else {
                    msg = "gyroscopeSensor onSensorChanged:: 0";
                }
                L.d(msg);
                if (tvGtro != null) {
                    tvGtro.post(() -> tvGtro.setText(msg));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            L.d("gyroscopeSensor onAccuracyChanged::" + accuracy);
        }
    };

    private static final SensorEventListener accelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            String msg = "accelerometerSensor onSensorChanged::" + event.values[0] + " " + event.values[1] + " " + event.values[2];
            L.d(msg);
            if (tvAcc != null) {
//                tvAcc.post(() -> tvAcc.setText(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            L.d("accelerometerSensor onAccuracyChanged::" + accuracy);
        }
    };


    /**
     * 我看了一下这个方法，整体是判断某个设备的usb是否赋予权限，未授权的话就进行权限申请
     */
    public static void usbPermission(Context context) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // 获取设备
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        L.d("----------------------------------");
        if (deviceList.size() > 0) {
            for (UsbDevice device : deviceList.values()) {
                //Toast.makeText( this, device.toString(), Toast.LENGTH_SHORT).show();
                // 官方文档上边是这样写的，直接获取第一个，但往往不一定只连接一个设备，就要求我们找到自己想要的那个，一般的做法是
                int count = device.getInterfaceCount();
                for (int i = 0; i < count; i++) {
                    UsbInterface usbInterface = device.getInterface(i);
                    L.d("DeviceName:" + device.getDeviceName() + ", DeviceId=" + device.getDeviceId() + ", ProductName=" + device.getProductName() + ", ProductId=" + device.getProductId() + " usbInterfaceName=" + usbInterface.getName());
                    // 之后我们会根据 intf的 getInterfaceClass 判断是哪种类型的Usb设备，
                }
                // 没有权限,则申请
                if (!manager.hasPermission(device)) {
                    L.d("申请USB权限");
                    String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    manager.requestPermission(device, mPermissionIntent);
                    break;
                } else {
                    readUsbDevice(context, device);
                    break;
                }
            }
        }
        L.d("----------------------------------");
    }

    /**
     * 读取USB设备
     *
     * @param usbDevice
     */
    private static void readUsbDevice(Context context, UsbDevice usbDevice) {

    }

    public static boolean isRegisterBroadCast = false;
    public static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("onReceive:" + intent.getAction());
        }
    };

    public static final String ACTION_guide_dismiss = "com.gxa.guide.dismiss";
    public static final String ACTION_launcher = "com.gxatek.cockpit.launcher.LAUNCHER_SCENE_CHANGED";
    public static final String ACTION_guide_display = "com.gxa.guide.display";
    public static final String ACTION_timeSync = "com.gxa.car.timesync.clock.action.update.time";
    public static final String ACTION_schedule = "update_schedule_widget";
    public static final String ACTION_CloseScreen = "com.gxatek.cockpit.carsetting.CloseScreen";
    public static final String ACTION_Open_panel = "com.gxatek.cockpit.systemui.ALL_MENU_CLICK";

    public static void registerBroadcast(Context context) {
        L.d("registerBroadcast");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction("com.exa.companyclient.ACTION_OPEN_CLIENT");
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(ACTION_guide_dismiss);
        filter.addAction(ACTION_launcher);
        filter.addAction(ACTION_guide_display);
        filter.addAction(ACTION_timeSync);
        filter.addAction(ACTION_schedule);
        filter.addAction(ACTION_CloseScreen);
        filter.addAction(ACTION_Open_panel);
//        filter.addDataScheme("file");//for MediaProvider
        context.registerReceiver(mReceiver, filter);
        isRegisterBroadCast = true;


    }


    /**
     * android 11 use permission MANAGE_EXTERNAL_STORAGE
     */
    public static void testCreateFileInRootDir() {
        FileOutputStream fos = null;
        try {
            final File dir = Environment.getExternalStorageDirectory();
            final File newFile = new File(dir, "test_new_file");
            newFile.mkdir();
            final File newFile1 = new File(newFile.getAbsolutePath(), "newFile");
            L.d("开始创建文件：" + newFile.getAbsolutePath());
            if (!newFile1.exists()) {
                newFile1.createNewFile();
            }
            fos = new FileOutputStream(newFile1, true);
            L.d("向文件末尾写入内容：" + newFile.getAbsolutePath());
            fos.write(("time:" + System.currentTimeMillis() + "\n").getBytes(StandardCharsets.UTF_8));
            L.d("内容写入成功");
            fos.close();
            try {
                FileInputStream fis = new FileInputStream(newFile1);
                byte[] bytes = new byte[1024];
                while (fis.read(bytes) != -1) {
                    L.d("读取成功:" + new String(bytes));
                }
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            L.e(e.getMessage());
        }
    }

    /**
     * 调试字体
     */
    public static void testFonts(Activity activity) {
        TextView text0, text1, text2, text3, text4, text5, text6, text7, text8;
        L.dd();
        activity.findViewById(R.id.fontsBox).setVisibility(View.VISIBLE);
        final String fontTestWords = "Innovation in China 中国制造，惠及全球 0123456789";
        text0 = activity.findViewById(R.id.text0);
        text1 = activity.findViewById(R.id.text1);
        text2 = activity.findViewById(R.id.text2);
        text3 = activity.findViewById(R.id.text3);
        text4 = activity.findViewById(R.id.text4);
        text5 = activity.findViewById(R.id.text5);
        text6 = activity.findViewById(R.id.text6);
        text7 = activity.findViewById(R.id.text7);
        text8 = activity.findViewById(R.id.text8);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            L.d("text0 字重：" + text0.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text1 字重：" + text1.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text2 字重：" + text2.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text3 字重：" + text3.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text4 字重：" + text4.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text5 字重：" + text5.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text6 字重：" + text6.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text7 字重：" + text7.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
            L.d("text8 字重：" + text8.getTypeface().getWeight() + ", style" + text0.getTypeface().getStyle());
        }

        text0.setText(fontTestWords + "   ");
        text1.setText(fontTestWords + "   sans-serif");
        text2.setText(fontTestWords + "   default");
        text3.setText(fontTestWords + "   AIONType");
        text4.setText(fontTestWords + "   serif");
        text5.setText(fontTestWords + "   SourceHanSansCN");
        text6.setText(fontTestWords + "   xml-GacFont-Medium");
        text7.setText(fontTestWords + "   xml-GacFont-500");
        text8.setText(fontTestWords + "   xml-sans-serif-500");

        Typeface sans_serif = Typeface.create("sans-serif", Typeface.BOLD);
        Typeface aDefault = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        Typeface AIONType = Typeface.create("SourceHanSansCN-Normal", Typeface.BOLD);
        Typeface SourceHanSansCN = Typeface.create("SourceHanSansCN-Bold", Typeface.BOLD);
        Typeface serif = Typeface.create("serif", Typeface.BOLD);
        Typeface regular = Typeface.create("AIONType-regular", Typeface.NORMAL);


        text1.setTypeface(sans_serif);
        text2.setTypeface(aDefault);
        text3.setTypeface(AIONType);
        text4.setTypeface(serif);
        text5.setTypeface(SourceHanSansCN);

//        L.d("SourceHanSansCN is Default ? " + (SourceHanSansCN.equals(aDefault)));
        L.d("sans-serif is Default ? " + (sans_serif.equals(aDefault)));
//        L.d("serif is Default ? " + (serif.equals(aDefault)));
//        L.d("monospace is Default ? " + (monospace.equals(aDefault)));
        L.d("AIONType is Default ? " + (AIONType.equals(aDefault)));
        L.d("FZVariable-YouHeiJ is Default ? " + (SourceHanSansCN.equals(aDefault)));
//
        LogTools.logSystemFonts();
    }

    /**
     * Build.VERSION_CODES.O
     * 全屏监听
     *
     * @param activity
     */
    public static void registerFullScreenListener(Activity activity) {
        Uri uri = Settings.System.getUriFor("fullscreen");
        activity.getContentResolver().registerContentObserver(uri, false, new ContentObserver(new Handler(Looper.myLooper())) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                L.e("registerFullScreenListener onChange1:" + selfChange);
            }

            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                super.onChange(selfChange, uri);
                L.e("registerFullScreenListener onChange2:" + selfChange + ",uri=" + uri);
            }

            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri, int flags) {
                super.onChange(selfChange, uri, flags);
                L.e("registerFullScreenListener onChange3:" + selfChange + ",flags:" + flags);
            }

            @Override
            public void onChange(boolean selfChange, @NonNull Collection<Uri> uris, int flags) {
                super.onChange(selfChange, uris, flags);
                L.e("registerFullScreenListener onChange4:" + selfChange + ",flags:" + flags);
            }
        });
    }

    private static final String[] STORAGE_PERMISSION = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE,};

    public static void testCreateFile(Context context) {
        final int permissionResult1 = PermissionChecker.checkCallingOrSelfPermission(context, STORAGE_PERMISSION[0]);
        final int permissionResult2 = PermissionChecker.checkCallingOrSelfPermission(context, STORAGE_PERMISSION[1]);
//        final int permissionResult3 = PermissionChecker.checkCallingOrSelfPermission(this, STORAGE_PERMISSION[2]);
        L.d("testCreateFile start permissionResult1:" + permissionResultToString(permissionResult1) + ", permissionResult2:" + permissionResultToString(permissionResult2)
//                + ", permissionResult3:" + permissionResultToString(permissionResult3)
        );

        // String path = getExternalFilesDir(null).getAbsolutePath();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        // String path = "/storage/usb1";
        File file = new File(path, "SubDir1");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write("test code\n".getBytes(StandardCharsets.UTF_8));
            fos.close();
            Toast.makeText(context, "创建目录成功：" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "创建目录失败", Toast.LENGTH_LONG).show();
        }
    }

    private static String permissionResultToString(int permissionResult) {
        switch (permissionResult) {
            case PermissionChecker.PERMISSION_DENIED:
                return "PERMISSION_DENIED";
            case PermissionChecker.PERMISSION_GRANTED:
                return "PERMISSION_GRANTED";
            case PermissionChecker.PERMISSION_DENIED_APP_OP:
                return "PERMISSION_DENIED_APP_OP";
            default:
                return "unknown:" + permissionResult;
        }
    }

    /**
     * Send buried broadcast use for Statistics.
     */
    private void sendStatisticsBroadcast(Context context, String pkg, PackageManager pm) {
        String appId = "com.gxatek.cockpit.gxadataminingservice";
        String appName = "";
        String versionName = "";

        try {
            PackageInfo info = pm.getPackageInfo(appId, 0);
            if (info != null) {
                versionName = info.versionName;
                CharSequence label = info.applicationInfo.loadLabel(pm);
                if (label != null) {
                    appName = label.toString();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent("com.gxatek.cockpit.datacenter.action.UPLOAD");
            intent.setData(new Uri.Builder().authority("com.gxatek.cockpit.datacenter").scheme("os").path("/upload").build());
            intent.addFlags(0x01000000);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", appId);
            jsonObject.put("appName", appName);
            jsonObject.put("appVersion", versionName);
            jsonObject.put("event", "exposure");
            jsonObject.put("page", "systemui");
            jsonObject.put("local", "Toast");
            jsonObject.put("action", "Toast_push");
            jsonObject.put("ets", System.currentTimeMillis());
            JSONObject property = new JSONObject();
            property.put("module_source", pkg);
            jsonObject.put("property", property);
            intent.putExtra("extra", jsonObject.toString());
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUiModeStr(UiModeManager modeManager) {
        String result = "" + modeManager.getNightMode();
        switch (modeManager.getNightMode()) {
            case UiModeManager.MODE_NIGHT_YES:
                result = "黑夜";
                break;
            case UiModeManager.MODE_NIGHT_NO:
                result = "白天";
                break;
            case UiModeManager.MODE_NIGHT_AUTO:
                result = "auto";
                break;
            case UiModeManager.MODE_NIGHT_CUSTOM:
                result = "custom";
                break;
            default:
                break;
        }
        return result;
    }
}
