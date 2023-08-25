package com.exa.companydemo.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.view.WindowManager
import java.util.*

/**
 * @Author lsh
 * @Date 2023/7/20 14:39
 * @Description
 */
class TestAddViewService :Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }
}