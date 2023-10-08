package com.exa.companydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.util.ScreenshotHelper;
import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;

import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.companydemo.common.AppInfoActivity;
import com.exa.companydemo.locationtest.LocationActivity;
import com.exa.companydemo.test.BuildTestToast;
import com.exa.companydemo.utils.NetworkManager;
import com.exa.companydemo.widget.EndLineView;
import com.exa.companydemo.widget.Titlebar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

import androidx.core.app.ActivityCompat;

import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;
import static com.exa.companydemo.TestUtil.isRegisterBroadCast;
import static com.exa.companydemo.TestUtil.mReceiver;
import static com.exa.companydemo.utils.Tools.screenshotView;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {
    private TextView msgT;
    private Button btn1, btn2, btn3, btn4;
    private EndLineView endLineView;
    private EditText editText;
    private boolean isFullScreen;
    private Activity mContext;

    private final String PLATFORM_CONFIG_PATH = "/vendor/etc/data/avm_package_info.json";
    private View toastView;
    private WindowManager windowManager;
    private TextView tv;
    private UiModeManager modeManager;
    private int index = 0;
    private boolean isGome;
    private NetworkManager networkManager;
    private MediaProjectionManager mProjectionManager;

    @Override
    protected int getLayoutId() {
//        ScreenUtils.setFullScreen(this);
//        StatubarUtil.setStatusBarInvasion(this, false);
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        L.d("initData");
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    protected void initView() {
        mContext = this;
        windowManager = App.getContext().getSystemService(WindowManager.class);
        Tools.setScreenBrightness(this, 50);
        modeManager = getSystemService(UiModeManager.class);
        L.d("黑夜模式：" + TestUtil.getUiModeStr(modeManager));
//        checkPermission();
//        TestUtil.registerFullScreenListener(this);
//        TestUtil.registerBroadcast(this);
//        setToolbarId(R.id.toolbar);
        Titlebar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        editText = findViewById(R.id.edt);
        msgT = findViewById(R.id.msgT);
        btn4 = findViewById(R.id.btn4);
        endLineView = findViewById(R.id.elv);
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
        findViewById(R.id.btn3).setOnClickListener(view -> {
            L.w("进入测试页");
            startActivity(new Intent(this, TestActivity.class));
        });
        findViewById(R.id.btnNightMode).setOnClickListener(v -> {//am start com.android.engmode
            L.w("白天黑夜模式");
            if (modeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                modeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
            } else {
                modeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
            }
            BaseConstants.getHandler().postDelayed(() ->
                    L.w("白天黑夜模式:" + TestUtil.getUiModeStr(modeManager)), 2000);
        });
        findViewById(R.id.btnMode).setOnClickListener(v -> {//am start com.android.engmode
            Utils.openApp(this, "com.android.engmode");
        });
        btn4.setOnClickListener(view -> {
            L.w("测试按钮");
            setText(L.msg);
            try {
                test();
            } catch (Exception e) {
                e.printStackTrace();
                L.e("执行测试异常：" + e.getMessage());
            }
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            Tools.hideKeyboard(editText);
            return false;
        });
        // 沉浸式
        ScreenUtils.setStatusBarInvasion(this);
    }

    @SuppressLint({"RestrictedApi", "WrongConstant", "Range"})
    private void test() throws Exception {
        index++;
        L.dd(index);

//        getScreenShotPower();

//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.type = 2019;
//        layoutParams.flags |= 262220;
//        layoutParams.format = -3;
//        layoutParams.width = -1;
//        layoutParams.height = 80;
//        layoutParams.gravity = 80;
//
//        if ((layoutParams.flags & FLAG_DIM_BEHIND) != 0) {
//            L.w("包含FLAG_DIM_BEHIND");
//        } else {
//            L.w("不包含FLAG_DIM_BEHIND");
//        }
//
//        WindowManager.LayoutParams mStateBarParams = new WindowManager.LayoutParams();
//        layoutParams.format = -3;
//        mStateBarParams.type = 2000;
//        mStateBarParams.flags |= -2138832824;
//
//        mStateBarParams.gravity = 53;
//        mStateBarParams.flags &= -33;
//        if ((layoutParams.flags & FLAG_DIM_BEHIND) != 0) {
//            L.w("包含FLAG_DIM_BEHIND");
//        } else {
//            L.w("不包含FLAG_DIM_BEHIND");
//        }

//        new BuildTestToast().addView(mContext);
//        TestUtil.testDialog(this,"ssssss",-1);

//        TestUtil.getSensorData(mContext);
//        L.dd("isTelephonyNetEnable:" + networkManager.isTelephonyDataEnable());

        TestUtil.showAlertDialog(this);
//        TestUtil.testDialog(this,"121212",-1);
//        networkManager.switchTelephonyDataEnable();
//        Toast.makeText(this, "测试Toast: " + index, Toast.LENGTH_SHORT).show();
//        checkPermission();
//        TestUtil.testDialog(this, "ahahh", 0);
//        TestUtil.testFonts(this);
//        TestUtil.showToast(this);
//        startService(new Intent(this, DemoService.class));
//        checkSystemPropertiesReady();

//        Intent intent = new Intent();
//        String pkg = "com.exa.companyclient";
//        String clazz = "com.exa.companyclient.service.MService";
//        intent.setComponent(new ComponentName(pkg,clazz));
//        App.getContext().startService(intent);
    }

    private void printLocation(Location location) {
        setText(location.toString());
        Bundle bundle = location.getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            StringBuilder buffer = new StringBuilder();
            for (String key : bundle.keySet()) {
                buffer.append(key).append("=").append(bundle.get(key).toString()).append(",");
            }
            setText(buffer.toString());
            L.d("LocationBundle=" + bundle + " , " + buffer);
        }
    }

    public static final int REQUEST_MEDIA_PROJECTION = 10001;

    // 申请截屏权限
    private void getScreenShotPower() {
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mProjectionManager != null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }

    private MediaProjection mMediaProjection;

    // 回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION && data != null) {
            if (resultCode == RESULT_OK) {
                MediaProjection mediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                mediaProjection.registerCallback(new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        mediaProjection.unregisterCallback(this);
                        saveScreenshot(mediaProjection);
                    }
                }, null);
                // 截取屏幕内容
                MediaProjection.Callback callback = new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        mediaProjection.unregisterCallback(this);
                        saveScreenshot(mediaProjection);
                    }
                };
                mediaProjection.registerCallback(callback, null);
            } else if (resultCode == RESULT_CANCELED) {
                L.d("用户取消");
            }
        }
    }

    private void bindClientService() {
        L.dd();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.exa.companyclient",
                "com.exa.companyclient.service.MService"));
        @SuppressLint("WrongConstant") final boolean isBind = bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                L.d("bindService onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                L.d("bindService onServiceDisconnected");
            }
        }, Context.BIND_AUTO_CREATE | 0x04000000);
        L.e("bind-result:" + isBind);
    }

    private void saveScreenshot(MediaProjection mediaProjection) {
        // 将截取的屏幕内容保存为图片文件
        Bitmap bitmap = Bitmap.createBitmap(2560, 1440, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(0, 0, 2560, 1440, paint);
        OutputStream outputStream = null;
        try {
            String path = Environment.getExternalStorageDirectory() + "/screenshot.png";
            outputStream = Files.newOutputStream(Paths.get(path));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            L.d("截屏存储路径：" + path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void screenshot() {
        ImageView imageView = findViewById(R.id.imageView);
        screenshotView(getWindow(), imageView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        L.dd(getClass().getSimpleName());
    }

    private void registerReceiver() {
        L.dd("registerTimeUpdateReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.gxatek.cockpit.datacenter.action.UPLOAD");
        registerReceiver(mTimeUpdateReceiver, filter);
        setText(DateUtil.getNowDateHM());
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
            L.d("收到广播：" + action);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        L.w("onWindowFocusChanged: hasFocus = " + hasFocus + "\n" + Log.getStackTraceString(new Throwable()));
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
        L.dd(getClass().getSimpleName());
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