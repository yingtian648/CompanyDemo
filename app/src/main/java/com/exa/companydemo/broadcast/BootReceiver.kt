package com.exa.companydemo.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.exa.baselib.utils.L
import com.exa.baselib.utils.Tools
import com.exa.companydemo.App

/**
 * @Author lsh
 * @Date 2023/7/5 10:24
 * @Description
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.apply {
            L.d("onReceive:$this")
            openSelf()
        }
    }

    private fun openSelf() {
        Tools.openApp(App.getContext(), App.getContext().packageName)
    }
}