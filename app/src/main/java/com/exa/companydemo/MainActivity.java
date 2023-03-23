package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.companydemo.common.AppInfoActivity;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.test.BuildTestToast;
import com.gxa.car.scene.SceneInfo;
import com.gxa.car.scene.SceneManager;
import com.gxa.car.scene.SceneStateListener;
import com.gxa.car.scene.ServiceStateListener;
import com.gxa.car.scene.WindowChangeListener;
import com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static com.exa.companydemo.TestUtil.isRegisterBroadCast;
import static com.exa.companydemo.TestUtil.mReceiver;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {
    private TextView msgT;
    private Button btn1, btn2, btn3, btn4;
    private EditText editText;
    private boolean isFullScreen;
    private Activity mContext;

    private final String PLATFORM_CONFIG_PATH = "/vendor/etc/data/avm_package_info.json";
    private View toastView;
    private WindowManager windowManager;
    private TextView tv;

    @Override
    protected void initData() {
        L.d("initData");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        L.d("MainActivity onConfigurationChanged:" + newConfig.uiMode);
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    protected void initView() {
        mContext = this;
        Tools.setScreenBrightness(this, 50);
        checkPermission();

        TestUtil.registerFullScreenListener(this);
//        TestUtil.registerBroadcast(this);
        setToolbarId(R.id.toolbar);
        editText = findViewById(R.id.edt);
        msgT = findViewById(R.id.msgT);
        btn4 = findViewById(R.id.btn4);
        findViewById(R.id.btnApp).setOnClickListener(view -> {
            L.w("点击Location Test");
            startActivity(new Intent(this, LocationActivity.class));
        });
        findViewById(R.id.btnSystemUI).setOnClickListener(view -> {
            L.w("全屏按钮");
            startActivity(new Intent(this, SystemUITestActivity.class));
        });
        findViewById(R.id.btnPlay).setOnClickListener(view -> {
            L.w("视频播放");
            startActivity(new Intent(this, VideoPlayerActivity.class));
        });
        findViewById(R.id.btn2).setOnClickListener(view -> {
            L.w("点击跳转  到第二个页面");
            startActivity(new Intent(this, AppInfoActivity.class));
        });
        findViewById(R.id.btnMode).setOnClickListener(v -> {//am start com.android.engmode
            Utils.openApp(this, "com.android.engmode");
        });
        btn4.setOnClickListener(view -> {
            L.w("测试按钮");
            test();
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            Tools.hideKeyboard(editText);
            return false;
        });
//        editText.setOnSystemUiVisibilityChangeListener(visibility -> {
//            L.e("OnSystemUiVisibilityChanged: " + visibility);
//        });

//        BaseConstants.getHandler().postDelayed(() -> {
//            test();
//        }, 5000);
        List<String> list = new ArrayList<>();
        String data = "www.www.wwxxsda";
        list.add(data);
        L.d("contains:" + list.contains(data));
    }

    public long getCurrentTimeZoneTimeMillis(int utcDate, double utcTime) {
        String time;
        long result = System.currentTimeMillis();
        if (utcDate <= 0 || utcTime < 0.0d) {
            return result;
        }
        String date = String.valueOf(utcDate);
        String time2 = String.valueOf(utcTime);
        if (time2.contains(".")) {
            time = time2.substring(0, time2.indexOf("."));
        } else {
            time = time2;
        }
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.set(Integer.parseInt(date.substring(date.length() - 2)) + 2000,
                    Integer.parseInt(date.substring(date.length() - 4,
                            date.length() - 2)) - 1,
                    Integer.parseInt(date.substring(0, date.length() - 4)),
                    Integer.parseInt(time.substring(0, time.length() - 4)),
                    Integer.parseInt(time.substring(time.length() - 4, time.length() - 2)),
                    Integer.parseInt(time.substring(time.length() - 2)));
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            L.w("getCurrentTimeZoneTimeMillis time parse err!!!");
            return result;
        }
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
//        TestUtil.copyAssetsFonts(this);

        TestUtil.testFonts(this);
        String personalPath = "/storage/emulated/0/Fonts/etc/fonts_personal.xml";
        File file = new File(personalPath);
        L.d("file:" + file.exists());

//        BuildTestToast.makeMyToast(this);
//        TestUtil.showToast(MainActivity.this);
//        TestUtil.testDialog(this, "111111",2529);
//        TestUtil.usbPermission(this);
//        ScreenUtils.setFullScreen(this);

//        TestUtil.sendBroadcast(this, "com_exa_companydemo_action", null);
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

    private void setText(String msg) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = format.format(new Date());
        runOnUiThread(() -> {
            String n = date + "\t" + msg + "\n" + msgT.getText().toString();
            msgT.setText(n);
        });
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