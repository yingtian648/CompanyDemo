package com.exa.companydemo.test

import android.view.View
import androidx.fragment.app.FragmentManager
import space.syncore.commonui.widget.dialog.OSDialog

/**
 * 1.OSDialogHelper需要实现它的接口来提供给用户使用
 * 2.回调接口
 */
interface IOSDialogHelper {

    /**
     * 显示Dialog
     * 如果DialogFragment已经构建, 则会调用DialogFragment中的mDialog.show()
     * 如果DialogFragment未构建，则会调用DialogFragment.show(FragmentManager,String)
     */
    fun show(fm: FragmentManager) = Unit

    /**
     * 隐藏Dialog,但不释放
     * 调用DialogFragment中的mDialog.hide()
     */
    fun hide() = Unit

    /**
     * 释放Dialog
     * DialogFragment.dismissAllowingStateLoss()
     */
    fun dismiss() = Unit

    /**
     * 1.子类在此方法中使用DataBinding绑定View
     * 2.仅在第一次创建Dialog时回调
     * 3.在OSDialog的onDialogShown时回调
     * @param view 设置到OSDialog的Layout资源解析出来的View
     */
    fun onBindView(view: View?) = Unit

    /**
     * 用于反馈OSDialog已经创建完成
     * 注：不应在此方法中执行耗时操作
     */
    fun onOsDialogCreated(dialog: OSDialog) = Unit

    /**
     * 1.在调用DialogFragment的mDialog.hide()时回调
     * 2.点击界面外隐藏时调用
     */
    fun onDialogHide() = Unit

    /**
     * 当dialog显示时回调
     * 1.在调用DialogFragment的mDialog.show()时回调
     * 2.在onDialogShown(view: View?)之后会调用一次
     */
    fun onDialogShow() = Unit

    /**
     * 调用DialogFragment.dismiss时回调
     */
    fun onDismiss() = Unit
}