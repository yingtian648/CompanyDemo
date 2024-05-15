package com.exa.companydemo.utils

import android.text.TextUtils
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author lsh
 * @date 2024/5/13 11:40
 * @description
 */
object AllMenuJsonParser {
    private const val TAG = "AllMenuParser"
    private const val CONFIG_PATH = "/vendor/etc/all_menu_config.json"
    private const val FILED_WHITE_LIST = "whiteList"
    private const val FILED_BLACK_LIST = "blackList"
    private const val FILED_NAME = "name"
    private const val FILED_PACKAGE = "package"

    /**
     * @return Pair.first whiteList,Pair.second blackList
     */
    suspend fun loadAllMenuConfig(): Pair<List<ConfigInfo>, List<ConfigInfo>>? {
        val file = File(CONFIG_PATH)
        if (file.isFile && file.exists()) {
            try {
                FileInputStream(file).use { fis ->
                    val br = BufferedReader(InputStreamReader(fis))
                    val sb = StringBuilder()
                    var line: String?
                    while ((br.readLine().also { line = it }) != null) {
                        sb.append(line)
                    }
                    br.close()
                    fis.close()
                    val jsonContent = sb.toString()
                    if (TextUtils.isEmpty(jsonContent)) {
                        Log.e(TAG, "jsonContent is empty")
                    } else {
                        return parse(jsonContent)
                    }
                }
            } catch (e: IOException) {
                Log.w(TAG, "getVRResVersion err", e)
            } catch (e: JSONException) {
                Log.w(TAG, "getVRResVersion err", e)
            }
        } else {
            Log.e(TAG, "loadAllMenuConfig: not find config file")
        }
        return null
    }

    private fun parse(content: String): Pair<List<ConfigInfo>, List<ConfigInfo>>? {
        Log.i(TAG, "parse: $content")
        val jObj = JSONObject(content)
        val blackList = mutableListOf<ConfigInfo>()
        val whiteList = mutableListOf<ConfigInfo>()
        if (jObj.has(FILED_BLACK_LIST)) {
            val blackObj = jObj.getJSONArray(FILED_BLACK_LIST)
            Log.i(TAG, "parse blackObj: ${blackObj.length()}")
            for (i in 0 until blackObj.length()) {
                val obj = blackObj.getJSONObject(i)
                if (obj.has(FILED_NAME) && obj.has(FILED_PACKAGE)) {
                    blackList.add(
                        ConfigInfo(obj.getString(FILED_NAME), obj.getString(FILED_PACKAGE))
                    )
                }
            }
            Log.i(TAG, "blackList: $blackList")
        }
        if (jObj.has(FILED_WHITE_LIST)) {
            val whiteObj = jObj.getJSONArray(FILED_WHITE_LIST)
            Log.i(TAG, "parse whiteObj: ${whiteObj.length()}")
            for (i in 0 until whiteObj.length()) {
                val obj = whiteObj.getJSONObject(i)
                if (obj.has(FILED_NAME) && obj.has(FILED_PACKAGE)) {
                    whiteList.add(
                        ConfigInfo(obj.getString(FILED_NAME), obj.getString(FILED_PACKAGE))
                    )
                }
            }
            Log.i(TAG, "whiteList: $whiteList")
        }
        return Pair(whiteList, blackList)
    }

    data class ConfigInfo(val name: String?, val packageName: String)
}
