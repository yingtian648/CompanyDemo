package com.exa.systemui

import android.app.UiModeManager
import android.content.res.Configuration
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.ScreenUtils
import com.exa.baselib.utils.StatubarUtil
import com.exa.systemui.databinding.ActivityMainBinding

class MainActivity : BaseBindActivity<ActivityMainBinding>() {
    private var isFullScreen = false
    private var isNightMode = false
    private var isShowLightBar = false
    private lateinit var mUiModeManager: UiModeManager
    override fun setContentViewLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        mUiModeManager = getSystemService(UiModeManager::class.java)
        bind.btnStatusBarColor.setOnClickListener {
            L.d("状态栏颜色")
            StatubarUtil.setStatusBarBgColor(this, R.color.blue)
        }
        bind.btnNavColor.setOnClickListener {
            L.d("导航栏颜色")
            StatubarUtil.setNavigationBarColor(this, R.color.yellow)
        }
        bind.btnCJ.setOnClickListener {
            L.d("沉浸式")
            isShowLightBar = !isShowLightBar;
            ScreenUtils.showLightStatusBars(this, isShowLightBar)
//            StatubarUtil.setStatusBarInvasion(this)
        }
        bind.btnUIMode.setOnClickListener {
            if (isNightMode) {
                mUiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
            } else {
                mUiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
            }
            isNightMode = !isNightMode
        }
        bind.btnFull.setOnClickListener {
            L.w("全屏测试")
            if (isFullScreen) {
                ScreenUtils.showStatusBars(this)
            } else {
                ScreenUtils.setFullScreen(this)
            }
            isFullScreen = !isFullScreen
        }
    }

    override fun initData() {

    }
}