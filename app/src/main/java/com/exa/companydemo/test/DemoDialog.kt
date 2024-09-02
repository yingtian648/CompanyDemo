package com.exa.companydemo.test

import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.exa.baselib.utils.L
import com.exa.companydemo.R
import com.exa.companydemo.databinding.DialogLayoutBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import space.syncore.commonui.widget.dialog.OSDialog

/**
 * @author lsh
 * @date 2024/7/31 14:29
 * @description
 */
class DemoDialog(context: Context) : IOSDialogHelper {
    private lateinit var binding: DialogLayoutBinding
    private var mHelper: OSDialogHelper = OSDialogHelper(
        context, this,
        R.layout.dialog_layout, true, isFullScreen = false
    )
    private lateinit var mDialog: OSDialog
    private val viewModel by mDialog.activityViewModels<DemoViewModel>()
    private var job: Job? = null

    override fun show(fm: FragmentManager) {
        mHelper.show(fm, javaClass.name)
    }

    override fun hide() {
        mHelper.hide()
    }

    override fun dismiss() {
        mHelper.dismiss()
    }

    override fun onBindView(view: View?) {
        L.dd("DemoDialog")
        view?.let {
            binding = DialogLayoutBinding.bind(it)
            initView()
//            view.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                mHelper.mDialog.dialog?.window?.insetsController?.hide(WindowInsets.Type.systemBars())
//            }
        }
    }

    fun initView() {
        binding.sureButton.setOnClickListener { hide() }
        binding.cancelButton.setOnClickListener { hide() }
    }

    override fun onOsDialogCreated(dialog: OSDialog) {
        L.dd("DemoDialog")
        mDialog = dialog
    }

    override fun onDialogShow() {
        super.onDialogShow()
        L.dd()
        job = mDialog.lifecycleScope.launch {
            launch {
                mDialog.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.stateFlow.collectLatest {
                        L.d("STARTED viewModel.stateFlow:$it")
                    }
                }
            }
        }
    }

    override fun onDialogHide() {
        super.onDialogHide()
        L.dd()
        job?.cancel()
    }
}