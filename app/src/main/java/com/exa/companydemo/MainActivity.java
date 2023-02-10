package com.exa.companydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.location.GnssAntennaInfo;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.server.location.gnss.IExtLocationCallback;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.GpsConvertUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.location.demo.LocationServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static com.exa.companydemo.TestUtil.isRegisterBroadCast;
import static com.exa.companydemo.TestUtil.mReceiver;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {
    private TextView topT;
    private Button btn1, btn2, btn3, btn4;
    private EditText editText;
    private boolean isFullScreen;
    private Activity mContext;

    @Override
    protected void initData() {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initView() {
        mContext = this;
        Tools.setScreenBrightness(this,50);
        checkPermission();

//        TestUtil.registerBroadcast(this);

        editText = findViewById(R.id.edt);
        topT = findViewById(R.id.topT);
        btn4 = findViewById(R.id.btn4);
        topT.setOnClickListener(v -> {
//            topT.setText(DateUtil.getNowTime() + " 点击了");
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            L.d("白天模式？" + (currentNightMode == Configuration.UI_MODE_NIGHT_NO));
            topT.setText("白天模式？" + (currentNightMode == Configuration.UI_MODE_NIGHT_NO));
            getResources().getConfiguration().uiMode = UI_MODE_NIGHT_YES;
//            getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
        findViewById(R.id.btnApp).setOnClickListener(view -> {
            L.d("点击Location Test");
            startActivity(new Intent(this, LocationActivity.class));
        });
        findViewById(R.id.btn).setOnClickListener(view -> {
            L.d("全屏");
//            startActivity(new Intent(this, VideoPlayerActivity.class));
            if (isFullScreen) {
                ScreenUtils.showStatusBars(this);
            } else {
                ScreenUtils.setFullScreen(this);
            }
            isFullScreen = !isFullScreen;
        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            L.d("点击跳转  到第二个页面");
            startActivity(new Intent(this, AppInfoActivity.class));
        });
        btn4.setOnClickListener(view -> {
            L.d("测试按钮");
            test();
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Tools.hideKeyboard(editText);
                return false;
            }
        });
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    private void test() {
        L.dd("1212");
//        TestUtil.showToast(this);
//        L.d("IBinder.FLAG_ONEWAY=" + IBinder.FLAG_ONEWAY);
//        TestUtil.usbPermission(this);
//        ScreenUtils.setFullScreen(this);

//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.dvr.app", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.dvr.widge", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.dvr.broadcast.PendingIntentReceiver", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.weather.updateFirst", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.weather.update", null);

//        TestUtil.registerBroadcast(this);

//       testDialog();
    }

    @Override
    protected int getLayoutId() {
//        ScreenUtils.setFullScreen(this);
        StatubarUtil.setStatusBarInvasion(this, false);
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