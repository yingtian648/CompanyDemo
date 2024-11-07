package com.zlingsmart.demo.mtestapp.util

import android.content.Context
import android.net.wifi.WifiManager
import com.exa.baselib.utils.L

/**
 * @author lsh
 * @date 2024/11/7 10:15
 * @description
 */
object Tools {
    fun getWifiIp(context: Context):String{
        val mWifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
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
}