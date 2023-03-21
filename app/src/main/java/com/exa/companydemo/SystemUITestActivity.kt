package com.exa.companydemo

import android.app.UiModeManager
import android.content.Intent
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
    override fun setContentViewLayoutId(): Int = R.layout.activity_systemui_test

    override fun initView() {
        mUiModeManager = getSystemService(UiModeManager::class.java)
        nightMode = mUiModeManager.nightMode
        bind.btnUIMode.text = if (nightMode == UiModeManager.MODE_NIGHT_YES) "白天模式" else "黑夜模式"
        bind.btnStatusBarColor.setOnClickListener {
            L.d("状态栏颜色")
            StatubarUtil.setStatusBarBgColor(this, R.color.blue)
        }
        bind.btnStart.setOnClickListener {
            L.d("启动SystemUI")
            startSystemUI()
        }
        bind.btnNavColor.setOnClickListener {
            L.d("导航栏颜色")
            StatubarUtil.setNavigationBarColor(this, R.color.yellow)
        }
        bind.btnAppearance.setOnClickListener {
            L.d("亮色SystemUI")
            isShowLightBar = !isShowLightBar
            ScreenUtils.showLightStatusBars(this, isShowLightBar)
        }
        bind.btnCJ.setOnClickListener {
            L.d("沉浸式")
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
            if (isFullScreen) {
                ScreenUtils.showStatusBars(this)
            } else {
                ScreenUtils.hideStatusBars(this)
            }
            isFullScreen = !isFullScreen
        }
    }

    override fun initData() {
        // 启动SystemUIService
        startSystemUI()
    }

    private fun startSystemUI(){
        try {
            val intent = Intent()
            intent.setClassName("com.exa.systemui", "com.exa.systemui.service.SystemUIService")
            startService(intent)
        } catch (e: Exception) {
            L.e("startService err", e)
        }
    }
}