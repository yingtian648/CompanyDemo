package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.location.Location;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.common.AppInfoActivity;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.service.MAidlService;
import com.exa.companydemo.utils.PhoneManagerServiceTemp;
import com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static com.exa.companydemo.TestUtil.isRegisterBroadCast;
import static com.exa.companydemo.TestUtil.mReceiver;
import static java.text.DateFormat.FULL;
import static java.util.TimeZone.LONG;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {
    private TextView topT;
    private Button btn1, btn2, btn3, btn4;
    private EditText editText;
    private boolean isFullScreen;
    private Activity mContext;

    private final String PLATFORM_CONFIG_PATH = "/vendor/etc/data/avm_package_info.json";

    @Override
    protected void initData() {
        L.d("initData");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        L.d("onConfigurationChanged:" + newConfig.uiMode);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initView() {
        mContext = this;
        Tools.setScreenBrightness(this, 50);
        checkPermission();

        TestUtil.registerFullScreenListener(this);
//        TestUtil.registerBroadcast(this);

        editText = findViewById(R.id.edt);
        topT = findViewById(R.id.topT);
        btn4 = findViewById(R.id.btn4);
        topT.setOnClickListener(v -> {
//            topT.setText(DateUtil.getNowTime() + " 点击了");
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            L.w("白天模式？" + (currentNightMode == Configuration.UI_MODE_NIGHT_NO));
            topT.setText("白天模式？" + (currentNightMode == Configuration.UI_MODE_NIGHT_NO));
            getResources().getConfiguration().uiMode = UI_MODE_NIGHT_YES;
//            getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
        findViewById(R.id.btnApp).setOnClickListener(view -> {
            L.w("点击Location Test");
            startActivity(new Intent(this, LocationActivity.class));
        });
        findViewById(R.id.btn).setOnClickListener(view -> {
            L.w("全屏按钮");
//            startActivity(new Intent(this, VideoPlayerActivity.class));
            if (isFullScreen) {
                ScreenUtils.showStatusBars(this);
            } else {
                ScreenUtils.setFullScreen(this);
            }
            isFullScreen = !isFullScreen;
        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            L.w("点击跳转  到第二个页面");
            startActivity(new Intent(this, AppInfoActivity.class));
        });
        btn4.setOnClickListener(view -> {
            L.w("测试按钮");
            test();
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Tools.hideKeyboard(editText);
                return false;
            }
        });
        editText.setOnSystemUiVisibilityChangeListener(visibility -> {
            L.e("OnSystemUiVisibilityChanged: " + visibility);
        });
    }

    private void bindScreenSaver() {
        Intent intent = new Intent("com.gxatek.cockpit.screensaver");
        intent.setClassName("com.gxatek.cockpit.screensaver", "com.gxatek.cockpit.screensaver.ScreenSaverService");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IScreenSaverViewAidl aidl = IScreenSaverViewAidl.Stub.asInterface(service);
                try {
                    aidl.startScreenViews();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    L.e("err:" + e.getMessage());
                } finally {
                    finish();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    private void test() {

//        TestUtil.testDialog(this);
//        new PhoneManagerServiceTemp();

//        TestUtil.showToast(MainActivity.this);
//        L.d("IBinder.FLAG_ONEWAY=" + IBinder.FLAG_ONEWAY);
//        TestUtil.usbPermission(this);
//        ScreenUtils.setFullScreen(this);

//        TestUtil.sendBroadcast(this, "com_exa_companydemo_action", null);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com_exa_companydemo_action");
//        L.dd("registerReceiver");
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                L.dd(intent.getAction());
//            }
//        }, filter);

        Intent intent = new Intent();
        intent.setClassName("com.android.server.statusbar","com.android.server.statusbar.StatusBarManagerService");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                L.d("onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                L.d("onServiceConnected");
            }
        },Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.dd();

//        bindScreenSaver();
//        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.dd();
    }

    @Override
    protected int getLayoutId() {
//        ScreenUtils.setFullScreen(this);
//        StatubarUtil.setStatusBarInvasion(this, false);
        return R.layout.activity_main;
    }

    private void checkPermission() {
//        requestPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},1);
//        String[] ps = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            ps = new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE};
//        }
//        PermissionUtil.requestPermission(this, () -> {
//            L.d("已授权读写权限");
//        }, ps);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterBroadCast)
            unregisterReceiver(mReceiver);
    }
}