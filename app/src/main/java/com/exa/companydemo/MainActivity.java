package com.exa.companydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.companydemo.common.AppInfoActivity;
import com.exa.companydemo.location.LocationActivity;
import com.gxa.car.scene.SceneInfo;
import com.gxa.car.scene.SceneManager;
import com.gxa.car.scene.WindowChangeListener;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    protected int getLayoutId() {
//        ScreenUtils.setFullScreen(this);
        StatubarUtil.setStatusBarInvasion(this, false);
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        L.d("initData");
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    protected void initView() {
        mContext = this;
        windowManager = App.getContext().getSystemService(WindowManager.class);
        Tools.setScreenBrightness(this, 50);
        modeManager = getSystemService(UiModeManager.class);
        boolean night = modeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES;
        L.d("黑夜模式：" + night);
//        checkPermission();
        TestUtil.registerFullScreenListener(this);
//        TestUtil.registerBroadcast(this);
//        setToolbarId(R.id.toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        editText = findViewById(R.id.edt);
        msgT = findViewById(R.id.msgT);
        btn4 = findViewById(R.id.btn4);
        findViewById(R.id.btnApp).setOnClickListener(view -> {
            L.w("点击Location Test");
            startActivity(new Intent(this, LocationActivity.class));
        });
        findViewById(R.id.btnSystemUI).setOnClickListener(view -> {
            L.w("全屏按钮");
            isFullScreen = !isFullScreen;
            if (isFullScreen) {
                ScreenUtils.hideStatusBars(this);
            } else {
                ScreenUtils.showStatusBars(this);
            }
//            startActivity(new Intent(this, SystemUITestActivity.class));
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

        String cameraConfig = SystemProperties.get("ro.sys.ivi.eol.camera.supported");
        if (cameraConfig != null && cameraConfig.length() > 0) {
            try {
                L.dd("cameraConfig=" + Integer.parseInt(cameraConfig));
            } catch (NumberFormatException e) {
                L.de(e);
            }
        }
        L.dd("cameraConfig=" + cameraConfig);
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    private void test() {
//        checkPermission();
        TestUtil.testFonts(this);

//        TestUtil.showToast(this);
//        startService(new Intent(this, DemoService.class));
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

//        TestUtil.testDialog(this,"1111111",2501);

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
//        checkPermission();
//        bindScreenSaver();
//        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.dd();
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