package com.exa.companyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.exa.baselib.utils.L;

/**
 * @Author lsh
 * @Date 2023/12/22 18:45
 * @Description
 */
public class TestReceiver extends BroadcastReceiver {
    private String tag;

    public void registerReceiver(Context context){
        L.dd(tag);
        IntentFilter intentFilter  = new IntentFilter();
        intentFilter.addDataScheme("package");
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(this,intentFilter);
    }

    public TestReceiver() {
        this("TestReceiver");
    }

    public TestReceiver(String tag) {
        this.tag = tag;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        L.w(tag + " onReceive:" + intent.getAction() + "," + intent);
    }
}
