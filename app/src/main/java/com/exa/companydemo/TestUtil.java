package com.exa.companydemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.widget.CarToast;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CarToast;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.utils.GpsConvertUtil;
import com.exa.baselib.utils.L;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.SENSOR_SERVICE;

public class TestUtil {

    /**
     * 测试 Toast
     *
     * @param context
     */
    public static void showToast(Context context) {
        L.d("showToast");
        String msg = "一二三四五六七八一二三四五六七八一二三四五六七八";
//        msg = "一二三四五六七";
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//        CarToast.makeText(context, msg, CarToast.LENGTH_LONG).show();

//        Toast toast = new Toast(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.layout, null, false);
//        toast.setView(view);
//        toast.show();

//        CarToast carToast = new CarToast(context);
//        View viewc = LayoutInflater.from(context).inflate(R.layout.layout, null, false);
//        carToast.setView(viewc);
//        carToast.show();

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

    /**
     * 测试 发送广播
     *
     * @param activity
     * @param action
     * @param packageName 接收包名
     */
    public static void sendBroadcast(Activity activity, String action, String packageName){
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
            Toast.makeText(activity,"sendBroadcast (" + action + ") Exception:" + e.getMessage(),Toast.LENGTH_SHORT).show();
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
}
