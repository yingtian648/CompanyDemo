package com.exa.companydemo.mediaprovider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;

/**
 * adb模拟执行
 * adb shell am broadcast -a android.intent.action.MEDIA_MOUNTED
 * adb shell am broadcast -a android.intent.action.MEDIA_UNMOUNTED
 * adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_STARTED
 * adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_FINISHED
 */
public class MediaScannerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        L.d("MediaScannerReceiver onReceive:" + action);
        switch (action) {
            case BaseConstants.ACTION_MY_PROVIDER_SCAN_FINISH://自定义媒体扫描完成
                break;
            case Intent.ACTION_MEDIA_MOUNTED://挂载
                startMediaScannerService(context);
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED://卸载
                break;
            case Intent.ACTION_MEDIA_SCANNER_STARTED://扫描开始
                break;
            case Intent.ACTION_MEDIA_SCANNER_FINISHED://扫描结束

                break;
        }
    }

    private void startMediaScannerService(Context context) {
        Intent intent = new Intent(context, MediaScannerService.class);
        Bundle b = new Bundle();
//        b.putString("path", BaseConstants.FILE_DIR_MUSIC);
        b.putString("path","/mnt/media_rw/usb1");
        intent.putExtras(b);
        context.startService(intent);
    }
}

