package com.exa.companydemo.test

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.exa.companydemo.R
import com.exa.companydemo.utils.LogUtil
import space.syncore.commonui.widget.dialog.OSDialog

/**
 * 用于实现复杂的OsDialog
 * 1.子类可以使用OSDialog的lifecycleScope协程处理相关数据流。
 * 2.子类可以正常管理OSDialog的生命周期。
 * 3.此Dialog点击界面外时走的mDialog.hide(),所以，需要在切换页面时手动Dismiss掉
 * 4.添加监听OSDialog进入onPause时，已经hide的dialog需要dismiss,否则生命周期再次进入onResume时会重新显示
 */
class OSDialogHelper(
    private val context: Context,
    private val mListener: IOSDialogHelper,
    layout: Int = 0,
    hidedOnTouchOutside: Boolean = true,
    style: Int = 0,
    private val isFullScreen: Boolean = false,
) : OSDialog.DialogListener, IOSDialogHelper {

    private val mObjectTag by lazy {
        "${javaClass.simpleName}@${System.identityHashCode(this)}"
    }
    private var mDialog: OSDialog
    private var mDismissed = false

    init {
        val builder = OSDialog.Builder(context)
        if (isFullScreen) {
            setFullScreenSize(builder)
        }
        if (hidedOnTouchOutside) {
            builder.setHidedOnTouchOutside()
        } else {
            builder.setCanceledOnTouchOutside(false)
        }
        if (style == 0) {
            if (hidedOnTouchOutside) {
                builder.setStyle(R.style.Carsettings2DialogHelper_autoHide)
            } else {
                builder.setStyle(R.style.Carsettings2DialogHelper)
            }
        } else {
            builder.setStyle(style)
        }
        if (layout != 0) {
            builder.setContentLayout(layout)
        }
        mDialog = builder.build()
        mListener.onOsDialogCreated(mDialog)
    }

    /**
     * 设置全屏宽高尺寸
     */
    private fun setFullScreenSize(builder: OSDialog.Builder) {
        val windowManager = context.getSystemService(WindowManager::class.java)
        builder.setPosition(0,0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val rect = windowManager.currentWindowMetrics.bounds
            builder.setSize(rect.right.toFloat(), rect.bottom.toFloat())
        } else {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            builder.setSize(metrics.widthPixels.toFloat(), metrics.heightPixels.toFloat())
        }
    }

    /**
     * @param autoHideOthers 是否对其它OSDialog执行hide操作
     * @param showSystemOverlay 是否显示自定义WindowManager.LayoutParams的dialog,与params组合使用
     */
    fun show(
        fm: FragmentManager,
        tag: String,
        autoHideOthers: Boolean = true,
        showSystemOverlay: Boolean = false,
        params: WindowManager.LayoutParams? = null
    ) {
        LogUtil.i(mObjectTag, "show ${mDialog.dialog} autoHideOthers=$autoHideOthers $tag")
        if (autoHideOthers) {
            fm.fragments.forEach {
                if (it.tag != tag && it is OSDialog) {
                    LogUtil.i(mObjectTag, "autoHideOthers ${it.tag}")
                    it.hide()
                }
            }
        }
        mDialog.setDialogListener(this)
        val firstShow = mDialog.dialog == null
        if (showSystemOverlay && params != null) {
            mDialog.showSystemOverlay(context, params)
        } else {
            mDialog.show(fm, tag)
        }
        LogUtil.i(mObjectTag, "${mDialog.tag} onDialogShow firstShow=$firstShow")
        if (firstShow) {
            mListener.onOsDialogCreated(mDialog)
        }
        mDismissed = false
    }

    override fun hide() {
        LogUtil.i(mObjectTag, "${mDialog.tag} dialog.hide")
        mDialog.hide()
    }

    override fun dismiss() {
        LogUtil.i(mObjectTag, "${mDialog.tag} dismiss")
        mDismissed = true
        mDialog.dismissAllowingStateLoss()
    }

    override fun onDialogShown() {
        LogUtil.i(mObjectTag, "${mDialog.tag} onDialogShown")
        mDialog.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_PAUSE) {
                    dismissIfDialogHideOnPause()
                }
            }
        })
        mListener.onDialogShow()
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        if (isFullScreen) {
            view.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        mDialog.dialog?.setTitle(javaClass.simpleName)
        mListener.onBindView(mDialog.getContentView())
    }

    override fun onDialogReShown() {
        super.onDialogReShown()
        mListener.onDialogShow()
    }

    override fun onDialogHided() {
        super.onDialogHided()
        mListener.onDialogHide()
    }

    override fun onDialogDismissed() {
        super.onDialogDismissed()
        LogUtil.i(mObjectTag, "${mDialog.tag} onDialogDismissed")
        mListener.onDismiss()
    }

    /**
     * 在onPause时未显示的dialog应该dismiss
     * 否则再次进入onResume时会再次显示
     */
    fun dismissIfDialogHideOnPause() {
        if (mDialog.dialog?.isShowing == false) {
            LogUtil.i(mObjectTag, "dismissIfDialogHideOnPause ${mDialog.tag}")
            dismiss()
        }
    }
}