package com.exa.companydemo.test

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ktx.R
import com.exa.companydemo.utils.LogUtil
import space.syncore.commonui.widget.dialog.OSDialog

/**
 * @author lsh
 * @date 2024/7/26 16:29
 * @description
 */
open class OsDialogHelper(context: Context, layout: Int = 0) : OSDialog.DialogListener {

    private var mCurrShowing = false
    protected var mDialog: OSDialog

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }

    open fun onOsDialogCreated(oSDialog: OSDialog) = Unit

    init {
        val builder = OSDialog.Builder(context).setHidedOnTouchOutside()
        if (layout != 0) {
            LogUtil.i(mObjectTag, "setContentLayout")
            builder.setContentLayout(layout)
        }
        mDialog = builder.build()
        onOsDialogCreated(mDialog)
    }

    fun show(manager: FragmentManager) {
        LogUtil.i(mObjectTag, "show ${mDialog.dialog}")
        if (mDialog.dialog != null) {
            mDialog.dialog?.show()
        } else {
            mDialog.setDialogListener(this)
            mDialog.show(manager, javaClass.name)
        }
    }

    fun hide() {
        LogUtil.i(mObjectTag, "hide")
        mCurrShowing = false
        mDialog.dialog?.hide()
    }

    fun dismiss() {
        LogUtil.i(mObjectTag, "dismiss")
        mCurrShowing = false
        mDialog.dismiss()
    }

    override fun onDialogShown() {
        super.onDialogShown()
        mDialog.dialog?.setTitle(javaClass.name)
    }

    /**
     * 如果弹框未隐藏，则不应释放改dialog
     */
    fun dismissIfDialogHide(): Boolean {
        LogUtil.i(mObjectTag, "dismissIfDialogHide $mCurrShowing")
        if (!mCurrShowing) {
            mDialog.dismiss()
            return true
        }
        return false
    }

    override fun onDialogDismissed() {
        super.onDialogDismissed()
        mCurrShowing = false
    }
}