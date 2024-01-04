package com.exa.companyclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.companyclient.databinding.ActivityMain2Binding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AlertDialog;

public class MainActivity2 extends BaseBindActivity<ActivityMain2Binding> implements View.OnClickListener {
    private int index = 0;
    private boolean mShowSystemUI = true;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initView() {
        logScreenInfo();
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

        new TestReceiver("------").registerReceiver(this);
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
                startActivityByDisplayId(MainActivity2.this, MainActivity2.class, 0);
                break;
            case R.id.display1://启动在副屏上
                startActivityByDisplayId(MainActivity2.this, MainActivity3.class, 1);
                break;
            case R.id.display2://启动在副屏上
                startActivityByDisplayId(MainActivity2.this, MainActivity3.class, 2);
                break;
            case R.id.display3:
                startActivityByDisplayId(MainActivity2.this, MainActivity3.class, 3);
                break;
            case R.id.display5:
                startActivityByDisplayId(MainActivity2.this, MainActivity3.class, 5);
                break;
            case R.id.switchSystemUI:
                mShowSystemUI = !mShowSystemUI;
                if (mShowSystemUI) {
                    ScreenUtils.showStatusBars(this);
                } else {
                    ScreenUtils.hideStatusBars(this);
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
        String msg =
                "副屏测试Toast $index toast_max_width=" + getResources().getDimensionPixelSize(R.dimen.toast_max_width);
        msg = "一二三四";
        msg = "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        Dialog dialog = new Dialog(this);
//        dialog.setOwnerActivity(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout,null);
//        dialog.setContentView(view);
//        dialog.show();
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

    private void setText(String msg) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        runOnUiThread(() -> {
            String n = msg + "\n" + bind.text.getText().toString();
            bind.text.setText(n);
        });
    }
}