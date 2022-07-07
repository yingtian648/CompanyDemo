package com.exa.companyclient.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.exa.companyclient.utils.L;

/**
 *  adb模拟执行
 *  adb shell am broadcast -a android.intent.action.MEDIA_MOUNTED
 *  adb shell am broadcast -a android.intent.action.MEDIA_UNMOUNTED
 *  adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_STARTED
 *  adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_FINISHED
 */
public class MediaScannerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        L.d("MediaScannerReceiver onReceive:" + action);
        switch (action) {
            case Intent.ACTION_MEDIA_MOUNTED://挂载
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED://卸载
                break;
            case Intent.ACTION_MEDIA_SCANNER_STARTED://扫描开始
                break;
            case Intent.ACTION_MEDIA_SCANNER_FINISHED://扫描结束
                ExeHelper.getInstance().exeGetSystemMediaProviderData();
                break;
        }
    }
}

