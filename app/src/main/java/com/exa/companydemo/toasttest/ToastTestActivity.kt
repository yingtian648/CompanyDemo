package com.exa.companydemo.toasttest

import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.CarToast
import android.widget.TextView
import android.widget.Toast
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.L
import com.exa.companydemo.R
import com.exa.companydemo.databinding.ActivityToastTestBinding

class ToastTestActivity : BaseBindActivity<ActivityToastTestBinding>(), View.OnClickListener {

    override fun setContentViewLayoutId(): Int = R.layout.activity_toast_test

    override fun initView() {
        bind.btnTNormal.setOnClickListener(this)
        bind.btnCTNormal.setOnClickListener(this)
        bind.btnCTDIY.setOnClickListener(this)
        bind.btnTDIY.setOnClickListener(this)
        bind.rbShort.isChecked = true
        bind.rbTop.isChecked = true
        bind.rbx0.isChecked = true
        bind.rby0.isChecked = true
    }

    override fun onClick(v: View?) {
        val words: CharSequence = if (TextUtils.isEmpty(bind.edit.text)) {
            "请输入要显示的文字..."
        } else {
            bind.edit.text
        }
        val time: Int = if (bind.rbLong.isChecked) {
            Toast.LENGTH_LONG
        } else {
            Toast.LENGTH_SHORT
        }
        val gravity: Int = if (bind.rbTop.isChecked) {
            Gravity.TOP
        } else if (bind.rbLeft.isChecked) {
            Gravity.START
        } else if (bind.rbRight.isChecked) {
            Gravity.END
        } else if (bind.rbBottom.isChecked) {
            Gravity.BOTTOM
        } else {
            Gravity.CENTER
        }
        val xOffset: Int = if (bind.rbx0.isChecked) {
            0
        } else {
            50
        }
        val yOffset: Int = if (bind.rby0.isChecked) {
            0
        } else {
            50
        }

        v?.id.let {
            when (it) {
                R.id.btnTNormal -> {
                    L.d("Toast.show:time=$time words=$words")
                    Toast.makeText(this, "$words", time).show()
                }
                R.id.btnCTNormal -> {
                    L.d("CarToast.show:time=$time words=$words")
                    CarToast.makeText(this, "$words", time).show()
                }
                R.id.btnTDIY -> {
                    L.d("Toast自定义View:time=$time words=$words")
                    showCustomerToast(words,time,gravity,xOffset, yOffset)
                }
                R.id.btnCTDIY -> {
                    L.d("CarToast自定义View:time=$time words=$words")
                    showCustomerCarToast(words,time,gravity,xOffset, yOffset)
                }
                else -> Unit
            }
        }
    }

    private fun showCustomerToast(
        words: CharSequence,
        duration: Int,
        gravity: Int,
        xOffset: Int,
        yOffset: Int
    ) {
        val toast = Toast(this)
        val view = LayoutInflater.from(this).inflate(R.layout.toast_test, null, false)
        val tv = view.findViewById<TextView>(R.id.message)
        tv.text = words
        toast.view = view
        toast.setGravity(gravity, xOffset, yOffset)
        toast.duration = duration
        toast.show()
    }

    private fun showCustomerCarToast(
        words: CharSequence,
        duration: Int,
        gravity: Int,
        xOffset: Int,
        yOffset: Int
    ) {
        val toast = CarToast(this)
        val view = LayoutInflater.from(this).inflate(R.layout.toast_test, null, false)
        val tv = view.findViewById<TextView>(R.id.message)
        tv.text = words
        toast.view = view
        toast.setGravity(gravity, xOffset, yOffset)
        toast.duration = duration
        toast.show()
    }

    override fun initData() {

    }
}