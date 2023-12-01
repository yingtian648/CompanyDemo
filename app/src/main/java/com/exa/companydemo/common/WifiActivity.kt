package com.exa.companydemo.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.debug.IAdbManager
import android.graphics.Color
import android.net.*
import android.net.wifi.*
import android.os.*
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.base.adapter.BaseRecyclerAdapter
import com.exa.baselib.utils.L
import com.exa.baselib.utils.ScreenUtils
import com.exa.baselib.utils.Tools
import com.exa.companydemo.R
import com.exa.companydemo.databinding.ActivityWifiBinding
import com.exa.companydemo.utils.CmdUtil

class WifiActivity : BaseBindActivity<ActivityWifiBinding>() {
    private var isScanning = false
    private var lastConnectState = false
    private var mPort: String? = ""
    private var mWifiInfo: WifiInfo? = null
    private var dataList: MutableList<ScanResult> = mutableListOf()
    private lateinit var mWifiManager: WifiManager
    private lateinit var mConnectManager: ConnectivityManager

    /**
     * 设置端口号
     */
    private val setPortCmd = "setprop service.adb.tcp.port 5555"

    /**
     * 获取端口号
     */
    private val getPortCmd = "getprop service.adb.tcp.port"

    override fun setContentViewLayoutId(): Int = R.layout.activity_wifi

    private val handler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == CmdUtil.HANDLER_EXE_COMMAND) {
                val result = msg.data.getString("nor")
                val errMsg = msg.data.getString("err")
                if (mPort != result) {
                    mPort = result
                    bind.csT.text = bind.csT.text.toString() + ":$mPort"
                }
                L.d("result:$result, err:$errMsg")
            }
        }
    }

    private val wifiReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged", "MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            L.d("wifiReceiver onReceive ${intent?.action}")
            intent?.action.let {
                when (it) {
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> filterScanResult(mWifiManager.scanResults)
                    WifiManager.NETWORK_STATE_CHANGED_ACTION -> refreshWifiStatus()
                    WifiManager.WIFI_STATE_CHANGED_ACTION -> refreshWifiStatus()
                    else -> Unit
                }
            }
        }
    }

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            L.dd(network.toString())
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            L.dd(network.toString())
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            L.dd(network.toString())
        }

        override fun onUnavailable() {
            super.onUnavailable()
            L.dd()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            L.dd(network.toString())
            refreshWifiStatus()
        }
    }

    override fun initData() {
        initWifiManager()
        bind.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.recyclerView.adapter =
            object : BaseRecyclerAdapter<ScanResult>(this, R.layout.item_wifi, dataList) {
                @SuppressLint("HardwareIds", "SetTextI18n")
                override fun onViewHolder(view: View, data: ScanResult, position: Int) {
                    val connectBtn = view.findViewById<Button>(R.id.connect)
                    val tvL = view.findViewById<TextView>(R.id.tv)
                    val tvR = view.findViewById<TextView>(R.id.tvR)
                    if (data.BSSID == mWifiInfo?.bssid) {
                        connectBtn.isEnabled = false
                        connectBtn.text = "已连接"
                    } else {
                        connectBtn.isEnabled = true
                        connectBtn.text = "连接"
                        connectBtn.setOnClickListener { showInputPasswordDialog(data) }
                    }
                    tvL.text = data.SSID
                    tvR.text = "信号强度 ${getSignalStrength(data.level)}  ${getHzStr(data.frequency)}"
                }
            }
    }

    private fun initWifiManager() {
        mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        mConnectManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mConnectManager.registerDefaultNetworkCallback(connectivityCallback)

        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiReceiver, filter)
        refreshWifiStatus()
    }

    /**
     * 连接wifi
     */
    private fun connect(data: ScanResult, pwd: String) {
        data.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val specifier: NetworkSpecifier =
                    WifiNetworkSpecifier.Builder()
                        .setBssid(MacAddress.fromString(it.BSSID)) //名称
                        .setWpa2Passphrase(pwd)// 密码
                        .build()
                val request: NetworkRequest = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifier)
                    .build()
                mConnectManager.requestNetwork(request, connectivityCallback)
            }
        }
    }

    private fun showInputPasswordDialog(data: ScanResult) {
        val edit = EditText(this)
        edit.setTextColor(Color.BLACK)
        edit.textSize = 40F
        edit.inputType = EditorInfo.TYPE_TEXT_VARIATION_URI
        edit.layoutParams = ViewGroup.LayoutParams(500, 100)
        edit.imeOptions = EditorInfo.IME_ACTION_DONE
        edit.setOnEditorActionListener { v, _, _ ->
            Tools.hideKeyboard(edit)
            true
        }
        val frameLayout = FrameLayout(this)
        frameLayout.addView(edit)
        frameLayout.setPadding(50)
        frameLayout.layoutParams = ViewGroup.LayoutParams(600, 200)
        AlertDialog.Builder(this)
            .setTitle("请输入【${data.SSID}】的连接密码：")
            .setView(frameLayout)
            .setPositiveButton(
                "确认"
            ) { _, _ -> connect(data, edit.text.toString().trim()) }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshWifiStatus() {
        bind.openBtn.post {
            L.d("mWifiManager.wifiState=${mWifiManager.wifiState} isConnected=${isConnected()}")
            bind.statusT.text = when (mWifiManager.wifiState) {
                WifiManager.WIFI_STATE_DISABLED -> "WIFI已关闭"
                WifiManager.WIFI_STATE_DISABLING -> "WIFI关闭中..."
                WifiManager.WIFI_STATE_ENABLED -> "WIFI已打开"
                WifiManager.WIFI_STATE_ENABLING -> "WIFI启动中..."
                WifiManager.WIFI_STATE_UNKNOWN -> "未知"
                else -> "未知"
            }
            val curConnected = isConnected()
            if (lastConnectState != curConnected) {
                bind.csT.text = if (curConnected) {
                    "wifi已连接 ${getWifiIp()}"
                } else {
                    "wifi未连接"
                }
                CmdUtil.exeCommand(getPortCmd, false, handler)
                bind.recyclerView.adapter?.notifyDataSetChanged()
            }
            if (mWifiManager.wifiState == WifiManager.WIFI_STATE_DISABLED) {
                dataList.clear()
                bind.recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun refreshWifiScanList() {
        bind.closeBtn.postDelayed({
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@postDelayed
            }
            filterScanResult(mWifiManager.scanResults)
            refreshWifiStatus()
        }, 10000)
    }

    private fun getWifiIp(): String {
        mWifiInfo = mWifiManager.connectionInfo
        mWifiInfo?.apply {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun filterScanResult(scanResults: List<ScanResult>?) {
        if (scanResults == null || scanResults.isEmpty()) {
            dataList.clear()
        } else {
            mWifiInfo = mWifiManager.connectionInfo
            dataList.removeIf { data ->
                var contains = false
                scanResults.forEach {
                    contains = data.SSID.equals(it.SSID) && data.frequency == it.frequency
                }
                !contains
            }
            var index = 0
            scanResults.forEach {
                var contains = false
                for (data in dataList) {
                    contains = it.SSID == data.SSID
                    if (contains) {
                        break
                    }
                }
                if (!contains && !TextUtils.isEmpty(it.SSID) && it.BSSID != "") {
                    dataList.add(it)
                }
                index++
            }
        }
        bind.recyclerView.post { bind.recyclerView.adapter?.notifyDataSetChanged() }
    }

    override fun initView() {
        ScreenUtils.setStatusBarInvasion(this)
        checkPermission()
        bind.openBtn.setOnClickListener {
            setWifiEnable(true)
        }
        bind.closeBtn.setOnClickListener {
            setWifiEnable(false)
        }
        bind.scanBtn.setOnClickListener {
            startScan()
        }
        bind.setPortBtn.setOnClickListener {
            CmdUtil.exeCommand(setPortCmd, true, null)
        }
        val supportWifiAdb = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)
        val supportQrWifiAdb = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        val b = ServiceManager.getService("adb")
        val service = IAdbManager.Stub.asInterface(b)
        L.w("supportWifiAdb=$supportWifiAdb supportQrWifiAdb=$supportQrWifiAdb PORT=${service.adbWirelessPort}")
    }

    // 添加一个网络并连接
    fun addNetwork(wcg: WifiConfiguration?) {
        val wcgID = mWifiManager.addNetwork(wcg)
        val b = mWifiManager.enableNetwork(wcgID, true)
        println("addNetwork--$wcgID")
        println("addNetwork--$b")
    }

    private fun startScan() {
        L.dd()
        mWifiManager.startScan()
        isScanning = true
        filterScanResult(mWifiManager.scanResults)
    }

    private fun isConnected(): Boolean {
        val wifiInfo = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return wifiInfo?.isConnected ?: false
    }

    private fun setWifiEnable(enable: Boolean) {
        mWifiManager.isWifiEnabled = enable
        if (enable) {
            refreshWifiScanList()
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this@WifiActivity,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this@WifiActivity,
                Manifest.permission.CHANGE_WIFI_STATE,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                Array(2) {
                    Manifest.permission.ACCESS_FINE_LOCATION
                    Manifest.permission.CHANGE_WIFI_STATE
                },
                1
            )
        }
    }

    private fun getHzStr(int: Int): String {
        return if (is5GHz(int)) "5G" else "2.4G"
    }

    /**
     * 判断wifi是否为2.4G
     * @param freq
     * @return
     */
    private fun is24GHz(freq: Int): Boolean {
        return freq in 2401..2499
    }

    /**
     * 判断wifi是否为5G
     * @param freq
     */
    private fun is5GHz(freq: Int): Boolean {
        return freq in 4901..5899
    }

    /**
     * wifi信号强度
     * 0 <-120
     * 1 [-119, -80）
     * 2 [-79, -70）
     * 3 [-69, -55）
     * 4 >= -55
     */
    private fun getSignalStrength(scanResultLevel: Int): Int {
        return if (scanResultLevel > -55) {
            4
        } else if (scanResultLevel > -69) {
            3
        } else if (scanResultLevel > -80) {
            2
        } else if (scanResultLevel > -120) {
            1
        } else {
            0
        }
    }

    private fun isExists(SSID: String): WifiConfiguration? {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        val existingConfigs = mWifiManager.configuredNetworks
        for (existingConfig in existingConfigs) {
            if (existingConfig.SSID == "\"" + SSID + "\"") {
                return existingConfig
            }
        }
        return null
    }

    //然后是一个实际应用方法，只验证过没有密码的情况：
    fun CreateWifiInfo(SSID: String, Password: String, Type: Int): WifiConfiguration? {
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()
        config.SSID = "\"" + SSID + "\""
        val tempConfig: WifiConfiguration? = isExists(SSID)
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId)
        }
        if (Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = ""
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            config.wepTxKeyIndex = 0
        }
        if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true
            config.wepKeys[0] = "\"" + Password + "\""
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            config.wepTxKeyIndex = 0
        }
        if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\""
            config.hiddenSSID = true
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            config.status = WifiConfiguration.Status.ENABLED
        }
        return config
    }
}
