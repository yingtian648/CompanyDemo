package com.exa.companydemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import static android.graphics.fonts.SystemFonts.getAvailableFonts;

public class MainActivity extends BaseActivity {
    private TextView text;
    private EditText editText;

    @Override
    protected void initData() {
        getNavMode();
    }

    @Override
    protected void initView() {
        String screen = "屏幕宽高：" + Tools.getScreenW(this) + "," + Tools.getScreenH(this);
        L.d(screen);
        text = findViewById(R.id.text);
        editText = findViewById(R.id.edt);
        text.setText(screen);
        checkPermission();
        findViewById(R.id.btnApp).setOnClickListener(view -> {
            L.d("点击App信息");
            startActivity(new Intent(this, LocationActivity.class));
            text.setText("点击App信息");
        });
        findViewById(R.id.btn).setOnClickListener(view -> {
            L.d("点击Toast测试1");
            test();
        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            L.d("点击跳转  到第二个页面");
            startActivity(new Intent(this, AppInfoActivity.class));
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Tools.hideKeyboard(editText);
                return false;
            }
        });
        registerBroadcast();
    }

    @Override
    protected int getLayoutId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getDecorView().getWindowInsetsController();
            if (controller != null) {
                controller.addOnControllableInsetsChangedListener(new WindowInsetsController.OnControllableInsetsChangedListener() {
                    @Override
                    public void onControllableInsetsChanged(@NonNull WindowInsetsController controller, int typeMask) {
                        if (WindowInsets.Type.statusBars() == typeMask) {
                            L.d("InsetsChanged:" + typeMask + ",controller:" + controller.getClass());

                            WindowInsets windowInsets = new  WindowInsets.Builder().build();
                            L.d("InsetsChanged windowInsets:" + windowInsets.isVisible(WindowInsets.Type.navigationBars()));
                            getNavMode();
                        }
                    }
                });
                getNavMode();
                L.d("-----------------------------------------------");
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
//                controller.hide(WindowInsets.Type.navigationBars());
                controller.hide(WindowInsets.Type.statusBars());
            }
        }
        return R.layout.activity_main;
    }

    private void getNavMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets windowInsets = WindowInsets.CONSUMED;
            L.d("hasInsets:" + windowInsets.hasInsets() + ",isConsumed:" + windowInsets.isConsumed());
            L.d("NavigationBar is visible:" + windowInsets.getInsets(WindowInsets.Type.navigationBars()));
            L.d("StatusBars is visible:" + windowInsets.getInsets(WindowInsets.Type.statusBars()));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d("onResume");
    }

    private void test() {
        BaseConstants.getFixPool().execute(() -> {//获取字体
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Set<Font> fonts = getAvailableFonts();
                for (Font font : fonts) {
                    L.d(font.toString());
                }
            }
        });
    }

    private void checkPermission() {
//        PermissionUtil.requestPermission(this, () -> {
//            L.d("已授权 读写权限");
//        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("main onReceived:" + intent.getAction());
            String action = intent.getAction();
            switch (action) {
                case BaseConstants.ACTION_MY_PROVIDER_SCAN_FINISH://自定义媒体扫描完成
                    break;
                case Intent.ACTION_MEDIA_MOUNTED://挂载
                    startMediaScannerService(context, intent);
                    break;
                case Intent.ACTION_MEDIA_UNMOUNTED://卸载
                    break;
                case Intent.ACTION_MEDIA_SCANNER_STARTED://扫描开始
                    break;
                case Intent.ACTION_MEDIA_SCANNER_FINISHED://扫描结束

                    break;
            }
        }
    };

    private void registerBroadcast() {
        L.d("registerBroadcast");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);
    }

    private void startMediaScannerService(Context context, Intent intentRes) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}