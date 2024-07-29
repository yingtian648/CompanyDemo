package com.exa.companyclient

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MEDIA_EJECT
import android.content.Intent.ACTION_MEDIA_MOUNTED
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.SystemBarUtil
import com.exa.companyclient.databinding.ActivityMain3Binding

class SubDisplayActivity : BaseBindActivity<ActivityMain3Binding>() {

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
            SystemBarUtil.showStatusBars(this)
        }
        bind.hideSystemUi.setOnClickListener {
            SystemBarUtil.hideStatusBars(this)
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
        L.dd(javaClass.simpleName + "注册广播MEDIA_MOUNTED,MEDIA_EJECT")
        val filter = IntentFilter()
        filter.addDataScheme("file")
        filter.addAction(ACTION_MEDIA_MOUNTED)
        filter.addAction(ACTION_MEDIA_EJECT)
        registerReceiver(TestReceiver(), filter)
    }

    inner class TestReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                L.dd("MainActivity3 onReceive:" + intent?.action + ", displayid=" + context?.display?.displayId)
                setText("MainActivity3 onReceive:" + intent?.action + ", displayid=" + context?.display?.displayId)
            }
        }
    }

    private fun test() {
        index++
        L.dd("$index")
        Toast.makeText(this, "副屏测试Toast $index", Toast.LENGTH_SHORT).show()
//        showDialog()
    }

    override fun initData() {
        SystemBarUtil.hideStatusBars(this)
    }

    private fun showDialog() {
        val dialog = MDialog(this, R.style.DialogTheme, R.layout.dialog_layout)
        dialog.show()
    }

    private fun setText(msg: String) {
        bind.text.post {
            bind.text.text = msg
        }
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