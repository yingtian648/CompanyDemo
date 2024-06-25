package com.raite.launcher.ui

import android.content.Intent
import com.exa.baselib.utils.L
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
        L.dd("HomeActivity")
        binding.tvTitle.text = "HomeActivity"
        binding.main.setBackgroundResource(R.drawable.win_bg)
        SystemBarUtil.setInvasionStatusBar(this)
        binding.edit.setOnFocusChangeListener { v, hasFocus ->
            L.d("OnFocusChange $hasFocus")
        }
    }

    override fun initData() {
        binding.tvTitle.setOnClickListener {
            val intent = Intent(this, DefaultDisplayActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        L.dd("HomeActivity")
    }
}