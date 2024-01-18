package com.exa.companydemo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.exa.baselib.utils.L
import com.exa.companydemo.R
import com.exa.companydemo.utils.ShareVideoDialog
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
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                checkFullScreen(windowManager)
//            }
//        }, 1000, 1000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        addView()
        ShareVideoDialog(this).show()
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
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        L.dd("DemoService")
        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_layout, null, false)
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            windowManager.removeView(view)
            stopSelf()
        }
        val titleT = view.findViewById<TextView>(R.id.titleT)
        val params = WindowManager.LayoutParams()
        params.title = "DemoService"
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.type = 2003
        params.format = PixelFormat.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            params.fitInsetsTypes = WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars()
        }
        params.flags = (WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
//        view.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        titleT.text = "555"
        windowManager.addView(view, params)

        L.d("titleT displayId:" + titleT.context.display?.displayId)
    }
}