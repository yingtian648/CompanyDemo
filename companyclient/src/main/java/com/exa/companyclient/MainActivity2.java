package com.exa.companyclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Display;
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

public class MainActivity2 extends BaseBindActivity<ActivityMain2Binding> implements View.OnClickListener {
    private int index = 0;
    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initView() {
        L.dd("屏幕高度：" + Tools.getScreenH(this)
                + ",DisplayId=" + getWindowManager().getDefaultDisplay().getDisplayId());
        StatubarUtil.setStatusBarInvasion(this);
        logScreenInfo();
    }

    @Override
    protected void initData() {
        bind.startOnSubScreen.setOnClickListener(this);
        bind.startOnMainScreen.setOnClickListener(this);
        bind.bindService.setOnClickListener(this);
        bind.showSystemUi.setOnClickListener(this);
        bind.hideSystemUi.setOnClickListener(this);
        bind.testBtn.setOnClickListener(this);
        bind.backBtn.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startOnMainScreen://启动在主屏上
                startActivityByDisplayId(MainActivity2.this, MainActivity2.class, 0);
                break;
            case R.id.startOnSubScreen://启动在副屏上
                startActivityByDisplayId(MainActivity2.this, MainActivity3.class, 2);
                break;
            case R.id.bindService:
                break;
            case R.id.showSystemUi:
                ScreenUtils.showStatusBars(this);
                break;
            case R.id.hideSystemUi:
                ScreenUtils.hideStatusBars(this);
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
        index++;
//        Toast.makeText(this,"主屏测试Toast " + index,Toast.LENGTH_SHORT).show();
        PermissionUtil.requestPermission(this, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionGranted() {

            }
        },new String[]{"android.permission.RECORD_AUDIO"});
    }

    private void logScreenInfo() {
        //多屏显示时使用
        Display mDisplay = getWindowManager().getDefaultDisplay();
        setText("默认显示器：" + mDisplay.getDisplayId() + ", " + mDisplay.getName() + ", " + mDisplay.isValid());
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays(null);
        if (displays != null) {
            for (int i = 0; i < displays.length; i++) {
                setText("更多显示器：" + displays[i].getDisplayId() + ", " + displays[i].getName() + ", " + displays[i].isValid());
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