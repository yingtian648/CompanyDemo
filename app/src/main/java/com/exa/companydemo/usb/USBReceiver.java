package com.exa.companydemo.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.exa.baselib.utils.L;

public class USBReceiver extends BroadcastReceiver {
    private static final String TAG = "USBReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        L.e(TAG, "onReceived:" + intent.getAction());
    }
}
