package com.exa.companydemo.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;

import com.exa.baselib.utils.L;

/**
 * @Author lsh
 * @Date 2024/1/9 16:51
 * @Description
 */
public class Ces2715Util {
    private static final int defaultDisplayId = 0;
    private static final int secondaryDisplayId = 2;
    private static final Handler mHandler = new Handler(Looper.myLooper());
    private static final IWindowShareManager mIWindowShareManager = new IWindowShareManager();

    /**
     * 放在com.raite.wshare.service.WShareBinder.java中
     */
    public void registerShareReceiver(android.content.Context context) {
        android.content.IntentFilter filter = new android.content.IntentFilter();
        filter.addAction("com.raite.test_move_task");
        context.registerReceiver(new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, android.content.Intent intent) {
                android.util.Log.i("WShareBinder", "onReceive com.raite.test_move_task");
                int fromDisplayId = intent.getIntExtra("from", 0);
                int toDisplayId = intent.getIntExtra("to", 1);

                if (fromDisplayId == 0) {
                    mHandler.postDelayed(() -> {
                        try {
                            mIWindowShareManager.moveOffset(500, 0, fromDisplayId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 200);
                    mHandler.postDelayed(() -> {
                        try {
                            mIWindowShareManager.moveFinish(1000, 0, fromDisplayId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 500);
                } else {
                    mHandler.postDelayed(() -> {
                        try {
                            mIWindowShareManager.moveOffset(-500, 0, fromDisplayId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 200);
                    mHandler.postDelayed(() -> {
                        try {
                            mIWindowShareManager.moveFinish(-1000, 0, fromDisplayId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 500);
                }
            }
        }, filter);
    }

    /**
     * 主屏副屏来回移动栈到另外一个屏上
     * Ces2715Util.switchMoveTask(this);
     */
    public static void switchMoveTask(Context context) {
        int displayId = Display.DEFAULT_DISPLAY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            displayId = context.getDisplay().getDisplayId();
        }
        L.dd("from=" + displayId);
        Intent intent = new Intent("com.raite.test_move_task");
        intent.setPackage("com.raite.wshare.service");
        if (displayId == Display.DEFAULT_DISPLAY) {
            intent.putExtra("from", defaultDisplayId);
            intent.putExtra("to", secondaryDisplayId);
        } else {
            intent.putExtra("from", secondaryDisplayId);
            intent.putExtra("to", defaultDisplayId);
        }
        context.sendBroadcast(intent);
    }


    private static class IWindowShareManager {
        public void moveOffset(int offX, int offY, int displayId) {
        }

        public void moveFinish(int offX, int offY, int displayId) {
        }
    }
}
