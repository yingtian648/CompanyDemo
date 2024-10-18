package com.exa.companydemo.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.exa.baselib.utils.L

@SuppressLint("StaticFieldLeak")
object AppListUtil {
    const val TAG = "AppListUtil"
    private var mContext: Context? = null
    private var mListener: IPadAppNamesChangedListener? = null

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

    fun unRegisterAppChangeListener(){
        this.mListener = null
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mListener?.onPadAppNamedChanged(getAppNameList())
        }
    }

    private fun registerPackageChangeReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addDataScheme("package")
        mContext?.registerReceiver(mReceiver, filter)
    }

    fun getAppNameList(): Array<String> {
        val names: MutableList<String> = ArrayList()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        mContext?.packageManager?.queryIntentActivities(intent, 0)?.forEach {
            names.add(it.activityInfo.loadLabel(mContext!!.packageManager).toString())
        }
        L.dd(names.toTypedArray().toString())
        return names.toTypedArray()
    }
}