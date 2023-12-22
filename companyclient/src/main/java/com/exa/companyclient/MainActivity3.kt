package com.exa.companyclient

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_PACKAGE_ADDED
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.ScreenUtils
import com.exa.companyclient.databinding.ActivityMain3Binding

class MainActivity3 : BaseBindActivity<ActivityMain3Binding>() {

    private var display2Context: Context? = null
    private var index = 0
    override fun setContentViewLayoutId(): Int = R.layout.activity_main3

    override fun initView() {
//        ScreenUtils.setStatusBarInvasion(this)//沉浸式
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.displays.forEach {
            if (it.displayId == 2) {
                display2Context = createDisplayContext(it)
            }
        }
        bind.showSystemUi.setOnClickListener {
            L.d("showSystemUi")
            ScreenUtils.showStatusBars(this)
        }
        bind.hideSystemUi.setOnClickListener {
            ScreenUtils.hideStatusBars(this)
            L.d("hideSystemUi")
        }
        bind.testBtn.setOnClickListener {
            L.d("测试按钮")
            test()
        }
        bind.closeBtn.setOnClickListener {
            L.d("关闭按钮")
            finish()
        }
        L.dd(javaClass.simpleName + "注册广播PACKAGE_ADDED")
        val filter = IntentFilter()
        filter.addAction(ACTION_PACKAGE_ADDED)
        registerReceiver(PkgReceiver(), filter)
    }

    class PkgReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            L.dd(intent?.action)
        }
    }

    private fun test() {
        index++
        L.dd("$index")
        Toast.makeText(this, "副屏测试Toast $index", Toast.LENGTH_SHORT).show()
//        showDialog()
    }

    override fun initData() {
        ScreenUtils.hideStatusBars(this)
    }

    private fun showDialog() {
        val dialog = MDialog(this, R.style.DialogTheme, R.layout.dialog_layout)
        dialog.show()
    }

    class MDialog(
        private val context: Context,
        private val themeId: Int = 0,
        private val layoutId: Int,
    ) : Dialog(context, themeId) {
        init {
            setContentView(layoutId)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val surBtn = findViewById<Button>(R.id.sure_button)
            val cancelBtn = findViewById<Button>(R.id.cancel_button)
            val dialogBox = findViewById<LinearLayout>(R.id.dialogBox)
            dialogBox.setBackgroundResource(R.drawable.dialog_bg)

            cancelBtn.setOnClickListener { dismiss() }
            surBtn.setOnClickListener { dismiss() }
        }
    }
}