package com.exa.companydemo.accessibility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import com.exa.baselib.utils.L
import com.exa.companydemo.R

/**
 * @author lsh
 * @date 2024/8/30 14:39
 * @description
 */
@SuppressLint("StaticFieldLeak")
object AccessibilityViewManager {
    private var mWindowManager: WindowManager? = null
    private var mView: View? = null

    fun addControllerView(context: Context) {
        mWindowManager = context.getSystemService(WindowManager::class.java)
        mView = LayoutInflater.from(context)
            .inflate(R.layout.layout_myaccessibility_service, null, false)
        initView(mView!!)
//        mWindowManager?.addView(mView!!, getParams())
    }

    private fun initView(view: View) {
        view.findViewById<CheckBox>(R.id.cb).apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    startDoFlow()
                } else {
                    stopDoFlow()
                }
            }
        }
    }

    private fun getParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY)
            .apply {
                width = 100
                height = 100
                gravity = Gravity.TOP or Gravity.END
                x = 200
                y = 200
                format = PixelFormat.TRANSPARENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            }
    }


    private fun startDoFlow() {
        L.dd()
        L.d("------------------------------------")
        MyAccessibilityService.service.rootWindowNode?.apply {
            for (i in 0 until childCount){
                L.d(getChild(i).className.toString() + "," + getChild(i).text)
            }
        }
        L.d("------------------------------------")
    }

    private fun stopDoFlow() {
        L.dd()
    }

}