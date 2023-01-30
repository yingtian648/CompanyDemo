package com.exa.companydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.location.GnssAntennaInfo;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.server.location.gnss.IExtLocationCallback;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.location.demo.LocationServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static com.exa.companydemo.TestUtil.isRegisterBroadCast;
import static com.exa.companydemo.TestUtil.mReceiver;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {
    private TextView topT, text0, text1, text2, text3, text4, text5, text6;
    private Button btn1, btn2, btn3, btn4;
    private EditText editText;
    private boolean isFullScreen;
    private Context mContext;
    private final String fontTestWords = "Innovation in China 中国制造，惠及全球 0123456789";

    @Override
    protected void initData() {
        testFonts();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initView() {
        mContext = this;
//        TestUtil.registerBroadcast(this);

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
        btn4.setOnClickListener(view -> {
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
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    private void test() {
//        TestUtil.showToast(this);
//        TestUtil.usbPermission(this);
//        ScreenUtils.setFullScreen(this);

//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.dvr.app", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.dvr.widge", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.dvr.broadcast.PendingIntentReceiver", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.weather.updateFirst", null);
//        TestUtil.sendBroadcast(this, "com.gxatek.cockpit.weather.update", null);

//        TestUtil.registerBroadcast(this);

        testDialog();
    }

    private void testDialog() {
        final MyDialog dialog = new MyDialog(this.getApplicationContext());
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null, false);
        Button sureBtn = view.findViewById(R.id.sure_button);
        Button cancelBtn = view.findViewById(R.id.cancel_button);
        TextView titleT = view.findViewById(R.id.titleT);
        titleT.setText("00000000000000000000");
        dialog.setContentView(view);
        cancelBtn.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {//adb shell input keyevent 199
        if (event.getKeyCode() == 199) {
            isFullScreen = !isFullScreen;
            if (isFullScreen) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private class MyDialog extends Dialog {
        public MyDialog(@NonNull Context context) {
            super(context);
            // setWindowAttrs();
        }

        public MyDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
            // setWindowAttrs();
        }

        protected MyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
            // setWindowAttrs();
        }

        @Override
        public void setContentView(@NonNull View view) {
            super.setContentView(view);
            // setWindowAttrs();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setWindowAttrs();
            super.onCreate(savedInstanceState);
        }

        /* access modifiers changed from: protected */
        @SuppressLint("WrongConstant")
        private Window setWindowAttrs() {
            Window window = getWindow();
            if (window == null) {
                return null;
            }
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes == null) {
                return window;
            }
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.gravity = Gravity.CENTER;
            attributes.format = PixelFormat.TRANSLUCENT;
            attributes.dimAmount = 0f;
            attributes.flags = attributes.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
            attributes.setTitle("MainActivity_Dialog");
            attributes.type = 2507;

            window.setAttributes(attributes);

            return window;
        }
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
//        L.d("intent#getComponent ：：" + (intent.getComponent() == null ? "null" : intent.getComponent().getPackageName()));
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
        text0 = findViewById(R.id.text);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        text6 = findViewById(R.id.text6);

        text0.setText(fontTestWords + "   Default");
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

        text0.setTypeface(aDefault);
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

    private void testLocationService() {
        LocationServiceImpl service = new LocationServiceImpl(this);
        service.onCreate();
        try {
            service.setCallback(new IExtLocationCallback() {
                @Override
                public void onLocation(Location location) throws RemoteException {

                }

                @Override
                public void reportSvStatus(int svCount, int[] svidWithFlags, float[] cn0s, float[] svElevations, float[] svAzimuths, float[] svCarrierFreqs, float[] basebandCn0s) throws RemoteException {

                }

                @Override
                public void onProviderEnabled() throws RemoteException {

                }

                @Override
                public void onProviderDisabled() throws RemoteException {

                }

                @Override
                public boolean isInEmergencySession() throws RemoteException {
                    return false;
                }

                @Override
                public void reportAGpsStatus(int agpsType, int agpsStatus, byte[] suplIpAddr) throws RemoteException {

                }

                @Override
                public void reportNmea(long time) throws RemoteException {

                }

                @Override
                public void reportMeasurementData(GnssMeasurementsEvent event) throws RemoteException {

                }

                @Override
                public void reportAntennaInfo(List<GnssAntennaInfo> antennaInfos) throws RemoteException {

                }

                @Override
                public void reportNavigationMessage(GnssNavigationMessage event) throws RemoteException {

                }

                @Override
                public void setTopHalCapabilities(int topHalCapabilities) throws RemoteException {

                }

                @Override
                public void setSubHalMeasurementCorrectionsCapabilities(int subHalCapabilities) throws RemoteException {

                }

                @Override
                public void setGnssYearOfHardware(int yearOfHardware) throws RemoteException {

                }

                @Override
                public void setGnssHardwareModelName(String modelName) throws RemoteException {

                }

                @Override
                public void reportGnssServiceDied() throws RemoteException {

                }

                @Override
                public void reportLocationBatch(Location[] locationArray) throws RemoteException {

                }

                @Override
                public void psdsDownloadRequest() throws RemoteException {

                }

                @Override
                public void reportGeofenceTransition(int geofenceId, Location location, int transition, long transitionTimestamp) throws RemoteException {

                }

                @Override
                public void reportGeofenceStatus(int status, Location location) throws RemoteException {

                }

                @Override
                public void reportGeofenceAddStatus(int geofenceId, int status) throws RemoteException {

                }

                @Override
                public void reportGeofenceRemoveStatus(int geofenceId, int status) throws RemoteException {

                }

                @Override
                public void reportGeofencePauseStatus(int geofenceId, int status) throws RemoteException {

                }

                @Override
                public void reportGeofenceResumeStatus(int geofenceId, int status) throws RemoteException {

                }

                @Override
                public void reportNfwNotification(String proxyAppPackageName, byte protocolStack, String otherProtocolStackName, byte requestor, String requestorId, byte responseType, boolean inEmergencyMode, boolean isCachedLocation) throws RemoteException {

                }

                @Override
                public void reportNiNotification(int notificationId, int niType, int notifyFlags, int timeout, int defaultResponse, String requestorId, String text, int requestorIdEncoding, int textEncoding) throws RemoteException {

                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}