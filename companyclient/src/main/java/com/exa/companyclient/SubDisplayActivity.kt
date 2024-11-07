package com.exa.companyclient

import android.annotation.SuppressLint
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
import android.os.UserHandle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.SystemBarUtil
import com.exa.companyclient.databinding.ActivityMain3Binding
import com.exa.companyclient.utils.PackageManagerHelper


class SubDisplayActivity : BaseBindActivity<ActivityMain3Binding>() {

    private var display2Context: Context? = null
    private var index = 0
    private var systemUiShow = false
    override fun setContentViewLayoutId(): Int = R.layout.activity_main3

    override fun initView() {
//        ScreenUtils.setStatusBarInvasion(this)//沉浸式
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.displays.forEach {
            if (it.displayId == 2) {
                display2Context = createDisplayContext(it)
            }
        }
        bind.btnSystemUi.setOnClickListener {
            L.d("showSystemUi")
            if (systemUiShow) {
                SystemBarUtil.hideStatusBars(this)
            } else {
                SystemBarUtil.showStatusBars(this)
            }
            updateFooter()
            systemUiShow = !systemUiShow
        }
        bind.testBtn.setOnClickListener {
            L.d("测试按钮")
            try {
                test()
            } catch (e: Exception) {
                L.e("test Exception", e)
            }
        }
        bind.closeBtn.setOnClickListener {
            L.d("关闭按钮")
            finish()
        }
        updateFooter()
        L.dd(javaClass.simpleName + "注册广播MEDIA_MOUNTED,MEDIA_EJECT")
        val filter = IntentFilter()
        filter.addDataScheme("file")
        filter.addAction(ACTION_MEDIA_MOUNTED)
        filter.addAction(ACTION_MEDIA_EJECT)
        registerReceiver(TestReceiver(), filter)
    }

    private fun updateFooter() {
        bind.tvb.postDelayed({
            val location = IntArray(2)
            bind.tvb.getLocationOnScreen(location)
            bind.tvb.text = "bottom:" + (location[1] + bind.tvb.height)
        }, 1000)
    }

    inner class TestReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                L.dd("MainActivity3 onReceive:" + intent?.action + ", displayid=" + context?.display?.displayId)
                setText("MainActivity3 onReceive:" + intent?.action + ", displayid=" + context?.display?.displayId)
            }
        }
    }

    @Throws(Exception::class)
    private fun test() {
        index++
        L.dd("$index")
        testToast()
//        showDialog()

//        val am = getSystemService(ActivityManager::class.java)
//        L.d("------------------------")
//        am.getRunningTasks(Int.MAX_VALUE)?.forEach { task ->
//            if (task.toString().contains("userId=10")) {
//                task.topActivity?.packageName?.let {
//                    getLabel(it)
//                }
//            }
//        }
        L.d("------------------------")
    }

    private fun getLabel(pkgName: String) {
        L.dd(pkgName)
        val user = UserHandle.getUserHandleForUid(10)
        val helper = PackageManagerHelper(this)
        helper.getApplicationInfo(pkgName, user, 0)?.let {
            it.loadLabel(packageManager)
            val label = packageManager.getUserBadgedLabel(it.loadLabel(packageManager), user)
            val msg =
                pkgName + ":" + it.loadLabel(packageManager).toString() + "," + label.toString()
            L.dd(msg)
            setText(msg)
        }
    }

    private fun testToast() {
        Toast.makeText(
            this,
            "一二三四五六七七八九十一二三四五六七七八九十一二三四五六七七八九十一二三四五六七七八九十$index",
            Toast.LENGTH_SHORT
        ).show()
        bind.testBtn.removeCallbacks(runnable)
        bind.testBtn.postDelayed(runnable, 7000)
    }

    private val runnable =
        Runnable { Toast.makeText(activity, "副屏测试Toast $index", Toast.LENGTH_SHORT).show() }

    override fun initData() {

    }

    private fun showDialog() {
        val dialog = MDialog(this, R.style.DialogTheme, R.layout.dialog_layout)
        dialog.show()
    }

    private var lines: Int = 0

    @SuppressLint("SetTextI18n")
    private fun setText(msg: String) {
        lines++
        if (lines % 20 == 0) {
            bind.text.text = ""
            lines = 0
        }
        bind.text.post {
            bind.text.text = bind.text.text.toString() + "\n" + msg
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