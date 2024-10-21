package com.exa.companydemo.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.util.Consumer

@SuppressLint("StaticFieldLeak")
object AppListUtil {
    const val TAG = "AppListUtil"
    private var mContext: Context? = null
    private var mListener: IPadAppNamesChangedListener? = null
    private val mAppMaps = mutableMapOf<String, Pair<String, String>>()

    fun init(context: Context) {
        this.mContext = context.applicationContext
        registerPackageChangeReceiver()
        getAppNameList()
    }

    fun release() {
        mContext?.unregisterReceiver(mReceiver)
        mContext = null
    }

    fun registerAppChangeListener(listener: IPadAppNamesChangedListener) {
        this.mListener = listener
    }

    fun unRegisterAppChangeListener() {
        this.mListener = null
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive ${intent?.action}")
//            mListener?.onPadAppNamedChanged(getAppNameList())
        }
    }

    private fun registerPackageChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addDataScheme("package")
        mContext?.registerReceiver(mReceiver, filter)
    }

    fun getAppNameList(): Map<String, Consumer<String>> {
        mAppMaps.clear()
        val map = mutableMapOf<String, Consumer<String>>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        mContext?.packageManager?.queryIntentActivities(intent, 0)?.forEach {
            val name = it.activityInfo.loadLabel(mContext!!.packageManager).toString()
            mAppMaps[name] = Pair(it.activityInfo.packageName, it.activityInfo.name)
            map[name] = getConsumer()
        }
        return map
    }

    private fun getConsumer(): Consumer<String> {
        return Consumer { content ->
            var find = false
            mAppMaps.keys.forEach {
                if (content == it && mAppMaps.containsKey(it)) {
                    find = true
                    kotlin.runCatching {
                        Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).apply {
                            component = ComponentName(mAppMaps[it]!!.first, mAppMaps[it]!!.second)
                            mContext?.startActivity(this)
                        }
                    }.onFailure { e ->
                        Log.w(TAG, "open $content fail!!", e)
                    }
                }
            }
            if (!find) {
                Log.w(TAG, "Not find $content")
            }
        }
    }
}