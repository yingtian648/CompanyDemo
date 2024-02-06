package com.exa.companydemo.service

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.app.Service
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.exa.baselib.BaseConstants
import com.exa.baselib.utils.L
import com.exa.companydemo.App
import com.exa.companydemo.R
import java.util.*

class DemoService : Service() {
    private lateinit var windowManager: WindowManager
    private var isFullScreen = false
    private lateinit var handler: Handler
    private lateinit var mContext: Context
    private lateinit var contentView: View
    private var dialog: Dialog? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        contentView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_layout, null, false)
        contentView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            dismissDialog()
        }
        contentView.findViewById<Button>(R.id.sure_button).setOnClickListener {
            dismissDialog()
        }
        mContext = this
        HandlerThread("DemoService_Handler").apply {
            start()
            handler = Handler(looper)
        }
        L.dd("context 1:" + mContext.toString())
        L.dd("context 2:" + App.getContext().toString())
    }

    private fun dismissDialog() {
        runCatching {
            windowManager.removeView(contentView)
        }
        runCatching {
            dialog?.dismiss()
        }
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        addView()
//        ShareVideoDialog(this).show()
        test()
        return START_STICKY
    }

    private fun test() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            L.dd(javaClass.simpleName + " display=" + mContext.display?.displayId)
        }
        dialog = Dialog(mContext)
        dialog?.apply {
            window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
            setContentView(contentView)
        }?.show()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    @SuppressLint("WrongConstant")
    private fun addView() {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            L.d("titleT displayId:" + titleT.context.display?.displayId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        L.dd(javaClass.simpleName)
    }
}