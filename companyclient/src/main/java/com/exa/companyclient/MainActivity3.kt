package com.exa.companyclient

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.WindowManager
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
    }

    private fun test(){
        index++
//        display2Context?.apply {
//            Toast.makeText(this,"Display2 显示Toast $index",Toast.LENGTH_SHORT).show()
//        }
        Toast.makeText(this,"Display2 显示Toast $index",Toast.LENGTH_SHORT).show()
    }

    override fun initData() {

    }
}