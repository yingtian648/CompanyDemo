package com.zlingsmart.demo.mtestapp.carpower;

import android.app.ActivityManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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

import androidx.annotation.NonNull;

/**
 * @author lsh
 * @date 2024/11/1 9:36
 * @description
 */
public class FordCarPowerStrPolicy {
    private static final String TAG = "FordCarPowerStrPolicy";
    private static final String CONFIG_FILE = "/etc/ford/ford_car_power_str_policy.xml";
    private static final String FIELD_WHITE_LIST = "white_list";
    private static final String FIELD_REMOVE_UI = "remove_ui";
    private static final String FIELD_KILL_PROCESS = "kill_process";
    private static final String FIELD_PACKAGE = "package";
    private final List<PackageInfo> mWhiteList = new ArrayList<>();
    private final List<PackageInfo> mRemoveUiList = new ArrayList<>();
    private final List<PackageInfo> mKillList = new ArrayList<>();
    private Context mContext;
    private final H mHandler;
    private static final int MSG_READ_POLICY = 1;
    private static final int MSG_DO_STR_POLICY = 2;

    public FordCarPowerStrPolicy(Context context) {
        mContext = context;
        HandlerThread handlerThread = new HandlerThread("FordCarPowerStrPolicy");
        handlerThread.start();
        mHandler = new H(handlerThread.getLooper());
        mHandler.sendEmptyMessage(MSG_READ_POLICY);
    }

    /**
     * use by FordCarPowerManagementService
     * 1.we will not care white list.
     * 2.If the app is in removeUiList and the recent task list, we will delete the app's UI.
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
        // get recent task
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        IActivityTaskManager mAtm = ActivityTaskManager.getService();
        List<ActivityManager.RecentTaskInfo> tasks = activityManager.getRecentTasks(Integer.MAX_VALUE,0);
        if (tasks != null) {
            for (ActivityManager.RecentTaskInfo task : tasks) {
                if (task.baseIntent.getComponent() == null) {
                    continue;
                }
                String pkgName = task.baseIntent.getComponent().getPackageName();
                Log.i(TAG, "recentTask:" + pkgName);
                for (PackageInfo packageInfo : mRemoveUiList) {
                    if (pkgName.equals(packageInfo.pkgName)) {
                        Log.i(TAG, "removeUi " + pkgName);

                    }
                }

            }
        }
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        if (processes != null) {
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                Log.i(TAG, "process:" + process.processName);
                for (PackageInfo packageInfo : mKillList) {
                    if (process.processName.equals(packageInfo.pkgName)) {
                        Log.i(TAG, "forceStopPackage " + process.processName);
//                        activityManager.forceStopPackage(process.processName);
                        break;
                    }
                }
            }
        }
    }


    private void readConfig() {
        Log.i(TAG, "readConfig start");
        File file = new File(CONFIG_FILE);
        if (file.exists() && file.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(file);

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
                                packageInfo.appName = parser.getAttributeValue(null, "name");
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
                fis.close();
                Log.i(TAG, "whiteList: " + mWhiteList.size() + ", " + mWhiteList);
                Log.i(TAG, "removeUiList: " + mRemoveUiList.size() + ", " + mRemoveUiList);
                Log.i(TAG, "killProcessList: " + mKillList.size() + ", " + mKillList);
            } catch (IOException | XmlPullParserException e) {
                Log.w(TAG, "readConfig err", e);
            }
        } else {
            Log.e(TAG, "readConfig err: fill not exist!! " + CONFIG_FILE);
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
