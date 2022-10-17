package com.exa.companydemo;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;

import java.io.File;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.graphics.fonts.SystemFonts.getAvailableFonts;

public class MainActivity extends BaseActivity {
    private TextView text;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        String screen = "屏幕宽高：" + Tools.getScreenW(this) + "," + Tools.getScreenH(this);
        L.d(screen);
        text = findViewById(R.id.text);
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
        registerBroadcast();
    }

    @Override
    protected int getLayoutId() {
        int flages = getWindow().getAttributes().flags;
        int visiable = getWindow().getDecorView().getSystemUiVisibility();
        int appearance = -2;
        int behavior = -2;
        L.d("flags before:" + flages + ",SystemUiVisibility:" + visiable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getDecorView().getWindowInsetsController();
            if (controller != null) {
                appearance = controller.getSystemBarsAppearance();
                behavior = controller.getSystemBarsBehavior();
                L.d("flags before:" + flages + ",appearance:" + appearance + ",behavior:" + behavior + ",SystemUiVisibility:" + visiable);


//                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
//                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);


                appearance = controller.getSystemBarsAppearance();
                behavior = controller.getSystemBarsBehavior();
            }
            visiable = getWindow().getDecorView().getSystemUiVisibility();
            flages = getWindow().getAttributes().flags;
            L.d("flags after:" + flages + ",appearance:" + appearance + ",behavior:" + behavior + ",SystemUiVisibility:" + visiable);
        }
        return R.layout.activity_main;
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

//        Dialog dialog = new MyDialog(this);
//        dialog.show();
    }

    private class MyDialog extends Dialog {
        private int mGravity;

        public MyDialog(@NonNull Context context) {
            this(context, 0);
        }

        public MyDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        protected MyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mGravity = Gravity.BOTTOM;
            initDialog();
        }

        private void updateWidthHeight(WindowManager.LayoutParams lp) {
            if (lp.gravity == Gravity.TOP || lp.gravity == Gravity.BOTTOM) {
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
        }

        private void initDialog() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.gravity = Gravity.BOTTOM;
            updateWidthHeight(lp);
            getWindow().setAttributes(lp);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.input_layout, null, false);
            setContentView(view);
        }

        private void initDockWindow() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();

            lp.setTitle("模拟输入法");

            lp.gravity = mGravity;
            updateWidthHeight(lp);

            getWindow().setAttributes(lp);

            int windowSetFlags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            int windowModFlags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            boolean mTakesFocus = true;
            if (!mTakesFocus) {
                windowSetFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            } else {
                windowSetFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                windowModFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            }

            getWindow().setFlags(windowSetFlags, windowModFlags);
        }

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