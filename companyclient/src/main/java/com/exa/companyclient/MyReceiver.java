package com.exa.companyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;

/**
 * com.exa.companyclient.ACTION_OPEN_CLIENT
 */
public class MyReceiver extends BroadcastReceiver {
    private String TAG = "MyReceiver";

    public MyReceiver() {
    }

    public MyReceiver(String tag) {
        TAG = tag;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        L.w(TAG + "#onReceive:" + intent.getAction());
        Tools.startAppByClassName(context.getApplicationContext(), MainActivity.class.getName());
    }
}
