package com.exa.companydemo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import com.android.internal.policy.PhoneWindow
import com.exa.baselib.utils.L
import com.exa.baselib.utils.Tools
import com.exa.companydemo.R
import com.gxa.car.scene.SceneManager

/**
 * @Author lsh
 * @Date 2023/10/11 18:27
 * @Description
 */
class MDialogService : Service(), Window.Callback {
    private lateinit var mContext: Context
    private lateinit var mWindowManager: WindowManager
    private lateinit var dialogView: View
    private val mParams = WindowManager.LayoutParams()
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showTipView()
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Provision has been removed now, the default value of def_device_provisioned and
     * def_user_setup_complete changed to 'true' in SettingProvider to skip user setup.
     * If user does not do factory reset and happened to upgrade before Provision set
     * the value to true, it may cause DEVICE_PROVISIONED and
     * USER_SETUP_COMPLETE always be 0.
     * We check its value at here and ensure be set 1.
     */
    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        dialogView = LayoutInflater.from(mContext)
            .inflate(R.layout.dialog_layout_full, null, false)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancel_button)
        val sureBtn = dialogView.findViewById<Button>(R.id.sure_button)
        cancelBtn.setOnClickListener { v: View? ->
            mWindowManager.removeViewImmediate(dialogView)
        }
        sureBtn.setOnClickListener { v: View? ->
            mWindowManager.removeViewImmediate(dialogView)
        }

        mParams.type = 2512
        mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val flags = (WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        mParams.title = javaClass.name
        mParams.flags = flags
        mParams.format = PixelFormat.TRANSLUCENT
        mParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        mParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        mParams.gravity = Gravity.TOP or Gravity.LEFT
    }

    private fun showTipView() {
        mWindowManager.addView(dialogView, mParams)
    }

    private fun showTipView1() {
        L.dd("MDialogService")
        val context = applicationContext
        val mContext = ContextThemeWrapper(context, R.style.DialogTheme)
        val mWindowManager =
            context.getSystemService(WINDOW_SERVICE) as WindowManager
        val mWindow = PhoneWindow(mContext)
        mWindow.callback = this
        mWindow.setWindowManager(mWindowManager, null, null)
        mWindow.setGravity(Gravity.CENTER)
        val dialogView = LayoutInflater.from(mContext)
            .inflate(R.layout.dialog_layout, null, false)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancel_button)
        val sureBtn = dialogView.findViewById<Button>(R.id.sure_button)
        cancelBtn.setOnClickListener { v: View? ->

        }
        sureBtn.setOnClickListener { v: View? ->
            mWindowManager.removeViewImmediate(dialogView)
        }
        mWindow.setContentView(dialogView)
        val mDecor = mWindow.decorView
        val lp = mWindow.attributes
        lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        mDecor.setPadding(0, 0, 0, 0)
        mWindow.setLayout(Tools.getScreenW(context), Tools.getScreenH(context))
        mDecor.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //        mWindow.setStatusBarColor(Color.TRANSPARENT);
//        mWindow.setNavigationBarColor(Color.TRANSPARENT);
        mWindow.statusBarColor = Color.parseColor("#E3E3E3")
        mWindow.navigationBarColor = Color.parseColor("#E3E3E3")
        mWindowManager.addView(dialogView, lp)

//        mWindow.setStatusBarColor(Color.YELLOW);
//        mWindow.setNavigationBarColor(Color.YELLOW);
        L.dd("mShowing")
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return false
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
        return false
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun dispatchTrackballEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        return false
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return null
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return false
    }

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu): Boolean {
        return false
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        return false
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        return false
    }

    override fun onWindowAttributesChanged(attrs: WindowManager.LayoutParams?) {
    }

    override fun onContentChanged() {
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
    }

    override fun onAttachedToWindow() {
    }

    override fun onDetachedFromWindow() {
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
    }

    override fun onSearchRequested(): Boolean {
        return false
    }

    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        return false
    }

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?): ActionMode? {
        return null
    }

    override fun onWindowStartingActionMode(
        callback: ActionMode.Callback?,
        type: Int
    ): ActionMode? {
        return null
    }

    override fun onActionModeStarted(mode: ActionMode?) {
    }

    override fun onActionModeFinished(mode: ActionMode?) {
    }
}