package com.exa.companydemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.EditText;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.companydemo.utils.LocationInfo;
import com.exa.companydemo.utils.LogTools;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.utils.SatInfo_t;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity {
    private TextView text, text1, text2, text3, text4;
    private EditText editText;
    private final String fontTestWords = "Innovation in China 中国制造，惠及全球 0123456789";

    @Override
    protected void initData() {
        getNavMode();
    }

    @Override
    protected void initView() {
        String screen = "屏幕宽高：" + Tools.getScreenW(this) + "," + Tools.getScreenH(this);
        L.d(screen);
        text = findViewById(R.id.text);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        editText = findViewById(R.id.edt);
        checkPermission();
        findViewById(R.id.btnApp).setOnClickListener(view -> {
            L.d("点击Location Test");
            startActivity(new Intent(this, LocationActivity.class));
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

        registerBroadcast(mReceiver);


        text.setText(fontTestWords + "   Default");
        text1.setText(fontTestWords + "   NotoSansHans");
        text2.setText(fontTestWords + "   sans-serif");
        text3.setText(fontTestWords + "   serif");
        text4.setText(fontTestWords + "   monospace");
        Typeface aDefault = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

        Typeface GacFont = Typeface.create("NotoSansHans", Typeface.BOLD);
        Typeface sans_serif = Typeface.create("sans-serif", Typeface.BOLD);
        Typeface serif = Typeface.create("serif", Typeface.BOLD);
        Typeface monospace = Typeface.create("monospace", Typeface.BOLD);
        text.setTypeface(aDefault);
        text1.setTypeface(GacFont);
        text2.setTypeface(sans_serif);
        text3.setTypeface(serif);
        text4.setTypeface(monospace);

        L.d("GacFont is Default ? " + (GacFont.equals(aDefault)));
        L.d("sans-serif is Default ? " + (sans_serif.equals(aDefault)));
        L.d("serif is Default ? " + (serif.equals(aDefault)));
        L.d("monospace is Default ? " + (monospace.equals(aDefault)));

        getDataJson();
    }

    @Override
    protected int getLayoutId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getDecorView().getWindowInsetsController();
            if (controller != null) {
//                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
//                controller.hide(WindowInsets.Type.navigationBars());
//                controller.hide(WindowInsets.Type.statusBars());
            }
        } else {
            int uiOpts = View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(uiOpts);
        }
        return R.layout.activity_main;
    }

    private void getNavMode() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            WindowInsets windowInsets = WindowInsets.CONSUMED;
//            L.d("hasInsets:" + windowInsets.hasInsets() + ",isConsumed:" + windowInsets.isConsumed());
//            L.d("NavigationBar is visible:" + windowInsets.isVisible(WindowInsets.Type.navigationBars()));
//            L.d("StatusBars is visible:" + windowInsets.isVisible(WindowInsets.Type.statusBars()));
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        L.d("onResume");
//        getNavMode();

//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/Weixin";
//        LogTools.logAudioFileAttr(path);
    }

    private void test() {
        LogTools.logSystemFonts();
    }

    private void checkPermission() {
        String[] ps = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ps = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE};
        }
        PermissionUtil.requestPermission(this, () -> {
            L.d("已授权 读写权限");
        }, ps);
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
        filter.addDataScheme("file");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void getDataJson() {
        LocationInfo location = new LocationInfo();
        location.setUTCTime(System.currentTimeMillis());
        location.setUTCDate(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        location.setLongitude(104.03453435);
        location.setLatitude(34.5624251);
        location.setSpeed(20f);//单位：米/秒
        location.setAltitude(600);//海拔
        location.setHorizontalAccuracy(4f);//启动后的纳秒数，包括睡眠时间
        location.setDirection(260);//精度
        location.setVerticalAccuracy(3f);
        location.setBearingAccuracy(5f);
        location.setSpdAccuracy(5f);
        location.setSatAvailable((byte) 0);
        location.setGNSSValidFlag((byte) 1);
        SatInfo_t satInfoT = new SatInfo_t();
        satInfoT.azimuth = 30;
        satInfoT.elevation = 128000;
        satInfoT.pRN = 1;
        satInfoT.sNR = 1;
        location.setSatInfoList(new SatInfo_t[]{satInfoT});
        String json = new Gson().toJson(location);
        L.d("---------------------------------------------------------------------------------");
        L.d(json);
        byte [] jb = json.getBytes(StandardCharsets.UTF_8);

        L.d("---------------------------------------------------------------------------------");
    }
}