package com.exa.companydemo.service

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsets.Type.*
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.exa.baselib.utils.L
import com.exa.companydemo.R
import com.google.android.material.badge.BadgeDrawable
import java.util.*


class DemoService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var subWindowManager: WindowManager
    private lateinit var displayManager: DisplayManager
    private var isFullScreen = false
    private lateinit var handler: Handler
    private lateinit var mContext: Context
    private lateinit var contentView: View
    private lateinit var subContentView: View
    private var dialog: Dialog? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            L.d("mContext init:${mContext.hashCode()} $mContext ${mContext.display?.displayId}")
//        }
        HandlerThread("DemoService_Handler").apply {
            start()
            handler = Handler(looper)
        }
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        displayManager = getSystemService(DisplayManager::class.java)

        // 主屏View
        contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout, null)
        contentView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            L.d("onClick cancel_button ")
            dismissDialog()
        }
        contentView.findViewById<Button>(R.id.sure_button).setOnClickListener {
            L.d("onClick sure_button ")
            subContentView.visibility = View.VISIBLE
            subWindowManager.updateViewLayout(subContentView,getLayoutParams())
        }

        // 副屏View
        subContentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout, null)
        subContentView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            dismissSubDialog()
        }
        subContentView.findViewById<Button>(R.id.sure_button).setOnClickListener {
            dismissSubDialog()
        }
        mContext.createDisplayContext(displayManager.getDisplay(2)).apply {
            subWindowManager = this.getSystemService(WindowManager::class.java)
            subContentView.visibility = View.GONE
            subContentView.alpha = 1.0F
            subWindowManager.addView(subContentView, getLayoutParams())
        }
    }

    @SuppressLint("WrongConstant")
    private fun getLayoutParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.title = "subWindow"
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG
        params.flags = params.flags or -2138832568
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.flags = params.flags and -33
        return params
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

    private fun dismissSubDialog() {
        runCatching {
            subWindowManager.removeView(subContentView)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        test()
        return START_STICKY
    }

    private fun test() {
        addView()
//        ShareVideoDialog(this).show()
    }

    private fun checkFullScreen(windowManager: WindowManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insets = windowManager.maximumWindowMetrics.windowInsets
            val naviInsets = insets.getInsets(navigationBars())
            val statusInsets = insets.getInsets(statusBars())
            L.dd(insets.getInsets(navigationBars()).toString())
            L.dd(insets.getInsets(statusBars()).toString())
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
        L.dd("DemoService")
        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_layout, null, false)
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            windowManager.removeView(view)
            stopSelf()
        }
        view.findViewById<Button>(R.id.sure_button).setOnClickListener {
            windowManager.removeView(view)
            stopSelf()
        }
        val titleT = view.findViewById<TextView>(R.id.titleT)
        val lp = WindowManager.LayoutParams()
        lp.title = "DemoService_dialog"
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.type = 2003
        lp.gravity = Gravity.BOTTOM
//        lp.format = PixelFormat.TRANSLUCENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            lp.fitInsetsTypes = navigationBars() or statusBars() //3
            lp.fitInsetsTypes = 3
        }
        lp.flags = (WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
//                or WindowManager.LayoutParams.FLAG_DIM_BEHIND
//                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
//        view.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        windowManager.addView(view, lp)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.dd(javaClass.simpleName)
    }
}