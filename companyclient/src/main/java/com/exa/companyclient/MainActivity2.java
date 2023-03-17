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

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.StatubarUtil;
import com.exa.baselib.utils.Utils;
import com.exa.companyclient.databinding.ActivityMain2Binding;
import com.gxatek.cockpit.vicelauncher.IRemoteStatusCallback;
import com.gxatek.cockpit.vicelauncher.ViceLauncherRemoteInterface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity2 extends BaseBindActivity<ActivityMain2Binding> implements View.OnClickListener {

    private ViceLauncherRemoteInterface remoteInterface;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initView() {
        StatubarUtil.setStatusBarInvasion(this);
        logScreenInfo();
    }

    @Override
    protected void initData() {
        bind.startOnSubScreen.setOnClickListener(this);
        bind.bindService.setOnClickListener(this);
        bind.showSystemUi.setOnClickListener(this);
        bind.hideSystemUi.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startOnSubScreen:
                startActivityByDisplayId(MainActivity2.this, getClass(), 5);//启动在第二块屏上
                break;
            case R.id.bindService:
                bindService();
                break;
            case R.id.showSystemUi:
                try {
                    remoteInterface.setStatusBarVisibility(View.VISIBLE);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.hideSystemUi:
                try {
                    remoteInterface.setStatusBarVisibility(View.GONE);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                remoteInterface = ViceLauncherRemoteInterface.Stub.asInterface(service);
                try {
                    remoteInterface.registerCallback(new IRemoteStatusCallback.Stub() {
                        @Override
                        public void onNavigationStatusChange(int i) throws RemoteException {
                            L.dd(i);
                        }

                        @Override
                        public void onScreenSaverStatusChange(int i) throws RemoteException {
                            L.dd(i);
                        }

                        @Override
                        public void onStatusBarStatusChange(int i) throws RemoteException {
                            L.dd(i);
                        }

                        @Override
                        public IBinder asBinder() {
                            return null;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e("registerCallback err", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                remoteInterface = null;
                L.dd();
            }
        }, Context.BIND_AUTO_CREATE);
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