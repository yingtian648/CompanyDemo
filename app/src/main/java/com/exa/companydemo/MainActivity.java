package com.exa.companydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Instrumentation;
import android.app.NotificationManager;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Button;
import android.widget.CarToast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.companydemo.accessibility.AccessibilityHelper;
import com.exa.companydemo.accessibility.MyAccessibilityService;
import com.exa.companydemo.common.AppInfoActivity;
import com.exa.companydemo.location.LocationActivity;
import com.gxa.car.scene.SceneInfo;
import com.gxa.car.scene.SceneManager;
import com.gxa.car.scene.WindowChangeListener;
import com.gxatek.cockpit.screensaver.aidl.IScreenSaverViewAidl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private UiModeManager modeManager;

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
        modeManager = getSystemService(UiModeManager.class);
        boolean night = modeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES;
        L.d("黑夜模式：" + night);
//        checkPermission();

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
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    private void test() {
//        BaseConstants.getHandler().postDelayed(() -> {
//            Toast.makeText(this,"121212",Toast.LENGTH_SHORT).show();
//            manager.cancelAll();
//        }, 3000);
//        checkPermission();
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
//        final Toast toast = Toast.makeText(this, "一二三四五六七八一二三四五六七八一二三四五六七八", Toast.LENGTH_LONG);
//        toast.show();
//        btn4.postDelayed(()->{
//            toast.cancel();
//        },1500);
//        TestUtil.showToast(this);

//        TestUtil.copyAssetsFonts(this);
//        TestUtil.testFonts(this);
//        String personalPath = "/storage/emulated/0/Fonts/etc/fonts_personal.xml";
//        File file = new File(personalPath);
//        L.d("file:" + file.exists());

//        BuildTestToast.makeMyToast(this);
//        TestUtil.showToast(MainActivity.this);
//        TestUtil.testDialog(this, "111111", 2000);
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
        String[] ps = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
//        requestPermissions(ps,1);
        PermissionUtil.requestPermission(this, () -> {
            L.d("已授权读写权限");
        }, ps);
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
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterBroadCast)
            unregisterReceiver(mReceiver);
    }
}