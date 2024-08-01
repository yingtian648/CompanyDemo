//package space.syncore.commonui.widget.dialog
//
//import android.animation.ObjectAnimator
//import android.annotation.SuppressLint
//import android.app.Dialog
//import android.content.Context
//import android.content.DialogInterface
//import android.graphics.PixelFormat
//import android.graphics.Rect
//import android.os.Bundle
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.view.Window
//import android.view.WindowManager
//import android.widget.FrameLayout
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.FragmentManager
//import space.syncore.commonui.R
//import space.syncore.commonui.core.background.OSBackgroundHelper
//import space.syncore.commonui.core.text.OSTextHelper
//import space.syncore.commonui.widget.OSButton
//import space.syncore.commonui.widget.OSTextView
//
//open class OSDialog: DialogFragment() {
//
//    private val osDialogTag = "osDialogTag"
//
//    private var mParentView: View? = null
//    private var mContentView: View? = null
//    private var layoutResID: Int? = null
//    private var customResID: Int? = null
//    private var mStyleResId: Int? = null
//
//    private var mWidth: Float? = null
//    private var mHeight: Float? = null
//    private var positionX: Int? = 0
//    private var positionY: Int? = 0
//
//    // 弹框主标题
//    private var titleText: String? = null
//    private var mTitleView: OSTextView? = null
//
//    // 弹窗信息
//    private var messageText: String? = null
//    private var mMessageView: OSTextView? = null
//
//    // 弹窗按钮
//    private var singleButtonContainer: View? = null
//    private var doubleButtonContainer: View? = null
//    private var tripleButtonContainer: View? = null
//    private var firstButtonText: String? = null
//    private var secondButtonText: String? = null
//    private var thirdButtonText: String? = null
//
//    private var singleButtonView: OSButton? = null
//
//    private var firstButtonView: OSButton? = null
//    private var secondButtonView: OSButton? = null
//
//    private var topButtonView: OSButton? = null
//    private var middleButtonView: OSButton? = null
//    private var bottomButtonView: OSButton? = null
//
//    private var mContentLayout: FrameLayout? = null // 弹窗自定义视图
//
//    private var dimAmount: Float? = null // 设置遮罩透明度，0为全透明，1为全黑
//
//    private var dialogComponent: DialogComponent = DialogComponent.DialogFragment // 弹窗组件
//
//    private var dialogType: DialogType = DialogType.untitled // 弹窗类型
//
//    private var canceledOnTouchOutside: Boolean? = null // 点击外部是否可取消
//
//    private var hidedOnTouchOutside: Boolean? = null // 点击外部是否可销毁
//
//    private var fragmentManager: FragmentManager? = null // 弹窗所在的FragmentManager
//
//    private var isAddedWindowManager = false // 是否已添加到WindowManager
//
//    private var dialogAnimRes: Int? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        when (dialogComponent) {
//            DialogComponent.DialogFragment -> {
//                setStyle(STYLE_NO_TITLE, R.style.OSDialogDefaultFullScreen)
//            }
//            DialogComponent.Dialog -> {}
//        }
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return when (dialogComponent) {
//            DialogComponent.Dialog -> {
//                val dialog = Dialog(requireActivity(), R.style.TransparentDialog)
//                val inflater = requireActivity().layoutInflater
//                mParentView = layoutResID?.let { inflater.inflate(it, null) }
//                initDialogView(inflater, mParentView)
//                mParentView?.let { dialog.setContentView(it) }
//                return dialog
//            }
//
//            DialogComponent.DialogFragment -> {
//                // 如果不是对话框，则返回默认的Dialog对象
//                super.onCreateDialog(savedInstanceState)
//            }
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return when(dialogComponent) {
//            DialogComponent.Dialog -> null // 如果是对话框，则返回null
//            DialogComponent.DialogFragment -> {
//                mParentView = layoutResID?.let { inflater.inflate(it, container, false) }
//                initDialogView(inflater, mParentView)
//                return mParentView
//            }
//        }
//    }
//
//    private fun initDialogView(inflater: LayoutInflater, parentView: View?){
//        mContentView = parentView?.findViewById(R.id.ll_content_container)
//        mContentLayout = parentView?.findViewById(R.id.fl_content_layout)
//        mTitleView = parentView?.findViewById(R.id.tv_dialog_title)
//        mMessageView = parentView?.findViewById(R.id.tv_dialog_message)
//        singleButtonContainer = parentView?.findViewById(R.id.ll_single_button_container)
//        singleButtonView = parentView?.findViewById(R.id.btn_single)
//        doubleButtonContainer = parentView?.findViewById(R.id.ll_double_button_container)
//        firstButtonView = parentView?.findViewById(R.id.btn_first)
//        secondButtonView = parentView?.findViewById(R.id.btn_second)
//        tripleButtonContainer = parentView?.findViewById(R.id.ll_triple_button_container)
//        topButtonView = parentView?.findViewById(R.id.btn_top)
//        middleButtonView = parentView?.findViewById(R.id.btn_middle)
//        bottomButtonView = parentView?.findViewById(R.id.btn_bottom)
//
//        if (customResID != null && customResID != 0) {
//            val customLayout = inflater.inflate(customResID!!, null)
//            customLayout?.let { mContentLayout?.addView(it) }
//        }
//
//        mTitleView?.text = titleText
//        mMessageView?.text = messageText
//
//        if (mTitleView?.text.toString().trim().isEmpty()) {
//            mTitleView?.visibility = View.GONE
//        }
//
//        if (mMessageView?.text.toString().trim().isEmpty()) {
//            mMessageView?.visibility = View.GONE
//        }
//
//        // 根据按钮文本设置按钮的可见性
//        when {
//            thirdButtonText != null -> {
//                // 显示三个按钮，每个按钮一行
//                tripleButtonContainer?.visibility = View.VISIBLE
//                doubleButtonContainer?.visibility = View.GONE
//                singleButtonContainer?.visibility = View.GONE
//
//                topButtonView?.apply {
//                    text = firstButtonText
//                    setOnClickListener { dialogListener?.onFirstButtonClicked() }
//                }
//                middleButtonView?.apply {
//                    text = secondButtonText
//                    setOnClickListener { dialogListener?.onSecondButtonClicked() }
//                }
//                bottomButtonView?.apply {
//                    text = thirdButtonText
//                    setOnClickListener { dialogListener?.onThirdButtonClicked() }
//                }
//            }
//            secondButtonText != null -> {
//                // 显示两个按钮，一行两列
//                tripleButtonContainer?.visibility = View.GONE
//                doubleButtonContainer?.visibility = View.VISIBLE
//                singleButtonContainer?.visibility = View.GONE
//
//                firstButtonView?.apply {
//                    text = firstButtonText
//                    setOnClickListener { dialogListener?.onFirstButtonClicked() }
//                }
//                secondButtonView?.apply {
//                    text = secondButtonText
//                    setOnClickListener { dialogListener?.onSecondButtonClicked() }
//                }
//            }
//            firstButtonText != null -> {
//                // 显示一个按钮，一行一列
//                tripleButtonContainer?.visibility = View.GONE
//                doubleButtonContainer?.visibility = View.GONE
//                singleButtonContainer?.visibility = View.VISIBLE
//
//                singleButtonView?.apply {
//                    text = firstButtonText
//                    setOnClickListener { dialogListener?.onFirstButtonClicked() }
//                }
//            }
//            else -> {
//                // 如果没有按钮文本，隐藏所有按钮容器
//                tripleButtonContainer?.visibility = View.GONE
//                doubleButtonContainer?.visibility = View.GONE
//                singleButtonContainer?.visibility = View.GONE
//            }
//        }
//
//        setupDialogComponents() // 设置弹窗组件样式
//    }
//
//    fun getContentView(): View? {
//        return mContentLayout
//    }
//
//    fun getRootView(): View? {
//        return mParentView
//    }
//
//    private fun setupDialogComponents() {
//        mStyleResId?.let { resId ->
//            mParentView?.let {
//                val backgroundHelper = OSBackgroundHelper(mParentView as View)
//                backgroundHelper.init(null, R.styleable.OSDialogAttr, R.styleable.OSDialogAttr_core_background_attr,
//                    R.styleable.OSDialogAttr, R.styleable.OSDialogAttr_core_border_attr, 0, resId)
//            }
//            val typedArray = context?.obtainStyledAttributes(null, R.styleable.OSDialogAttr, 0, resId)
//            try {
//                typedArray?.let { type ->
//
//                    val titleStyleRes = type.getResourceId(R.styleable.OSDialogAttr_dialog_text_title, -1)
//                    val messageStyleRes = type.getResourceId(R.styleable.OSDialogAttr_dialog_text_message, -1)
//                    val firstBtnStyleRes = type.getResourceId(R.styleable.OSDialogAttr_dialog_button_first, -1)
//                    val secondBtnStyleRes = type.getResourceId(R.styleable.OSDialogAttr_dialog_button_second, -1)
//                    val thirdBtnStyleRes = type.getResourceId(R.styleable.OSDialogAttr_dialog_button_third, -1)
//
//                    mTitleView?.let {
//                        val textHelper = OSTextHelper(it)
//                        textHelper.init(null, R.styleable.OSTextViewAttr, R.styleable.OSTextViewAttr_core_text_attr, 0, titleStyleRes)
//                    }
//
//                    mMessageView?.let {
//                        val textHelper = OSTextHelper(it)
//                        textHelper.init(null, R.styleable.OSDialogAttr, R.styleable.OSDialogAttr_dialog_text_message, 0, messageStyleRes)
//                    }
//
//                    singleButtonView?.updateStyleRes(firstBtnStyleRes)
//
//                    firstButtonView?.updateStyleRes(firstBtnStyleRes)
//
//                    secondButtonView?.updateStyleRes(secondBtnStyleRes)
//
//                    topButtonView?.updateStyleRes(firstBtnStyleRes)
//
//                    middleButtonView?.updateStyleRes(secondBtnStyleRes)
//
//                    bottomButtonView?.updateStyleRes(thirdBtnStyleRes)
//
//                }
//            } finally {
//                typedArray?.recycle()
//            }
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.let { window ->
//            window.setDimAmount(dimAmount ?: 0.5f) // 设置遮罩透明度，0为全透明，1为全黑
//            canceledOnTouchOutside?.let { dialog?.setCanceledOnTouchOutside(it) }
//            setDialogSize(window)
//            setPosition(window)
//            if(dialogAnimRes != -1){
//                dialogAnimRes?.let { window.setWindowAnimations(it) }
//            }else{
//                window.setWindowAnimations(R.style.dialog_anim_center)
//            }
//            // 设置内容为完全透明
//            mContentView?.alpha = 0f
//            // 获取窗口动画时长
//            val windowAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
//            // 延迟开始内容的渐变动画，直到窗口动画结束
//            mContentView?.postDelayed({
//                // 创建并启动内容的渐变动画
//                mContentView?.let { contentView ->
//                    ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f).apply {
//                        duration = 200 // 设置渐变动画的持续时间
//                        start()
//                    }
//                }
//            }, windowAnimationDuration)
//        }
//
//        dialogListener?.onDialogShown()
//        dialog?.window?.decorView?.setOnTouchListener { _, event ->
//            var outSideTouchIntercepted = false
//            if (event?.action == MotionEvent.ACTION_UP) {
//                val dialogBounds = Rect()
//                mParentView?.getHitRect(dialogBounds)
//                if (!dialogBounds.contains(event.x.toInt(), event.y.toInt())) {
//                    if(hidedOnTouchOutside == true){
//                        hide()
//                        outSideTouchIntercepted = true
//                    }
//                }
//            }
//            outSideTouchIntercepted
//        }
//    }
//
//    private fun setDialogSize(window: Window) {
//        val params = window.attributes
//        params.width = mWidth?.takeIf { it > 0 }?.toInt() ?: ViewGroup.LayoutParams.WRAP_CONTENT
//        params.height = mHeight?.takeIf { it > 0 }?.toInt() ?: ViewGroup.LayoutParams.WRAP_CONTENT
//        window.attributes = params
//    }
//
//    private fun setPosition(window: Window) {
//        val params = window.attributes
//        params.x = positionX!!
//        params.y = positionY!!
//        window.attributes = params
//    }
//
//    @SuppressLint("UseRequireInsteadOfGet")
//    fun show(manager: FragmentManager?){
//        val existingFragment = manager?.findFragmentByTag(osDialogTag) as? DialogFragment
//        if (existingFragment != null) {
//            dialog?.show()
//        } else {
//            this.show(manager!!, osDialogTag) // 使用 this 显示当前 DialogFragment 实例
//        }
//    }
//
//    fun show(context: Context){
//        showSystemOverlay(context) // WindowManager
//    }
//
//    fun hide(){
//        dialog?.hide()
//        hideSystemOverlay()
//    }
//
//    override fun dismiss() { // DialogFragment
//        dismissAllowingStateLoss()
//    }
//
//    fun dismiss(context: Context){ // WindowManager
//        dismissSystemOverlay(context)
//    }
//
//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        // 在这里处理对话框消失的事件
//        dialogListener?.onDialogDismissed()
//    }
//
//    // 显示为系统级别的覆盖视图
//    @Deprecated(
//        message = "This function is deprecated, use show(context: Context) instead",
//        replaceWith = ReplaceWith(
//            expression = "show(context)",
//            imports = ["space.syncore.commonui.widget.dialog.OSDialog.hide"]
//        )
//    )
//    fun showSystemOverlay(context: Context) {
//        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        // 检查mParentView是否已经被添加到WindowManager
//        if (mParentView?.windowToken == null) {
//            val params = WindowManager.LayoutParams().apply {
//                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY // 注意：需要SYSTEM_ALERT_WINDOW权限
//                format = PixelFormat.TRANSLUCENT
//                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE // 这样它就不会拦截触摸事件
//                width = mWidth.takeIf { it!! > 0 }?.toInt() ?: WindowManager.LayoutParams.WRAP_CONTENT
//                height = mHeight.takeIf { it!! > 0 }?.toInt() ?: WindowManager.LayoutParams.WRAP_CONTENT
//                gravity = Gravity.TOP or Gravity.START
//                x = positionX!!
//                y = positionY!!
//            }
//
//            // 初始化视图
//            val inflater = LayoutInflater.from(context)
//            mParentView = layoutResID?.let { inflater.inflate(it, null) }
//            // 初始化视图中的组件
//            initDialogView(inflater, mParentView)
//            // 将视图添加到 WindowManager
//            windowManager.addView(mParentView, params)
//        }else {
//            mParentView?.visibility = View.VISIBLE
//            val params = mParentView?.layoutParams as WindowManager.LayoutParams
//            windowManager.updateViewLayout(mParentView, params)
//        }
//        isAddedWindowManager = true
//    }
//
//    @Deprecated(
//        message = "This function is deprecated, use dismiss(context: Context) instead",
//        replaceWith = ReplaceWith(
//            expression = "hide()",
//            imports = ["space.syncore.commonui.widget.dialog.OSDialog.hide"]
//        )
//    )
//    fun hideSystemOverlay() {
//        // 检查mParentView是否已经被添加到WindowManager
//        if (isAddedWindowManager && mParentView?.windowToken != null) {
//            mParentView?.visibility = View.GONE
//        }
//    }
//
//    @Deprecated(
//        message = "This function is deprecated, use dismiss(context: Context) instead",
//        replaceWith = ReplaceWith(
//            expression = "dismiss(context)",
//            imports = ["space.syncore.commonui.widget.dialog.OSDialog.dismiss"]
//        )
//    )
//    fun dismissSystemOverlay(context: Context) {
//        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        // 检查视图是否已经添加到窗口管理器
//        if (mParentView?.windowToken != null) {
//            // 如果视图已经添加，则移除它
//            windowManager.removeView(mParentView)
//        }
//        mParentView = null // 清除引用，以便垃圾收集器可以回收视图
//        isAddedWindowManager = false
//    }
//
//    // 你可能需要创建一个接口来处理按钮点击事件
//    interface DialogListener {
//        fun onFirstButtonClicked(){}
//        fun onSecondButtonClicked(){}
//        fun onThirdButtonClicked(){}
//        fun onDialogShown(){}
//        fun onDialogDismissed(){}
//    }
//
//    private var dialogListener: DialogListener? = null
//
//    fun setDialogListener(listener: DialogListener) {
//        dialogListener = listener
//    }
//
//    class Builder(context: Context) {
//
//        private val dialog = OSDialog()
//
//        private var mContext = context
//
//        fun setStyle(styleResId: Int) = apply { dialog.mStyleResId = styleResId }
//
//        fun setTitle(title: String?) = apply { dialog.titleText = title }
//
//        fun setMessage(content: String?) = apply { dialog.messageText = content }
//
//        fun setContentLayout(layoutRedID: Int?) = apply { dialog.customResID = layoutRedID }
//
//        fun setFirstButtonText(text: String?) = apply { dialog.firstButtonText = text }
//
//        fun setSecondButtonText(text: String?) = apply { dialog.secondButtonText = text }
//
//        fun setThirdButtonText(text: String?) = apply { dialog.thirdButtonText = text }
//
//        fun setHidedOnTouchOutside() = apply { dialog.hidedOnTouchOutside = true }
//
//        fun setSize(width: Float, height: Float) = apply {
//            dialog.mWidth = width
//            dialog.mHeight = height
//        }
//
//        fun setPosition(centerX: Int, centerY: Int) = apply {
//            dialog.positionX = centerX
//            dialog.positionY = centerY
//        }
//
//
//        @SuppressLint("Recycle")
//        fun build(): OSDialog {
//
//            dialog.mStyleResId?.let { resId ->
//                val typedArray = mContext.obtainStyledAttributes(null, R.styleable.OSDialogAttr, 0, resId)
//                try {
//                    typedArray.let { type ->
//                        dialog.dimAmount = type.getFloat(R.styleable.OSDialogAttr_dimAmount, 0.5f)
//                        dialog.dialogComponent = DialogComponent.values()[type.getInt(R.styleable.OSDialogAttr_dialog_component, 0)]
//                        dialog.dialogType = DialogType.findByValue(type.getInt(R.styleable.OSDialogAttr_dialog_type, 0))!!
//                        dialog.layoutResID = when(dialog.dialogType){
//                            DialogType.titled -> R.layout.layout_dialog_titled
//                            DialogType.untitled -> R.layout.layout_dialog_untitled
//                        }
//                        dialog.dialogAnimRes = type.getResourceId(R.styleable.OSDialogAttr_dialog_anim_style, -1)
//                        dialog.canceledOnTouchOutside = type.getBoolean(R.styleable.OSDialogAttr_canceledOnTouchOutside, false)
//                        dialog.mWidth = dialog.mWidth ?: type.getDimension(R.styleable.OSDialogAttr_dialog_width, ViewGroup.LayoutParams.WRAP_CONTENT.toFloat())
//                        dialog.mHeight = dialog.mHeight ?: type.getDimension(R.styleable.OSDialogAttr_dialog_height, ViewGroup.LayoutParams.WRAP_CONTENT.toFloat())
//                    }
//                }finally {
//                    typedArray.recycle()
//                }
//            }
//
//            return dialog
//        }
//    }
//}