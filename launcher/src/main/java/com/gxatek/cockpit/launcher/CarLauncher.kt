package com.gxatek.cockpit.launcher

import android.view.WindowManager
import com.exa.baselib.utils.L
import com.gxatek.cockpit.launcher.databinding.ActivityLauncherBinding

class CarLauncher : BaseActivity<ActivityLauncherBinding>() {

    override fun getViewBinding(): ActivityLauncherBinding{
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        return ActivityLauncherBinding.inflate(layoutInflater)
    }

    override fun initView() {
//        binding.surface.holder.setFormat(PixelFormat.TRANSPARENT);
        L.dd()
    }

    override fun initData() {
        L.dd()
    }


}