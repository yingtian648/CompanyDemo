package com.exa.companydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.CarToast;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.FileUtils;
import com.exa.baselib.utils.GpsConvertUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.companydemo.utils.LogTools;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import static android.content.Context.SENSOR_SERVICE;
import static com.exa.baselib.utils.L.TAG;
import static com.exa.baselib.utils.L.v;

public class TestUtil {

    private static Toast toast;

    /**
     * 测试 Toast
     *
     * @param context
     */
    public static void showToast(Activity context) {
        L.d("showToast");
        String msg = "一二三四五六七八一二三四五六七八一二三四五六七八";
//        msg = "一二三四五六七";
//        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//        BaseConstants.getHandler().postDelayed(() -> {
//            CarToast.makeText(context, msg, CarToast.LENGTH_LONG).show();
//        }, 7000);
//        BaseConstants.getHandler().postDelayed(() -> {
//            CarToast.makeText(context, "好了", Toast.LENGTH_LONG).show();
//        }, 12000);
        toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_test, null, false);
        TextView tv = view.findViewById(R.id.message);
        tv.setText("一二三四五六七八一二三四五六1111111");
        toast.setView(view);
//        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        /**
         * app-build.grable中指定 targetSdk>=30
         * 设置Toast显示位置 offset 为偏移量
         * @param gravity Gravity.TOP,Gravity.CENTER,Gravity.BOTTOM
         * 不设置gravity，Toast显示在屏幕顶部中间
         */
        toast.setGravity(Gravity.BOTTOM, 300, 200);
        BaseConstants.getHandler().postDelayed(() -> {
            if (toast != null) {
                L.d("NotificationService", "toast.show");
                toast.show();
            }
        }, 1);
//        final WindowManager manager = getSystemService(WindowManager.class);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        params.gravity = Gravity.TOP;
//        params.y = 16;
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.packageName = getPackageName();
//        params.setFitInsetsSides(0);
//        params.setFitInsetsTypes(0);
//        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//        params.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//        params.format = PixelFormat.TRANSLUCENT;
//        UiModeManager uiModeManager = (UiModeManager) mContext.getSystemService(Context.UI_MODE_SERVICE);
//        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
//            final Rect padding = savePadding(textView);
//            textView.setBackgroundResource(R.drawable.toast_customer_night);
//            restorePadding(textView, padding);
//        } else {
//            final Rect padding = savePadding(textView);
//            textView.setBackgroundResource(R.drawable.toast_customer_normal);
//            restorePadding(textView, padding);
//        }
//        manager.addView(view, params);
//
//        btn4.postDelayed(() -> {
//            manager.removeView(view);
//        }, 3000);
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

    public static void testDialog(Activity activity, String title, int windowType) {
        final MyDialog dialog = new MyDialog(activity, 0, windowType);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null, false);
        Button sureBtn = view.findViewById(R.id.sure_button);
        Button cancelBtn = view.findViewById(R.id.cancel_button);
        TextView titleT = view.findViewById(R.id.titleT);
        titleT.setText(title);
        dialog.setContentView(view);
        sureBtn.setOnClickListener(v -> {
            TestUtil.showToast(activity);
        });
        cancelBtn.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                dialog.cancel();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
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

    private static class MyDialog extends Dialog {
        private int windowType;

        public MyDialog(@NonNull Context context) {
            super(context);
            // setWindowAttrs();
        }

        public MyDialog(@NonNull Context context, int themeResId, int windowType) {
            super(context, themeResId);
            this.windowType = windowType;
            // setWindowAttrs();
        }

        protected MyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
            // setWindowAttrs();
        }

        @Override
        public void setContentView(@NonNull View view) {
            super.setContentView(view);
            // setWindowAttrs();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setWindowAttrs();
            super.onCreate(savedInstanceState);
        }

        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event) {
//            L.dd(event.getAction());
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                L.dd("点击去弹框之外区域");
            }
            return super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        @SuppressLint("WrongConstant")
        private Window setWindowAttrs() {
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes == null) {
                return window;
            }
            attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;//WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;// WindowManager.LayoutParams.WRAP_CONTENT;
            attributes.gravity = Gravity.CENTER;
            attributes.format = PixelFormat.TRANSLUCENT;
            attributes.dimAmount = 0f;
            attributes.flags = attributes.flags
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE //弹出后不会抢window焦点
//                    | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
//                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            // 未设置FLAG_NOT_FOCUSABLE时，次标志可防止此窗口成为输入法的目标
            // 设置了FLAG_NOT_FOCUSABLE时，即使窗口不可聚焦，设置此标志也会请求将窗口作为输入法目标
            ;
            attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
//                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
            attributes.setTitle("MainActivity_Dialog");
            attributes.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
            if (windowType > 2500) {
                attributes.type = windowType;//对应windowType
            }
            window.setAttributes(attributes);
            return window;
        }
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
        long result = GpsConvertUtil.getCurrentTimeZoneTimeMillis(date, time);
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

    /**
     * 获取陀螺仪数据
     *
     * @param context
     */
    public static void getSensorData(Context context) {
        L.d("getSensorData");
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(4);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(1);
        sensorManager.registerListener(listener, gyroscopeSensor, 1);
        sensorManager.registerListener(listener, accelerometerSensor, 1);
    }

    private static SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            L.d("onSensorChanged::" + event.values[0] + " " + event.values[1] + " " + event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            L.d("onAccuracyChanged::" + accuracy);
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
                    L.d("DeviceName:" + device.getDeviceName() + ", DeviceId=" + device.getDeviceId() + ", ProductName=" +
                            device.getProductName() + ", ProductId=" + device.getProductId() + " usbInterfaceName=" + usbInterface.getName());
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
        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        L.d("readUsbDevice::" + (storageDevices == null ? "null" : storageDevices.length));
        for (int i = 0; i < storageDevices.length; i++) {
            UsbMassStorageDevice device = storageDevices[i];
            try {
                //初始化
                device.init();
                if (device.getPartitions() != null && device.getPartitions().size() > 0) {
                    //获取partition
                    Partition partition = device.getPartitions().get(0);
                    FileSystem currentFs = partition.getFileSystem();
                    //获取根目录
                    UsbFile root = currentFs.getRootDirectory();
                    root.createFile("测试创建文件.txt");
                    L.d("创建测试文件完成");
                    String msg = "读取U盘文件列表：" + root.listFiles()[0].getName();
                    if (root.listFiles() != null) {
                        for (int j = 0; j < root.listFiles().length; j++) {
                            L.d("U盘文件:" + root.listFiles()[i].getName());
                            if (root.listFiles()[i].isDirectory()) {
                                root.listFiles()[i].createFile("测试创建文件.txt");
                            }
                        }
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    L.d(msg);
                } else {
                    L.d("device.getPartitions() is empty");
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e("readUsbDevice Exception:" + e.getMessage());
            }
        }

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
        text0 = activity.findViewById(R.id.text);
        text1 = activity.findViewById(R.id.text1);
        text2 = activity.findViewById(R.id.text2);
        text3 = activity.findViewById(R.id.text3);
        text4 = activity.findViewById(R.id.text4);
        text5 = activity.findViewById(R.id.text5);
        text6 = activity.findViewById(R.id.text6);
        text7 = activity.findViewById(R.id.text7);
        text8 = activity.findViewById(R.id.text8);

        text0.setText(fontTestWords + "   ");
        text1.setText(fontTestWords + "   sans-serif");
        text2.setText(fontTestWords + "   default");
        text3.setText(fontTestWords + "   GacFont");
        text4.setText(fontTestWords + "   serif");
        text5.setText(fontTestWords + "   SourceHanSansCN");
        text6.setText(fontTestWords + "   xml-gacfont-500");
        text7.setText(fontTestWords + "   xml-normal-500");
        text8.setText(fontTestWords + "   xml-sans-serif-500");

        Typeface sans_serif = Typeface.create("sans-serif", Typeface.NORMAL);
        Typeface aDefault = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        Typeface GacFont = Typeface.create("GacFont", Typeface.NORMAL);
        Typeface SourceHanSansCN = Typeface.create("SourceHanSansCN", Typeface.NORMAL);
        Typeface serif = Typeface.create("serif", Typeface.NORMAL);

//        text1.setTypeface(sans_serif);
        text2.setTypeface(aDefault);
        text3.setTypeface(GacFont);
        text4.setTypeface(serif);
        text5.setTypeface(SourceHanSansCN);

//        L.d("SourceHanSansCN is Default ? " + (SourceHanSansCN.equals(aDefault)));
        L.d("sans-serif is Default ? " + (sans_serif.equals(aDefault)));
//        L.d("serif is Default ? " + (serif.equals(aDefault)));
//        L.d("monospace is Default ? " + (monospace.equals(aDefault)));
        L.d("GacFont is Default ? " + (GacFont.equals(aDefault)));
//
//        LogTools.logSystemFonts();
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
//                L.e("registerFullScreenListener onChange1:" + selfChange);
            }

            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                super.onChange(selfChange, uri);
//                L.e("registerFullScreenListener onChange2:" + selfChange);
            }

            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri, int flags) {
                super.onChange(selfChange, uri, flags);
//                L.e("registerFullScreenListener onChange3:" + selfChange + ",flags:" + flags);
            }

            @Override
            public void onChange(boolean selfChange, @NonNull Collection<Uri> uris, int flags) {
                super.onChange(selfChange, uris, flags);
//                L.e("registerFullScreenListener onChange4:" + selfChange + ",flags:" + flags);
            }
        });
    }

    private static final String[] STORAGE_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    };

    public static void testCreateFile(Context context) {
        final int permissionResult1 = PermissionChecker.checkCallingOrSelfPermission(context, STORAGE_PERMISSION[0]);
        final int permissionResult2 = PermissionChecker.checkCallingOrSelfPermission(context, STORAGE_PERMISSION[1]);
//        final int permissionResult3 = PermissionChecker.checkCallingOrSelfPermission(this, STORAGE_PERMISSION[2]);
        L.d("testCreateFile start permissionResult1:" + permissionResultToString(permissionResult1)
                        + ", permissionResult2:" + permissionResultToString(permissionResult2)
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
}
