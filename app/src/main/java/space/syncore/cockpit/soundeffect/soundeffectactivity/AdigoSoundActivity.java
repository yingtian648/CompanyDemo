package space.syncore.cockpit.soundeffect.soundeffectactivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.IWindowManager;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.exa.baselib.utils.L;
import com.exa.companydemo.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author lsh
 * @Date 2023/9/19 14:49
 * @Description
 */
public class AdigoSoundActivity extends AppCompatActivity {
    private AdigoSoundDialogFragment dialogFragment;

    private static final String SKIP_COCKPITMUMSOUND_FRAGMENT = "Skip_CockpitmumSoundFragment";

    private static final int INITIAL_VALUE = 0;
    private int iconIntent = -505;
    private static final int START_UNDER_NORMAL_STATE = 88;
    private static final int OPEN_AMBIENT_SOUND_START = 99;
    private final String TAG = "AdigoSoundActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adigo_sound);
        //注册当前音乐播放回调
        getAppStartData();
        initOpenDialog();//打开AdigoSoundDialogFragment
//        initWindow();
    }

    private void initWindow() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //目前只针对AH8,版本
            L.dd("SDK_INT:S");
//            WindowInsetsController controller = window.getInsetsController();
//            controller.hide(WindowInsets.Type.statusBars());
        } else {
            View decorView = window.getDecorView();
            int vis = decorView.getSystemUiVisibility();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(vis | option);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解注册广播接收器
        //关闭面板，按钮数据埋点
        L.dd(TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.dd(TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.dd(TAG);
    }

    public int getIconIntent() {
        return iconIntent;
    }

    /**
     * 获取其他应用打开ADS面板传入数据
     * startAppData：氛围音是否开启
     * 88：氛围音未开启，正常启动打开娱乐音效页面，
     * 99：氛围音开启，打开氛围音页面
     * mProLiteData：当前是否配有外置功放
     * 66：内置功放
     * 77：外置功放
     */
    private void getAppStartData() {

        int startAppData = getIntent().getIntExtra(SKIP_COCKPITMUMSOUND_FRAGMENT, INITIAL_VALUE);

        if (startAppData == OPEN_AMBIENT_SOUND_START) {
            //氛围音开启状态
            iconIntent = OPEN_AMBIENT_SOUND_START;
        } else if (startAppData == START_UNDER_NORMAL_STATE) {
            //氛围音未开启状态
            iconIntent = START_UNDER_NORMAL_STATE;
        } else {
            //不存在
            L.dd("不存在");
        }
    }

    /**
     * 打开Dialog弹窗
     */
    private void initOpenDialog() {
        //防止多次点击打开多个
        if (dialogFragment != null && dialogFragment.isVisible()) {
            return;
        }
        dialogFragment = new AdigoSoundDialogFragment(this);
        dialogFragment.show(getSupportFragmentManager(), "dialog");
        L.dd("initOpenDialog");
    }

    /**
     * 获取临时图标，启动APP的值切换到对应Fragment
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int startApp = getIntent().getIntExtra(SKIP_COCKPITMUMSOUND_FRAGMENT, INITIAL_VALUE);
        L.dd("onNewIntent:" + startApp);
        if (startApp == OPEN_AMBIENT_SOUND_START) {
            iconIntent = OPEN_AMBIENT_SOUND_START;
        } else if (startApp == START_UNDER_NORMAL_STATE) {
            iconIntent = START_UNDER_NORMAL_STATE;
        } else {
            //不存在
            L.dd("onNewIntent:not exists");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
    }
}
