package com.exa.baselib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Properties;

import androidx.annotation.NonNull;

public class CrashHandle implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    /**
     * 是否开启日志输出, 在Debug状态下开启, 在Release状态下关闭以提升程序性能
     */
    private static boolean DEBUG = false;
    @SuppressLint("StaticFieldLeak")
    private static CrashHandle INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private final Properties mDeviceCrashInfo = new Properties();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandle() {
    }

    /**
     * 是否debug模式
     */
    public void setDebug(boolean isDebug) {
        DEBUG = isDebug;
    }

    /**
     * 获取CrashHandler实例
     */
    public static synchronized CrashHandle getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandle();
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     */
    public void init(Context ctx, boolean isDebug) {
        mContext = ctx;
        setDebug(isDebug);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void init(Context ctx) {
        mContext = ctx;
        setDebug(true);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable e) {
        handleException(e);
        if (mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, e);
        }
        // Sleep一会后结束程序
        // 线程停止一会是为了显示Toast信息给用户
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            Log.e(TAG, "Error : ", ie);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     */
    private void handleException(Throwable ex) {
        if (ex != null) {
            final String msg = ex.getLocalizedMessage();
            // 使用Toast来显示异常信息
            toastShow(msg);
            // 收集设备信息
            collectCrashDeviceInfo();
            // 保存错误报告文件
            // 在系统崩溃的时候可能会直接弹出app,没法发出错误日志
            // 不在这里发送错误日志【在项目启动页开启service来发送错误日志】
            saveCrashInfoToFile(ex);
        }
    }

    private void toastShow(String msg) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (DEBUG) {
                    String content = "程序出错啦:" + msg;
                    Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
                }
                Looper.loop();
            }
        }.start();
    }

    /**
     * 收集程序崩溃的应用信息和设备信息
     */
    public void collectCrashDeviceInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "null" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                // setAccessible(boolean flag)
                // 将此对象的 accessible 标志设置为指示的布尔值。
                // 通过设置Accessible属性为true,才能对私有变量进行访问，
                // 不然会得到一个IllegalAccessException的异常
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null));
                if (DEBUG) {
                    Log.d(TAG, field.getName() + " : " + field.get(null));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     */
    private void saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        // printStackTrace(PrintWriter s)
        // 将此 throwable 及其追踪输出到指定的 PrintWriter
        ex.printStackTrace(printWriter);
        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        // toString() 以字符串的形式返回该缓冲区的当前值。
        String result = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put(STACK_TRACE, result);
        String msg = mDeviceCrashInfo.toString();

        // 以下几行代码是手机错误信息的代码
        // 1.JLog是自定义Log类，在这里打印崩溃日志
        // 2.JDLogManage是自定义【数据库操作管理类】——用于保存、取出和删除崩溃日志
        L.e(msg);
        // 生成错误日志并保存
        // 常用错误文件后缀.rc
        // todo 保存到本地数据库？
    }
}
