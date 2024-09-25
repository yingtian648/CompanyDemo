package com.exa.companyclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.CrashHandle;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companyclient.utils.TestUtil;
import com.gxatek.cockpit.shortcut.allMenu.AllMenuPanelActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context app;
    private final List<Activity> activityList = new ArrayList<>();
    private static Call call;

    public static void setCall(Call call) {
        App.call = call;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        L.init("MCompanyClient", true);
        BaseConstants.init();
        CrashHandle.getInstance().init(this);
        Tools.logScreenWH(this);
        registerAllMenuClickBroadCast();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                L.dd("---App---" + activity.getLocalClassName());
                activityList.add(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                L.dd("---App---" + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                L.dd("---App---" + activity.getLocalClassName());
                activityList.remove(activity);
            }
        });
    }

    public static Context getContext() {
        return app;
    }

    private final String action = "com.gxatek.cockpit.systemui.ALL_MENU_CLICK";
    private final String param = "visible";

    private void registerAllMenuClickBroadCast() {
        IntentFilter filter = new IntentFilter(action);
        registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean visible = intent.getBooleanExtra(param, false);
            L.dd("visible = " + visible);
            if (visible) {
                showAllMenu();
            } else {
                hideAllMenu();
            }
        }
    };

    public interface Call {
        void onFinish();
    }

    private TestUtil testUtil = new TestUtil();

    private void showAllMenu() {
        Intent start = new Intent(App.this, AllMenuPanelActivity.class);
        start.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(start);

//        testUtil.showDialog(this);
    }

    private void hideAllMenu() {
        for (Activity a : activityList) {
            if (a instanceof AllMenuPanelActivity) {
//                        a.moveTaskToBack(true);
//                        a.overridePendingTransition(R.anim.pop_in_enter, R.anim.pop_in_exit);
                a.finish();
//                call.onFinish();
            }
        }

//        testUtil.hideDialog();
    }


}
