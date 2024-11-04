package com.exa.companydemo.ford;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * use for CarPower str policy
 *
 * @hide
 */
public class FordCarPowerStrPolicy {
    private static final String TAG = "FORD.CAR.POWER.STR.POLICY";
    private static final String CONFIG_FILE = "/etc/ford/ford_car_power_str_policy.xml";
    private static final String FIELD_WHITE_LIST = "white_list";
    private static final String FIELD_REMOVE_UI = "remove_ui";
    private static final String FIELD_KILL_PROCESS = "kill_process";
    private static final String FIELD_PACKAGE = "package";
    private static final String FIELD_NAME = "name";
    private final List<PackageInfo> mWhiteList = new ArrayList<>();
    private final List<PackageInfo> mRemoveUiList = new ArrayList<>();
    private final List<PackageInfo> mKillList = new ArrayList<>();
    private Context mContext;
    private final H mHandler;
    private static final int MSG_READ_POLICY = 1;
    private static final int MSG_DO_STR_POLICY = 2;
    private final IActivityTaskManager mAtm = ActivityTaskManager.getService();
    private final ActivityManager mAm;

    public FordCarPowerStrPolicy(Context context) {
        mContext = context;
        mAm = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        HandlerThread handlerThread = new HandlerThread("FordCarPowerStrPolicy");
        handlerThread.start();
        mHandler = new H(handlerThread.getLooper());
        mHandler.sendEmptyMessage(MSG_READ_POLICY);
    }

    /**
     * use by FordCarPowerManagementService
     * 1.we will not care white list.
     * 2.If the app is in removeUiList and the running task list, we will delete the app's UI.
     * 3.If the app is in killList, it will be killed.
     */
    public void doStrPolicy() {
        Log.i(TAG, "doStrPolicy");
        mHandler.sendEmptyMessage(MSG_DO_STR_POLICY);
    }

    private class H extends Handler {
        public H(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DO_STR_POLICY:
                    doStrPolicyInternal();
                    break;
                case MSG_READ_POLICY:
                    readConfig();
                    break;
            }
        }
    }

    private void doStrPolicyInternal() {
        // get running task
        List<ActivityManager.RunningTaskInfo> tasks = mAm.getRunningTasks(Integer.MAX_VALUE);
        if (tasks != null) {
            for (ActivityManager.RunningTaskInfo task : tasks) {
                if (task.baseIntent.getComponent() == null) {
                    continue;
                }
                String pkgName = task.baseIntent.getComponent().getPackageName();
                for (PackageInfo packageInfo : mRemoveUiList) {
                    if (Objects.equals(pkgName, packageInfo.pkgName)) {
                        Log.i(TAG, "removeUi " + pkgName + ", taskId=" + task.taskId);
                        removeTask(task.taskId);
                    }
                }
            }
        }
        // get running process
        List<ActivityManager.RunningAppProcessInfo> processes = mAm.getRunningAppProcesses();
        if (processes != null) {
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.pkgList == null || process.pkgList.length == 0) {
                    continue;
                }
                String pkgName = process.pkgList[0];
                for (PackageInfo packageInfo : mKillList) {
                    if (Objects.equals(pkgName, packageInfo.pkgName)) {
                        Log.i(TAG, "forceStopPackage " + pkgName
                                + ", PID=" + process.pid
                                + ", processName=" + process.processName);
//                        try {
//                            mAm.forceStopPackage(pkgName);
//                        } catch (Exception e) {
//                            Log.w(TAG, "forceStopPackage err", e);
//                        }
                    }
                }
            }
        }
    }

    private void removeTask(int id) {
        try {
            mAtm.removeTask(id);
        } catch (RemoteException e) {
            Log.w(TAG, "removeTask err", e);
        }
    }


    private void readConfig() {
        Log.i(TAG, "readConfig start");
        File file = new File(CONFIG_FILE);
        if (!file.exists() || !file.isFile()) {
            Log.e(TAG, "readConfig err: file not exist!! " + CONFIG_FILE);
            return;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());

            int eventType = parser.getEventType();
            String parserType = FIELD_WHITE_LIST;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (parser.getName()) {
                        case FIELD_WHITE_LIST:
                            parserType = FIELD_WHITE_LIST;
                            break;
                        case FIELD_REMOVE_UI:
                            parserType = FIELD_REMOVE_UI;
                            break;
                        case FIELD_KILL_PROCESS:
                            parserType = FIELD_KILL_PROCESS;
                            break;
                        case FIELD_PACKAGE:
                            PackageInfo packageInfo = new PackageInfo();
                            packageInfo.appName = parser.getAttributeValue(null, FIELD_NAME);
                            packageInfo.pkgName = parser.nextText();
                            switch (parserType) {
                                case FIELD_WHITE_LIST:
                                    mWhiteList.add(packageInfo);
                                    break;
                                case FIELD_REMOVE_UI:
                                    mRemoveUiList.add(packageInfo);
                                    break;
                                case FIELD_KILL_PROCESS:
                                    mKillList.add(packageInfo);
                                    break;
                            }
                            break;
                    }
                }
                eventType = parser.next();
            }
            Log.i(TAG, "whiteList: " + mWhiteList.size() + ", " + mWhiteList);
            Log.i(TAG, "removeUiList: " + mRemoveUiList.size() + ", " + mRemoveUiList);
            Log.i(TAG, "killProcessList: " + mKillList.size() + ", " + mKillList);
        } catch (IOException | XmlPullParserException e) {
            Log.w(TAG, "readConfig err", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.w(TAG, "readConfig close fis err", e);
                }
            }
        }
    }

    private static class PackageInfo {
        String pkgName;
        String appName;

        @Override
        public String toString() {
            return "PackageInfo{" + "pkgName=" + pkgName + ", appName=" + appName + '}';
        }
    }
}
