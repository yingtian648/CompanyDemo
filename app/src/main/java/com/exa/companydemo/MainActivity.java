package com.exa.companydemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseActivity;
import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.location.LocationActivity;
import com.exa.companydemo.utils.LogTools;

import org.xmlpull.v1.XmlPullParser;

import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {
    private TextView topT,text, text1, text2, text3, text4, text5, text6;
    private EditText editText;
    private boolean isFullScreen;
    private final String fontTestWords = "Innovation in China 中国制造，惠及全球 0123456789";

    @Override
    protected void initData() {
        testFonts();
    }

    @Override
    protected void initView() {
        editText = findViewById(R.id.edt);
        topT = findViewById(R.id.topT);
        checkPermission();
        topT.setOnClickListener(v -> {
            topT.setText(DateUtil.getNowTime() + " 点击了");
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
            String msg = "撒谎吉萨号登机口啥叫啊十大建设大家卡刷道";
//            String msg = "撒谎吉萨号登机口啥叫啊十大建设大家卡刷道具卡啥叫看到啥就肯定会刷卡机打算结婚的卡刷道具卡撒谎撒谎的吉萨号登机口撒谎的加快速度安徽省贷记卡和数据库的啥叫看到";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Tools.hideKeyboard(editText);
                return false;
            }
        });

        registerBroadcast(mReceiver);

        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        int width = mParams.width;
    }

    @Override
    protected int getLayoutId() {
        ScreenUtils.setFullScreen(this);
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
                default:
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

        L.d("SourceHanSansCN is Default ? " + (SourceHanSansCN.equals(aDefault)));
        L.d("sans-serif is Default ? " + (sans_serif.equals(aDefault)));
        L.d("serif is Default ? " + (serif.equals(aDefault)));
        L.d("monospace is Default ? " + (monospace.equals(aDefault)));
        L.d("GacFont is Default ? " + (GacFont.equals(aDefault)));

//        LogTools.logSystemFonts();
    }
}