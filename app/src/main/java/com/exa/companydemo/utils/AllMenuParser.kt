package com.exa.companydemo.utils

import android.util.Log
import android.util.Xml
import org.json.JSONException
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author lsh
 * @date 2024/5/13 16:39
 * @description
 */
object AllMenuParser {
    private const val TAG = "AllMenuParser"
    private const val CONFIG_PATH = "/vendor/etc/all_menu_config.xml"
    private const val FIELD_GEN = "apps"
    private const val FIELD_WHITE_LIST = "white-list"
    private const val FIELD_BLACK_LIST = "black-list"
    private const val FIELD_ITEM = "item"

    /**
     * @return Pair.first whiteList,Pair.second blackList
     */
    suspend fun loadAllMenuConfig(): Pair<List<String>, List<String>>? {
        Log.i(TAG, "loadAllMenuConfig start")
        val file = File(CONFIG_PATH)
        if (file.isFile && file.exists()) {
            try {
                FileInputStream(file).use { fis ->
                    val parser = Xml.newPullParser()
                    parser.setInput(fis, StandardCharsets.UTF_8.name())
                    val whiteList = mutableListOf<String>()
                    val blackList = mutableListOf<String>()
                    var eventType = parser.eventType
                    var parserType = FIELD_WHITE_LIST
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        when (eventType) {
                            XmlPullParser.START_DOCUMENT -> {}
                            XmlPullParser.START_TAG -> {
                                when (parser.name) {
                                    FIELD_WHITE_LIST -> {
                                        parserType = FIELD_WHITE_LIST
                                    }

                                    FIELD_BLACK_LIST -> {
                                        parserType = FIELD_BLACK_LIST
                                    }

                                    FIELD_ITEM -> {
                                        if (parserType == FIELD_WHITE_LIST) {
                                            whiteList.add(parser.nextText())
                                        } else {
                                            blackList.add(parser.nextText())
                                        }
                                    }
                                }
                            }

                            XmlPullParser.END_TAG -> Unit
                            else -> {}
                        }
                        eventType = parser.next()
                    }
                    fis.close()
                    Log.i(TAG, "loadAllMenuConfig whiteList.size=${whiteList.size} $whiteList")
                    Log.i(TAG, "loadAllMenuConfig blackList.size=${blackList.size} $blackList")
                    return Pair(whiteList, blackList)
                }
            } catch (e: IOException) {
                Log.w(TAG, "loadAllMenuConfig err", e)
            } catch (e: JSONException) {
                Log.w(TAG, "loadAllMenuConfig err", e)
            }
        } else {
            Log.e(TAG, "loadAllMenuConfig: not find config file")
        }
        return null
    }
}