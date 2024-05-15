package com.raite.launcher.ui

import android.content.Intent
import com.exa.baselib.utils.SystemBarUtil
import com.gxatek.cockpit.launcher.BaseActivity
import com.gxatek.cockpit.launcher.R
import com.gxatek.cockpit.launcher.databinding.ActivitySecondaryHomeBinding

/**
 * @author lsh
 * @date 2024/5/10 9:53
 * @description
 */
class HomeActivity:BaseActivity<ActivitySecondaryHomeBinding>() {
    override fun getViewBinding(): ActivitySecondaryHomeBinding =
        ActivitySecondaryHomeBinding.inflate(layoutInflater)

    override fun initView() {
        binding.tvTitle.text = "HomeActivity"
        binding.main.setBackgroundResource(R.drawable.win_bg)
        SystemBarUtil.setInvasionStatusBar(this)
    }

    override fun initData() {
        binding.tvTitle.setOnClickListener {
            val intent = Intent(this, DefaultDisplayActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}