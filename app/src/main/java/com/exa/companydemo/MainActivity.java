package com.exa.companydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.utils.LogTools;

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
            startActivity(new Intent(this, VideoPlayerActivity.class));
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
    }

    private static char DOT = '\u2022';

    @Override
    protected int getLayoutId() {
        ScreenUtils.setFullScreen(this);
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
//        String[] ps = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            ps = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE};
//        }
//        PermissionUtil.requestPermission(this, () -> {
//            L.d("已授权 读写权限");
//        }, ps);
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

    /**
     * 调试字体
     */
    private void testFonts() {
        text.setText(fontTestWords + "   Default");
        text1.setText(fontTestWords + "   SourceHanSansCN");
        text2.setText(fontTestWords + "   sans-serif");
        text3.setText(fontTestWords + "   serif");
        text4.setText(fontTestWords + "   monospace");
        Typeface aDefault = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

        Typeface GacFont = Typeface.create("SourceHanSansCN", Typeface.BOLD);
        Typeface sans_serif = Typeface.create("sans-serif", Typeface.BOLD);
        Typeface serif = Typeface.create("serif", Typeface.BOLD);
        Typeface monospace = Typeface.create("monospace", Typeface.BOLD);

        text.setTypeface(aDefault);
        text1.setTypeface(GacFont);
        text2.setTypeface(sans_serif);
        text3.setTypeface(serif);
        text4.setTypeface(monospace);


        Typeface fonts1 = Typeface.create("SourceHanSansCN", Typeface.NORMAL);
        Typeface fonts2 = Typeface.create("SourceHanSansCN-ExtraLight", Typeface.NORMAL);
        Typeface fonts3 = Typeface.create("SourceHanSansCN-Light", Typeface.NORMAL);
        Typeface fonts4 = Typeface.create("SourceHanSansCN-Normal", Typeface.NORMAL);
        Typeface fonts5 = Typeface.create("SourceHanSansCN-Regular", Typeface.NORMAL);

        text.postDelayed(() -> {
            text.setText(fontTestWords + "   Default");
            text1.setText(fontTestWords + "   ExtraLight");
            text2.setText(fontTestWords + "   Light");
            text3.setText(fontTestWords + "   Normal");
            text4.setText(fontTestWords + "   Regular");
            text.setTypeface(fonts1);
            text1.setTypeface(fonts2);
            text2.setTypeface(fonts3);
            text3.setTypeface(fonts4);
            text4.setTypeface(fonts5);
        }, 10000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface fonts11 = Typeface.create(Typeface.create("SourceHanSansCN", Typeface.NORMAL), 200, false);
            Typeface fonts22 = Typeface.create(Typeface.create("SourceHanSansCN", Typeface.NORMAL), 300, false);
            Typeface fonts33 = Typeface.create(Typeface.create("SourceHanSansCN", Typeface.NORMAL), 400, false);
            Typeface fonts44 = Typeface.create(Typeface.create("SourceHanSansCN", Typeface.NORMAL), 500, false);
            Typeface fonts55 = Typeface.create(Typeface.create("SourceHanSansCN", Typeface.NORMAL), 700, false);
            text.postDelayed(() -> {
                text.setText(fontTestWords + "   weight 200");
                text1.setText(fontTestWords + "   weight 300");
                text2.setText(fontTestWords + "   weight 400");
                text3.setText(fontTestWords + "   weight 500");
                text4.setText(fontTestWords + "   weight 700");
                text.setTypeface(fonts11);
                text1.setTypeface(fonts22);
                text2.setTypeface(fonts33);
                text3.setTypeface(fonts44);
                text4.setTypeface(fonts55);
            }, 20000);
        }

        L.d("GacFont is Default ? " + (GacFont.equals(aDefault)));
        L.d("sans-serif is Default ? " + (sans_serif.equals(aDefault)));
        L.d("serif is Default ? " + (serif.equals(aDefault)));
        L.d("monospace is Default ? " + (monospace.equals(aDefault)));
    }
}