package com.exa.companydemo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.WifiInfo
//import android.net.TetheringManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.android.internal.util.ConcurrentUtils
import com.exa.baselib.utils.L
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException

/**
 * @Author lsh
 * @Date 2023/7/31 13:59
 * @Description
 */
class NetworkManager private constructor(private val mContext: Context) {
    private var mNetManager: ConnectivityManager =
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    //    private var mTetheringManager: TetheringManager =
//        mContext.getSystemService(TetheringManager::class.java)
    private var mWifiManager: WifiManager =
        mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var mTelephonyManager: TelephonyManager =
        mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private var mWifiEnable = false
    private var mTelephonyDataEnable = false
    private var mTelephonyDataLevel = 0
    private var mWifiConnected = false
    private var mConnectedWifiLevel = 0
    private var mHotSpotEnable = false
    private var mWaitingForTerminalState = false
    private var mListeners = mutableListOf<Callback>()
    private val mLock = Any()

    fun registerCallback(callback: Callback) {
        if (!mListeners.contains(callback)) {
            mListeners.add(callback)
        }
    }

    fun unRegisterCallback(callback: Callback) {
        mListeners.remove(callback)
    }

    interface Callback {
        fun onWifiStateChange(enable: Boolean, connect: Boolean, level: Int)
        fun onHotSpotStateChange(enable: Boolean, connect: Boolean)
        fun onTelephonyDataStateChange(enable: Boolean, level: Int) {}
    }

    companion object {
        private const val DEBUG = true
        private const val TAG = "NetworkManager"
        private const val ACTION_WIFI_AP_STATE_CHANGE = "android.net.wifi.WIFI_AP_STATE_CHANGED"
        private const val ACTION_TELEPHONY_ENABLE_CHANGE = "android.intent.action.ANY_DATA_STATE"
        const val NETWORK_TYPE_WIFI = 1
        const val NETWORK_TYPE_MOBILE = 2
        const val NETWORK_TYPE_UNKNOWN = 3

        @SuppressLint("StaticFieldLeak")
        private var mInstance: NetworkManager? = null

        fun getInstance(context: Context): NetworkManager {
            synchronized(NetworkManager::class.java) {
                if (mInstance == null) {
                    mInstance = NetworkManager(context)
                }
            }
            return mInstance!!
        }
    }

    init {
        registerReceiver()
        listenTelephonyDataChange()
        registerNetworkListener()
        mWifiEnable = mWifiManager.isWifiEnabled
        mWifiConnected = isWifiConnected()
        if (mWifiConnected) {
            mConnectedWifiLevel = WifiSignalLevel.getLevel(mWifiManager.connectionInfo!!.rssi)
        }
        mTelephonyDataEnable = isTelephonyDataEnable()
        getHotSpotState()
    }

    private fun registerNetworkListener() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        val callback = object : ConnectivityManager.NetworkCallback() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onAvailable(network: Network) {
                L.w(TAG, "onAvailable $network")
                runCatching {
                    mNetManager.getNetworkCapabilities(network)?.apply {
                        (transportInfo as WifiInfo?)?.apply {
                            L.w(TAG, "onAvailable connected $this")
                        }
                    }
                }
            }

            override fun onLost(network: Network) {
                L.w(TAG, "onLost $network")
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                runCatching {
                    (networkCapabilities.transportInfo as WifiInfo?)?.apply {
                        L.w(TAG, "onCapabilitiesChanged connected $this}")
                    }
                }
            }

            /**
             * 打印ip
             */
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                L.w(TAG, "onLinkPropertiesChanged ${linkProperties.linkAddresses}")
            }
        }
        mNetManager.registerNetworkCallback(request, callback)
    }

    fun initCurrNetworkStatus() {
        LogUtil.w("initCurrNetworkStatus")
        mListeners.forEach {
            it.onWifiStateChange(mWifiEnable, mWifiConnected, mConnectedWifiLevel)
        }
        mListeners.forEach {
            it.onHotSpotStateChange(mHotSpotEnable, false)
        }
    }

    fun isWifiEnable(): Boolean {
        return mWifiEnable
    }

    fun isTelephonyDataEnable(): Boolean {
        return if (!hasSimCard()) {
            LogUtil.w("isTelephonyNetEnable 没有sim卡")
            false
        } else if (ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            LogUtil.w("isTelephonyNetEnable error: need READ_PHONE_STATE permission")
            false
        } else {
            mTelephonyManager.isDataEnabled
        }
    }

    fun getWifiIp(): String {
        mWifiManager.connectionInfo?.apply {
            val ip = ipAddress
            L.d("ip=$ip")
            val split = "."
            return try {
                (ip and 0xFF).toString() + split +
                        (ip shr 8 and 0xFF) + split +
                        (ip shr 16 and 0xFF) + split +
                        (ip shr 24 and 0xFF)
            } catch (e: Exception) {
                "err"
            }
        }
        return "wifiInfo is null"
    }

    fun switchTelephonyDataEnable() {
        if (ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.MODIFY_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            LogUtil.w(
                "switchTelephonyNetEnable failed by need READ_PHONE_STATE" +
                        " or MODIFY_PHONE_STATE permission"
            )
        } else {
            LogUtil.w("switchTelephonyNetEnable: ${mTelephonyManager.isDataEnabled}")
            mTelephonyManager.isDataEnabled = !mTelephonyManager.isDataEnabled
            LogUtil.w("switchTelephonyNetEnable: ${mTelephonyManager.isDataEnabled}")
        }
    }

    /**
     * 切换WIFI开关
     */
    fun switchWifiEnable() {
        mWifiManager.isWifiEnabled = !mWifiManager.isWifiEnabled
    }

    fun isWifiConnected(): Boolean {
        var isWifiConn = false
        mWifiManager.connectionInfo?.apply {
            isWifiConn = (ssid != WifiManager.UNKNOWN_SSID || bssid != null)
        }
        return isWifiConn
    }

    fun isTelephonyDataConnected(): Boolean {
        var isMobileConn = false
        mNetManager.allNetworkInfo.forEach {
            if (it.type == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn = it.isConnected
            }
        }
        return isMobileConn
    }


    private fun listenTelephonyDataChange() {
        mTelephonyManager.listen(object : PhoneStateListener() {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    signalStrength.cellSignalStrengths.forEach {
                        mTelephonyDataLevel = it.level
                    }
                }
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }


    fun getNetWorkType(): Int {
        val networkInfo: NetworkInfo? = mNetManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected()) {
            val type: String = networkInfo.getTypeName()
            if (type.equals("WIFI", ignoreCase = true)) {
                return NETWORK_TYPE_WIFI
            } else if (type.equals("MOBILE", ignoreCase = true)) {
                return NETWORK_TYPE_MOBILE
            }
        }
        return NETWORK_TYPE_UNKNOWN
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ACTION_WIFI_AP_STATE_CHANGE)
        filter.addAction(ACTION_TELEPHONY_ENABLE_CHANGE)
        mContext.registerReceiver(ConnectionReceiver(), filter)
    }

    inner class ConnectionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.let {
                when (it) {
                    WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                        checkWifiStatus()
                    }
                    WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                        checkWifiStatus()
                    }
                    ACTION_WIFI_AP_STATE_CHANGE -> {
                        checkHotSpotState(it, intent)
                    }
                    ACTION_TELEPHONY_ENABLE_CHANGE -> {
                        checkTelephonyDataState()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkTelephonyDataState() {
        synchronized(mLock) {
            if (mTelephonyDataEnable != isTelephonyDataEnable()) {
                mTelephonyDataEnable = isTelephonyDataEnable()
                mListeners.forEach {
                    it.onTelephonyDataStateChange(mTelephonyDataEnable, mTelephonyDataLevel)
                }
            }
        }
    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    private fun hasSimCard(): Boolean {
        val simState = mTelephonyManager.simState
        return (simState != TelephonyManager.SIM_STATE_ABSENT
                && simState != TelephonyManager.SIM_STATE_UNKNOWN)
    }

    private fun checkHotSpotState(action: String, intent: Intent) {
        val state = intent.getIntExtra("wifi_state", 0)
        /**
         * WIFI_AP_STATE_DISABLING  10
         * WIFI_AP_STATE_DISABLED   11
         * WIFI_AP_STATE_ENABLING   12
         * WIFI_AP_STATE_ENABLED    13
         */
        if (DEBUG) {
            LogUtil.d(TAG, "Wifi热点状态：$state")
        }
        if (state == 11) {
            mListeners.forEach {
                it.onHotSpotStateChange(enable = false, connect = false)
            }
        } else if (state == 13) {
            mListeners.forEach {
                it.onHotSpotStateChange(enable = true, connect = false)
            }
        }
    }

    private fun checkWifiStatus() {
        synchronized(mLock) {
            var enableChange = false
            var connectChange = false
            var connectLevelChange = false
            if (mWifiEnable != mWifiManager.isWifiEnabled) {
                mWifiEnable = mWifiManager.isWifiEnabled
                enableChange = true
            }
            val conn = isWifiConnected()
            if (mWifiConnected != conn) {
                mWifiConnected = conn
                connectChange = true
            }
            val level = if (mWifiConnected) {
                WifiSignalLevel.getLevel(mWifiManager.connectionInfo!!.rssi)
            } else {
                WifiSignalLevel.LEVEL_0
            }
            if (mConnectedWifiLevel != level) {
                mConnectedWifiLevel = level
                connectLevelChange = true
            }
            if (enableChange || connectChange || connectLevelChange) {
                mListeners.forEach {
                    it.onWifiStateChange(mWifiEnable, mWifiConnected, mConnectedWifiLevel)
                }
            }
            if (DEBUG) {
                LogUtil.d(
                    TAG, "checkWifiStatus: enable=$mWifiEnable connect=$mWifiConnected" +
                            " connectLevel=$mConnectedWifiLevel ${mWifiManager.connectionInfo}"
                )
            }
        }
    }

    /**
     * wifi-热点
     */
    fun switchHotSpot() {
//        L.i(TAG, "switchHotSpot:$mHotSpotEnable")
//        if(mWaitingForTerminalState){
//            return
//        }
//        if (!mHotSpotEnable) {
//            mWaitingForTerminalState = true
//            mTetheringManager.startTethering(
//                TetheringManager.TetheringRequest.Builder(TetheringManager.TETHERING_WIFI)
//                .setShouldShowEntitlementUi(false).build(),
//                ConcurrentUtils.DIRECT_EXECUTOR,
//                object : TetheringManager.StartTetheringCallback {
//                    override fun onTetheringFailed(result: Int) {
//                        mWaitingForTerminalState = false
//                        L.w(TAG, "onTetheringFailed")
//                    }
//
//                    override fun onTetheringStarted() {
//                        mWaitingForTerminalState = false
//                        L.w(TAG, "onTetheringStarted")
//                    }
//                })
//        } else {
//            mTetheringManager.stopTethering(TetheringManager.TETHERING_WIFI)
//        }
    }

    /**
     * 获取热点开启状态
     */
    private fun getHotSpotState() {
        var isHotSpotOpen = false
        try {
            //通过放射获取 getWifiApState()方法
            val method = mWifiManager.javaClass.getDeclaredMethod("getWifiApState")
            //调用getWifiApState() ，获取返回值
            val state = method.invoke(mWifiManager) as Int
            //通过放射获取 WIFI_AP的开启状态属性
            val field: Field = mWifiManager.javaClass.getDeclaredField("WIFI_AP_STATE_ENABLED")
            //获取属性值
            val value = field.get(mWifiManager) as Int
            // WIFI_AP_STATE_ENABLED = 13,WIFI_AP_STATE_DISABLED=11
            //判断是否开启
            isHotSpotOpen = state == value
            if (DEBUG) {
                LogUtil.w("getHotSpotState: enable=$isHotSpotOpen state=$state")
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        mHotSpotEnable = isHotSpotOpen
    }
}