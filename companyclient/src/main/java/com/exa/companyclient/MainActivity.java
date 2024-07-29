package com.exa.companyclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.SystemBarUtil;
import com.exa.companyclient.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.ACTION_MEDIA_EJECT;
import static android.content.Intent.ACTION_MEDIA_MOUNTED;

public class MainActivity extends BaseBindActivity<ActivityMainBinding> implements View.OnClickListener {
    private int index = 0;
    private boolean mShowSystemUI = true;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        logScreenInfo();
        L.dd(getClass().getSimpleName() + "注册广播MEDIA_MOUNTED,MEDIA_EJECT");
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("file");
        filter.addAction(ACTION_MEDIA_MOUNTED);
        filter.addAction(ACTION_MEDIA_EJECT);
        registerReceiver(new TestReceiver(), filter);
    }

    class TestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                L.d("MainActivity2 onReceive:" + intent.getAction() + ", displayid=" + context.getDisplay().getDisplayId());
                setText("MainActivity2 onReceive:" + intent.getAction() + ", displayid=" + context.getDisplay().getDisplayId());
            }
        }
    }

    @Override
    protected void initData() {
        bind.display1.setOnClickListener(this);
        bind.display2.setOnClickListener(this);
        bind.display3.setOnClickListener(this);
        bind.display5.setOnClickListener(this);
        bind.startOnMainScreen.setOnClickListener(this);
        bind.switchSystemUI.setOnClickListener(this);
        bind.testBtn.setOnClickListener(this);
        bind.backBtn.setOnClickListener(this);

//        bind.backBtn.postDelayed(() -> {
//            startActivityByDisplayId(MainActivity2.this, MainActivity3.class, 3);
//        },5000);
    }

    /**
     * adb shell am start com.exa.companyclient/com.exa.companyclient.MainActivity3 --display 3
     *
     * @param v The view that was clicked.
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startOnMainScreen://启动在主屏上
                startActivityByDisplayId(MainActivity.this, MainActivity.class, 0);
                break;
            case R.id.display1://启动在副屏上
                startActivityByDisplayId(MainActivity.this, SubDisplayActivity.class, 1);
                break;
            case R.id.display2://启动在副屏上
                startActivityByDisplayId(MainActivity.this, SubDisplayActivity.class, 2);
                break;
            case R.id.display3:
                startActivityByDisplayId(MainActivity.this, SubDisplayActivity.class, 3);
                break;
            case R.id.display5:
                startActivityByDisplayId(MainActivity.this, SubDisplayActivity.class, 5);
                break;
            case R.id.switchSystemUI:
                mShowSystemUI = !mShowSystemUI;
                if (mShowSystemUI) {
                    SystemBarUtil.showStatusBars(this);
                } else {
                    SystemBarUtil.hideStatusBars(this);
                }
                break;
            case R.id.testBtn:
                test();
                break;
            case R.id.backBtn:
                finish();
                break;
            default:
                break;
        }
    }

    private void test() {
        L.dd();
        index++;
        Toast.makeText(App.getContext(),"主屏测试Toast " + index,Toast.LENGTH_SHORT).show();
//        Dialog dialog = new Dialog(this);
//        dialog.setOwnerActivity(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
//        dialog.setContentView(view);
//        dialog.show();

//        Toast.makeText(App.getContext(), "主屏测试Toast " + index, Toast.LENGTH_SHORT).show();
//
//        DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
//        Context d1Context = App.getContext().createDisplayContext(dm.getDisplay(1));
//        Toast.makeText(d1Context, "主屏测试Toast " + index, Toast.LENGTH_SHORT).show();

    }

    private void logScreenInfo() {
        //多屏显示时使用
        Display mDisplay = getWindowManager().getDefaultDisplay();
        setText("默认显示器：" + mDisplay.getDisplayId() + ", " + mDisplay.getName() + ", " + mDisplay.isValid());
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays(null);
        if (displays != null) {
            for (Display display : displays) {
                setText("更多显示器：" + display.getDisplayId() + ", " + display.getName() + ", " + display.isValid());
            }
        }
    }

    /**
     * 在指定屏幕上启动Activity
     *
     * @param context
     * @param clazz
     * @param displayId
     */
    public static void startActivityByDisplayId(Activity context, Class clazz, int displayId) {
        L.dd(displayId);
        Display mDisplay = context.getWindowManager().getDefaultDisplay();//默认显示器id 0
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays(null);
        if (displays != null) {
            for (int i = 0; i < displays.length; i++) {
                L.d("更多显示器：" + displays[i].getDisplayId() + ", " + displays[i].getName() + ", " + displays[i].isValid());
            }
        }
        //FLAG_ACTIVITY_LAUNCH_ADJACENT 多屏使用
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityOptions options = ActivityOptions.makeBasic().setLaunchDisplayId(displayId);
        context.startActivity(intent, options.toBundle());
    }

    public void registerShareReceiver(android.content.Context context) {
        android.content.IntentFilter filter = new android.content.IntentFilter();
        filter.addAction("com.raite.test_move_task");
        context.registerReceiver(new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, android.content.Intent intent) {
                int fromDisplayId = intent.getIntExtra("from", 0);
                int toDisplayId = intent.getIntExtra("to", 1);

            }
        }, filter);
    }

    private void setText(String msg) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        runOnUiThread(() -> {
            String n = msg + "\n" + bind.text.getText().toString();
            bind.text.setText(n);
        });
    }
}