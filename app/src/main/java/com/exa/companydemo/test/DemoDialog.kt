package com.exa.companydemo.test

import android.content.Context
import com.exa.companydemo.R
import com.exa.companydemo.databinding.DialogLayoutBinding

/**
 * @author lsh
 * @date 2024/7/31 14:29
 * @description
 */
class DemoDialog(context: Context) : OsDialogHelper(context, R.layout.dialog_layout) {
    private lateinit var binding: DialogLayoutBinding
    override fun onDialogShown() {
        super.onDialogShown()
        mDialog.getContentView()?.let{
            binding = DialogLayoutBinding.bind(it)
            initView()
        }
    }

    fun initView(){
        binding.sureButton.setOnClickListener { hide() }
        binding.cancelButton.setOnClickListener { hide() }
    }
}