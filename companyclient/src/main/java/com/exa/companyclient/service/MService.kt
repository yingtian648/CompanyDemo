package com.exa.companyclient.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import com.exa.baselib.utils.L

/**
 * @Author lsh
 * @Date 2023/8/16 10:53
 * @Description
 */
class MService : Service() {
    private val thread = HandlerThread("MService")
    private lateinit var handler: Handler
    private val tag = "MService"
    private var index = 1
    private var indexInner = 1
    override fun onBind(intent: Intent?): IBinder? {
        L.d(tag, "onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        L.d(tag, "onCreate")
        thread.start()
        handler = Handler(thread.looper)
        doBackground(index)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        L.d(tag, "onStartCommand")
        index++
        return super.onStartCommand(intent, flags, startId)
    }


    private fun doBackground(index: Int) {
        handler.post {
            while (true) {
                indexInner++
                Thread.sleep(1000)
                L.d("MService doBackground:$index $indexInner")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        L.d(tag, "onDestroy")
    }
}