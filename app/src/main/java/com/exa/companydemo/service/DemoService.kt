package com.exa.companydemo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.exa.baselib.utils.L
import com.exa.baselib.utils.Tools
import com.exa.companydemo.R
import java.util.*

class DemoService : Service() {
    private lateinit var windowManager: WindowManager
    private var isFullScreen = false
    private lateinit var handler: Handler
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        HandlerThread("DemoService_Handler").apply {
            start()
            handler = Handler(looper)
        }
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        Timer().schedule(object : TimerTask() {
            override fun run() {
                checkFullScreen(windowManager)
            }
        }, 1000, 1000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        addView()
        return START_STICKY
    }

    private fun checkFullScreen(windowManager: WindowManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insets = windowManager.maximumWindowMetrics.windowInsets
            val naviInsets = insets.getInsets(WindowInsets.Type.navigationBars())
            val statusInsets = insets.getInsets(WindowInsets.Type.statusBars())
            L.dd(insets.getInsets(WindowInsets.Type.navigationBars()).toString())
            L.dd(insets.getInsets(WindowInsets.Type.statusBars()).toString())
            if (naviInsets.bottom == 0 && statusInsets.top == 0) {
                if (!isFullScreen) {
                    isFullScreen = true
                    L.d(javaClass.name + " 是否全屏：" + isFullScreen)
                }
            } else if (isFullScreen) {
                isFullScreen = false
                L.d(javaClass.name + " 是否全屏：" + isFullScreen)
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun addView() {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.transient_notification_customer, null, false)
        val tv = view.findViewById<TextView>(R.id.message)
        tv.getTag()
        tv.text = "一二三四五六七八一二三四五六七八一二三四五六七八"
//        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
//            windowManager.removeView(view)
//            stopSelf()
//        }
        var with = 0
        Paint().apply {
            textSize = tv.textSize
            with = Tools.getDrawTextWidth(this, tv.text.toString())
        }
        L.d("width = $with textView-width = ${tv.maxWidth}")
        val params = WindowManager.LayoutParams()
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.type = 2008
        params.flags = (WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
        if (isFullScreen) {
            view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        windowManager.addView(view, params)
        handler.postDelayed({
            try {
                windowManager.removeView(view)
            } catch (e: Exception) {
                L.de(e)
            }
        },5000)
    }
}