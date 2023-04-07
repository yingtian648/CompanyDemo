package com.exa.companydemo

import android.app.Dialog
import android.app.UiModeManager
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.ScreenUtils
import com.exa.baselib.utils.StatubarUtil
import com.exa.companydemo.databinding.ActivitySystemuiTestBinding
import kotlin.properties.Delegates

class SystemUITestActivity : BaseBindActivity<ActivitySystemuiTestBinding>() {
    private var isFullScreen = false
    private var nightMode by Delegates.notNull<Int>()
    private var isShowLightBar = false
    private var isShowInvasion = false
    private lateinit var mUiModeManager: UiModeManager
    private var statusColor = R.color.white
    private var naviColor = R.color.white
    override fun setContentViewLayoutId(): Int = R.layout.activity_systemui_test

    private fun initOperateBtn() {
        // 显示弹框 - 全屏弹框
        bind.showDialog.setOnClickListener {
            // 全屏——向后倾斜——上拉下滑显示出SystemUI,SystemUI会挤压Activity高度
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            // 全屏——沉浸模式——上拉下滑显示出SystemUI,SystemUI会挤压Activity高度
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_IMMERSIVE
            // 全屏——粘性沉浸模式——上拉下滑显示出半透明SystemUI,SystemUI覆盖在Activity上面，Activity大小不变
            // SystemUI 会延时隐藏 setOnSystemUiVisibilityChangeListener不回调状态改变
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            // 全屏————粘性沉浸模式——上拉下滑显示出SystemUI,SystemUI覆盖在Activity上面，Activity大小不变
            // SystemUI 不会延时隐藏
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
//                    // Set the content to appear under the system bars so that the
//                    // content doesn't resize when the system bars hide and show.
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    // Hide the nav bar and status bar
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun clickTestBtn() {

    }

    private fun initListener() {
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                L.d("非全屏")
                isFullScreen = false
            } else {
                L.d("全屏")
                isFullScreen = true
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //
    }

    private fun openFullScreenDialog() {
        Dialog(this).apply {
            val view = LayoutInflater.from(this@SystemUITestActivity)
                .inflate(R.layout.dialog_layout, null, false)
            view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                this.dismiss()
            }
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
            this.window?.decorView?.systemUiVisibility = option
            setContentView(view)
        }.show()
    }

    override fun initView() {
        initListener()
        mUiModeManager = getSystemService(UiModeManager::class.java)
        nightMode = mUiModeManager.nightMode
        bind.btnUIMode.text = if (nightMode == UiModeManager.MODE_NIGHT_YES) "白天模式" else "黑夜模式"
        bind.btnStatusBarColor.setOnClickListener {
            L.d("状态栏颜色")
            bind.tv.text = L.msg
            statusColor = if (statusColor != R.color.blue) {
                R.color.blue
            } else {
                R.color.yellow
            }
            StatubarUtil.setStatusBarBgColor(this, statusColor)
        }
        bind.btnStart.setOnClickListener {
            L.d("启动SystemUI")
            bind.tv.text = L.msg
            startSystemUI()
        }
        bind.btnNavColor.setOnClickListener {
            L.d("导航栏颜色")
            bind.tv.text = L.msg
            naviColor = if (naviColor != R.color.blue) {
                R.color.blue
            } else {
                R.color.yellow
            }
            StatubarUtil.setNavigationBarColor(this, naviColor)
        }
        bind.btnAppearance.setOnClickListener {
            L.d("亮色SystemUI")
            bind.tv.text = L.msg
            isShowLightBar = !isShowLightBar
            ScreenUtils.showLightStatusBars(this, isShowLightBar)
        }
        bind.btnCJ.setOnClickListener {
            L.d("沉浸式")
            bind.tv.text = L.msg
            isShowInvasion = !isShowInvasion
            if (isShowInvasion) {
                StatubarUtil.setStatusBarInvasion(this)
            } else {
                StatubarUtil.setUnInvasion(this)
            }
        }
        bind.btnUIMode.setOnClickListener {
            if (nightMode == UiModeManager.MODE_NIGHT_NO) {
                mUiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
            } else {
                mUiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
            }
            nightMode = mUiModeManager.nightMode
            bind.btnUIMode.text = if (nightMode == UiModeManager.MODE_NIGHT_YES) "白天模式" else "黑夜模式"
        }
        bind.btnFull.setOnClickListener {
            L.w("全屏测试")
            bind.tv.text = L.msg
            if (isFullScreen) {
                ScreenUtils.showStatusBars(window)
            } else {
                ScreenUtils.hideStatusBars(window)
            }
            isFullScreen = !isFullScreen
        }
        bind.testBtn.setOnClickListener {
            L.d("点击测试按钮")
            bind.tv.text = L.msg
            clickTestBtn()
        }

        initOperateBtn()
    }

    override fun initData() {
        // 启动SystemUIService
        startSystemUI()
    }

    private fun startSystemUI() {
        try {
            val intent = Intent()
            intent.setClassName("com.exa.systemui", "com.exa.systemui.service.SystemUIService")
            startService(intent)
        } catch (e: Exception) {
            L.e("startService err", e)
        }
    }
}