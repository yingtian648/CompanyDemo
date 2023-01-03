package com.exa.companydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
//import android.widget.CarToast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.GpsConvertUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.utils.LogTools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;

import static android.content.res.Configuration.UI_MODE_NIGHT_YES;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {
    private TextView topT, text, text1, text2, text3, text4, text5, text6;
    private Button btn1, btn2, btn3, btn4;
    private EditText editText;
    private boolean isFullScreen;
    private Context mContext;
    private final String fontTestWords = "Innovation in China 中国制造，惠及全球 0123456789";
    private boolean isRegisterBroadCast;

    @Override
    protected void initData() {
        testFonts();
    }

    @Override
    protected void initView() {

        int date = 10123;
        double time = 123403.588;
        L.d(date + " " + time + " 时间转换：" + GpsConvertUtil.getCurrentTimeZoneTimeMillis(date, time));

        mContext = this;
//        registerBroadcast(mReceiver);

        editText = findViewById(R.id.edt);
        topT = findViewById(R.id.topT);
        btn4 = findViewById(R.id.btn4);
        checkPermission();
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
            L.d("点击Toast测试1");
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
        findViewById(R.id.btn4).setOnClickListener(view -> {
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

        btn4.setText("CarToast测试");
    }

    /**
     * Creates {@link WindowManager.LayoutParams} with default values for toasts.
     */
    @SuppressLint("WrongConstant")
    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //modify by dongjiao.tang 20220722
        params.type = 2503;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            params.setFitInsetsIgnoringVisibility(true);
        }

        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                // modify by caozhi,2022-11-15
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // end of modification
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        return params;
    }

    @SuppressLint("RestrictedApi")
    private void test() {
        TestUtil.showToast(this);
//        try {
////            TestUtil.sendBroadcast(this, "com.gxatek.cockpit.systemui.ALL_MENU_CLICK", "com.gxatek.cockpit.shortcut");
//            TestUtil.sendBroadcast(this, "com.gxatek.cockpit.systemui.ALL_MENU_CLICK", null);
////            TestUtil.sendBroadcast(this, "com.gxa.guide.display", getPackageName());
////            TestUtil.sendBroadcast(this,"com.exa.companyclient.ACTION_OPEN_CLIENT", "com.exa.companyclient");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            L.e("sendBroadcast Exception:" + e.getMessage());
//        }

        registerBroadcast(mReceiver);
    }

    @Override
    protected int getLayoutId() {
//        ScreenUtils.setFullScreen(this);
        StatubarUtil.setStatusBarInvasion(this, false);
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        L.d("onResume");
//        getNavMode();

//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/Weixin";
//        LogTools.logAudioFileAttr(path);

//        Intent intent = new Intent();
//        intent.setPackage("com.exa.companyclient");
//        intent.setAction("com.gxatek.cockpit.systemui.ALL_MENU_CLICK");
//        L.d("&& FLAG_RECEIVER_REGISTERED_ONLY ：：" + ((intent.getFlags() & Intent.FLAG_RECEIVER_REGISTERED_ONLY) == 0));
//        L.d("intent#getComponent#getPackageName ：：" + (intent.getComponent() == null ? "null" : intent.getComponent().getPackageName()));
//        L.d("intent#getSelector ：：" + (intent.getSelector() == null ? "null" : intent.getSelector().getAction()));
//        L.d("intent#getPackage ：：" + (intent.getPackage() == null ? "null" : intent.getPackage()));
    }

    private void checkPermission() {
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
        checkPermission();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("onReceive:" + intent.getAction());
        }
    };

    private final String ACTION_guide_dismiss = "com.gxa.guide.dismiss";
    private final String ACTION_launcher = "com.gxatek.cockpit.launcher.LAUNCHER_SCENE_CHANGED";
    private final String ACTION_guide_display = "com.gxa.guide.display";
    private final String ACTION_timeSync = "com.gxa.car.timesync.clock.action.update.time";
    private final String ACTION_schedule = "update_schedule_widget";
    private final String ACTION_CloseScreen = "com.gxatek.cockpit.carsetting.CloseScreen";
    private final String ACTION_Open_panel = "com.gxatek.cockpit.systemui.ALL_MENU_CLICK";

    private void registerBroadcast(BroadcastReceiver receiver) {
        L.d("registerBroadcast");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
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
        registerReceiver(receiver, filter);
        isRegisterBroadCast = true;
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

    /**
     * 调试字体
     */
    private void testFonts() {
        text = findViewById(R.id.text);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        text6 = findViewById(R.id.text6);

        text.setText(fontTestWords + "   Default");
        text1.setText(fontTestWords + "   SourceHanSansCN");
        text2.setText(fontTestWords + "   sans-serif");
        text3.setText(fontTestWords + "   serif");
        text4.setText(fontTestWords + "   monospace");
        text5.setText(fontTestWords + "   GacFont");
        text6.setText(fontTestWords + "   多行end省略");

        Typeface aDefault = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        Typeface SourceHanSansCN = Typeface.create("SourceHanSansCN", Typeface.BOLD);
        Typeface sans_serif = Typeface.create("sans-serif", Typeface.BOLD);
        Typeface serif = Typeface.create("serif", Typeface.BOLD);
        Typeface monospace = Typeface.create("monospace", Typeface.BOLD);
        Typeface GacFont = Typeface.create("GacFont", Typeface.BOLD);

        text.setTypeface(aDefault);
        text1.setTypeface(SourceHanSansCN);
        text2.setTypeface(sans_serif);
        text3.setTypeface(serif);
        text4.setTypeface(monospace);
        text5.setTypeface(GacFont);

//        L.d("SourceHanSansCN is Default ? " + (SourceHanSansCN.equals(aDefault)));
//        L.d("sans-serif is Default ? " + (sans_serif.equals(aDefault)));
//        L.d("serif is Default ? " + (serif.equals(aDefault)));
//        L.d("monospace is Default ? " + (monospace.equals(aDefault)));
//        L.d("GacFont is Default ? " + (GacFont.equals(aDefault)));
//
//        LogTools.logSystemFonts();
    }
}