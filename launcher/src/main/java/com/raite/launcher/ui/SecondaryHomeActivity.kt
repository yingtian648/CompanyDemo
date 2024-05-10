package com.raite.launcher.ui

import android.content.Intent
import com.exa.baselib.utils.SystemBarUtil
import com.gxatek.cockpit.launcher.BaseActivity
import com.gxatek.cockpit.launcher.databinding.ActivitySecondaryHomeBinding

class SecondaryHomeActivity : BaseActivity<ActivitySecondaryHomeBinding>() {

    override fun getViewBinding(): ActivitySecondaryHomeBinding =
        ActivitySecondaryHomeBinding.inflate(layoutInflater)

    override fun initView() {
        SystemBarUtil.setInvasionStatusBar(this)
        binding.tvTitle.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun initData() {

    }
}