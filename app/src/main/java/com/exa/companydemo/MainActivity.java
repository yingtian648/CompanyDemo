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
import android.view.KeyEvent;
import android.view.WindowManager;
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
    private EditText editText;
    private boolean isFullScreen;
    private Context mContext;
    private final String fontTestWords = "Innovation in China 中国制造，惠及全球 0123456789";

    @Override
    protected void initData() {
        testFonts();
    }

    @Override
    protected void initView() {
        mContext = this;
        registerBroadcast(mReceiver);

        editText = findViewById(R.id.edt);
        topT = findViewById(R.id.topT);
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
        double w = 102.5962240000;
        double lon = 129.7029650020;
        L.d("convert lat:" + GpsConvertUtil.convertCoordinates(lon));
        L.d("convert1 lat:" + ddmmTodddd1(lon));

        //20220315163333
        int date = 15032022;
        double time = 233333.11;

        long result = GpsConvertUtil.getCurrentTimeZoneTimeMillis(date,time);
        SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        L.d(String.valueOf(result));
        L.d(sdfOut.format(new Date(result)));
        L.d("-------------------------");
        setTime(date,time);
    }

    private void setTime(int utcDate, double utcTime) {
        long result = 0;
        if (utcDate != 0) {
            String date = String.valueOf(utcDate);
            String time = String.valueOf(utcTime);
            if (time.contains(".")) {
                if (time.indexOf(".") < 6) {
                    StringBuilder addO = new StringBuilder();
                    for (int i = 0; i < (6 - time.indexOf(".")); i++) {
                        addO.append("0");
                    }
                    time = addO + time;
                }
            } else if (time.length() < 6) {
                StringBuilder addO = new StringBuilder();
                for (int i = 0; i < (6 - time.length()); i++) {
                    addO.append("0");
                }
                time = addO + time;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss.SSS",Locale.getDefault());
            SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                Date dateGmt = sdf.parse(date + time);
                if (dateGmt != null) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    calendar.setTime(dateGmt);
                    calendar.setTimeZone(TimeZone.getDefault());
                    sdfOut.format(calendar.getTime());
                    result = calendar.getTimeInMillis();
                    L.d(String.valueOf(result));
                    L.d(sdfOut.format(calendar.getTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param res 2302.4545412/11325.45451212
     * @return
     */
    private double ddmmTodddd1(double res) {
        if (res == 0.0) {
            return res;
        }
        String resStr = String.valueOf(res);
        if (resStr.indexOf(".") < 2) {
            return res;
        }
        String wmm = resStr.substring(resStr.indexOf(".") - 2);
        String wdd = resStr.substring(0, resStr.indexOf(".") - 2);
        double wmm_a = Double.parseDouble(wmm) / 60;
        return Double.parseDouble(wdd) + wmm_a;
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

    private void test() {
        String msg = "撒谎吉萨号登机口啥叫啊十大建设大家";
//        msg = "撒谎吉萨号登机口啥叫啊十大建设大家好刷道具卡啥叫看到啥就肯定会刷卡机打算结婚的卡";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


//        WindowManager.LayoutParams mParams = createLayoutParams();
//        mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//        mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//        mParams.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            mParams.setFitInsetsTypes(0);
//            mParams.setFitInsetsSides(0);
//        }
//        mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//        final WindowManager mWindowManager = getSystemService(WindowManager.class);
//        final View view = LayoutInflater.from(this).inflate(R.layout.transient_notification, null, false);
//        TextView textView = view.findViewById(android.R.id.message);
//        textView.setText(msg);
//        mWindowManager.addView(view, mParams);
//
//        BaseConstants.getHandler().postDelayed(()->{
//            mWindowManager.removeView(view);
//        },2000);

//        Toast toast = new Toast(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.transient_notification, null, false);
//        TextView textView = view.findViewById(android.R.id.message);
//        textView.setText(msg);
//        toast.setView(view);
//        toast.setGravity(Gravity.TOP, 0, 8);
//        toast.getView().setFitsSystemWindows(false);
//        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        toast.getView().setSystemUiVisibility(option);
//        toast.show();
//        final Intent intent = new Intent();
//        try {
//            intent.setAction(ACTION_guide_dismiss);
//            sendBroadcast(intent);
//        } catch (Throwable e) {
//            L.e("sendBroadcast err:" + intent.getAction());
//        }
//        editText.postDelayed(() -> {
//            try {
//                intent.setAction(ACTION_launcher);
//                sendBroadcast(intent);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                L.e("sendBroadcast err:" + intent.getAction());
//            }
//        }, 1000);
//        editText.postDelayed(() -> {
//            try {
//                intent.setAction(ACTION_guide_display);
//                sendBroadcast(intent);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                L.e("sendBroadcast err:" + intent.getAction());
//            }
//        }, 2000);
//        editText.postDelayed(() -> {
//            try {
//                intent.setAction(ACTION_timeSync);
//                sendBroadcast(intent);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                L.e("sendBroadcast err:" + intent.getAction());
//            }
//        }, 3000);
//        editText.postDelayed(() -> {
//            try {
//                intent.setAction(ACTION_schedule);
//                sendBroadcast(intent);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                L.e("sendBroadcast err:" + intent.getAction());
//            }
//        }, 4000);
//        editText.postDelayed(() -> {
//            try {
//                intent.setAction(ACTION_CloseScreen);
//                sendBroadcast(intent);
//            } catch (Throwable e) {
//                e.printStackTrace();
//                L.e("sendBroadcast err:" + intent.getAction());
//            }
//        }, 5000);
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
    }

    private void checkPermission() {
        String[] ps = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ps = new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE};
        }
        PermissionUtil.requestPermission(this, () -> {
            L.d("已授权读写权限");
        }, ps);
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
//        filter.addDataScheme("file");//for MediaProvider
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

//        LogTools.logSystemFonts();
    }
}