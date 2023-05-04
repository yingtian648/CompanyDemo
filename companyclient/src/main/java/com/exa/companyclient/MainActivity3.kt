package com.exa.companyclient

import android.view.WindowManager
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.ScreenUtils
import com.exa.companyclient.databinding.ActivityMain3Binding

class MainActivity3 : BaseBindActivity<ActivityMain3Binding>() {

    override fun setContentViewLayoutId(): Int = R.layout.activity_main3

    override fun initView() {
        bind.showSystemUi.setOnClickListener {
            L.d("showSystemUi")
            ScreenUtils.showStatusBars(this)
        }
        bind.hideSystemUi.setOnClickListener {
            ScreenUtils.hideStatusBars(this)
            L.d("hideSystemUi")
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun initData() {

    }
}