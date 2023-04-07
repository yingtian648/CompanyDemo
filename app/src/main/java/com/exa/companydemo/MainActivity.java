package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.companydemo.common.AppInfoActivity;
import com.exa.companydemo.location.LocationActivity;
import com.gxa.car.scene.SceneInfo;
import com.gxa.car.scene.SceneManager;
import com.gxa.car.scene.WindowChangeListener;
import com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
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
        findViewById(R.id.btnNightMode).setOnClickListener(v -> {//am start com.android.engmode
            L.w("白天黑夜模式");
            UiModeManager modeManager = getSystemService(UiModeManager.class);
            if (modeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                modeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
            } else {
                modeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
            }
        });
        findViewById(R.id.btnMode).setOnClickListener(v -> {//am start com.android.engmode
            Utils.openApp(this, "com.android.engmode");
        });
        btn4.setOnClickListener(view -> {
            L.w("测试按钮");
            setText(L.msg);
            test();
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            Tools.hideKeyboard(editText);
            return false;
        });
//        editText.setOnSystemUiVisibilityChangeListener(visibility -> {
//            L.e("OnSystemUiVisibilityChanged: " + visibility);
//        });
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

    @SuppressLint("WrongConstant")
    private void testFlag() {
        L.d("------------------------------------------------------------");
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        L.d("start SystemUiVisibility:" + window.getDecorView().getSystemUiVisibility());

        int[] systemUiFlags = {
                View.SYSTEM_UI_FLAG_VISIBLE,
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR,
                View.SYSTEM_UI_FLAG_LOW_PROFILE,
                View.SYSTEM_UI_FLAG_IMMERSIVE,
                SYSTEM_UI_FLAG_LAYOUT_STABLE,
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
                SYSTEM_UI_FLAG_HIDE_NAVIGATION,
                View.SYSTEM_UI_FLAG_FULLSCREEN,
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        };
        int option = SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.getDecorView().setVisibility(option);

        L.d("start SystemUiVisibility:" + window.getDecorView().getSystemUiVisibility());
        params.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            params.setFitInsetsTypes(0);
            params.setFitInsetsSides(0);
            WindowInsetsController controller = window.getDecorView().getWindowInsetsController();
            controller.hide(WindowInsets.Type.navigationBars());
        }
        L.d("new flags 222:" + params.flags);

        window.setAttributes(params);
        L.d("------------------------------------------------------------");
        registerTimeUpdateReceiver();
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    private void test() {

//        registerWindowChangedListener();
//        Object fontManager = getSystemService("font");
//        if(fontManager!=null){
//            try {
//                ((IFontManager.Stub)fontManager).updateDefaultFontFamily("SSSS");
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                L.d("updateDefaultFontFamily err");
//            }
//        }else {
//            L.d("FontManagerService","fontManager is null");
//        }

//        testFlag();
//        Toast.makeText(this, "一二三四五六七八一二三四五六七八一二三四五六七八", Toast.LENGTH_LONG).show();
//        TestUtil.showToast(this);

//        TestUtil.copyAssetsFonts(this);
//        TestUtil.testFonts(this);
//        String personalPath = "/storage/emulated/0/Fonts/etc/fonts_personal.xml";
//        File file = new File(personalPath);
//        L.d("file:" + file.exists());

//        BuildTestToast.makeMyToast(this);
//        TestUtil.showToast(MainActivity.this);
//        TestUtil.testDialog(this, "111111",2000);
//        TestUtil.usbPermission(this);
//        ScreenUtils.setFullScreen(this);

//        TestUtil.sendBroadcast(this, "com_exa_companydemo_action", null);

    }

    private void registerTimeUpdateReceiver() {
        L.dd("registerTimeUpdateReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(mTimeUpdateReceiver, filter);
        setText(DateUtil.getNowDateHM());
    }

    private final BroadcastReceiver mTimeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (action == null || action.isEmpty()) return;
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                //系统每1分钟发送一次广播
                L.d(DateUtil.getNowDateHM());
                setText(DateUtil.getNowDateHM());
            } else if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                //系统手动更改时间发送广播
                L.d(DateUtil.getNowDateHM());
                setText(DateUtil.getNowDateHM());
            }
        }
    };


    private void registerWindowChangedListener() {
        SceneManager.getInstance(mContext).addWindowChangeListener(new WindowChangeListener() {
            @Override
            public void onWindowsChanged(SceneInfo sceneInfo, int i) {
                L.d("onWindowsChanged : " + sceneInfo.getPackageName() + ", windowType = " + sceneInfo.getWindowType());
            }

            @Override
            public void onFocusChanged(SceneInfo sceneInfo, SceneInfo sceneInfo1) {
                L.d("onFocusChanged : " + sceneInfo.getPackageName() + ", windowType = " + sceneInfo1.getWindowType());
            }
        });
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