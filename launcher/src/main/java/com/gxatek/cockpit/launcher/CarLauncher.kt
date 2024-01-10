package com.gxatek.cockpit.launcher

import android.view.WindowManager
import com.exa.baselib.utils.L
import com.gxatek.cockpit.launcher.databinding.ActivityLauncherBinding

class CarLauncher : BaseActivity<ActivityLauncherBinding>() {

    override fun getViewBinding(): ActivityLauncherBinding{
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