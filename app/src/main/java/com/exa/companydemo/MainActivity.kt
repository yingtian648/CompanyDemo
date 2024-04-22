package com.exa.companydemo

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.UiModeManager
import android.content.*
import android.content.Intent.*
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.hardware.radio.RadioManager
import android.net.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.CarToast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.exa.baselib.BaseConstants
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.*
import com.exa.baselib.utils.Tools
import com.exa.companydemo.TestUtil.*
import com.exa.companydemo.accessibility.AccessibilityHelper
import com.exa.companydemo.common.AppInfoActivity
import com.exa.companydemo.databinding.ActivityMainBinding
import com.exa.companydemo.locationtest.LocationActivity
import com.exa.companydemo.radio.TunerManager
import com.exa.companydemo.socket.impl.AbstractClient
import com.exa.companydemo.socket.impl.WifiSocketClientUtil
import com.exa.companydemo.test.BuildTestDialog
import com.exa.companydemo.toasttest.ToastTestActivity
import com.exa.companydemo.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author lsh
 * @Date 2023/10/23 15:35
 * @Description
 */
class MainActivity : BaseBindActivity<ActivityMainBinding>(), View.OnClickListener {
    private var isFullScreen = false

    private var modeManager: UiModeManager? = null
    private var index = 0
    private var mObjAnim: ObjectAnimator? = null

    override fun setContentViewLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        val clickIds = intArrayOf(
            R.id.btnLocation, R.id.btnSystemUI, R.id.btnPlay,
            R.id.btnAppList, R.id.btnTestActivity, R.id.btnToast,
            R.id.btnNightMode, R.id.btnEngineMode, R.id.btnTest,
            R.id.btnTcpip
        )
        for (clickId in clickIds) {
            findViewById<View>(clickId).setOnClickListener(this)
        }
        bind.btnEngineMode.setOnLongClickListener {
            val apps = resources.getStringArray(com.exa.baselib.R.array.setting_pkgs)
            for (item in apps) {
                if (Utils.openApp(this, item)) {
                    break
                }
            }
            return@setOnLongClickListener false
        }
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun initView() {
        modeManager = getSystemService(UiModeManager::class.java)
        L.d("黑夜模式：" + getUiModeStr(modeManager))

        val title = (getString(R.string.back) + " (wifi:"
                + NetworkManager.getInstance(this).getWifiIp()
                + ":" + SystemProperties.get("service.adb.tcp.port") + ")")
        bind.toolbar.setSubTitle(title)
        bind.toolbar.setNavigationOnClickListener { finish() }
        bind.edit.setOnEditorActionListener { _, _, _ ->
            Tools.hideKeyboard(bind.edit)
            false
        }
        doAfterInitView()
        bind.edit.visibility
    }

    private fun doAfterInitView() {
//        bind.elv.disable()
        //设置屏幕亮度
//        Tools.setScreenBrightness(this, 50)
//        checkPermission()
//        TestUtil.registerFullScreenListener(this);
//        TestUtil.registerBroadcast(this);
        // 沉浸式
        SystemBarUtil.setStatusBarInvasion(this)
        L.dd("66 " + resources.getDimensionPixelSize(R.dimen.toast_max_width))
    }

    @SuppressLint(
        "RestrictedApi", "WrongConstant", "Range", "UnspecifiedImmutableFlag",
        "ClickableViewAccessibility"
    )
    @Throws(Exception::class)
    private fun test() {
        App.index++
        L.dd("${App.index}")

        L.dd("66 " + resources.getDimensionPixelSize(R.dimen.toast_max_width))

//        TestDialog.showDialog(this)

        SystemBarUtil.isSystemUiHide(this)

//        val fm = TunerTestFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fl, fm)
//            .commit()
//        startActivity(WifiActivity::class.java)
//        bind.imageView.setCurrentAngle(index*30);
//        startActivity(Intent(this,WebActivity::class.java))
//        TestUtil.testSensorData(this)
//        startActivity(MDialogActivity::class.java)
//        TestUtil.testDialog(this,"ssssss",-1)
//        BuildTestDialog.getInstance().addNoteView(this)
//        BaseConstants.getHandler().postDelayed({
//            TestDialog.showLayout(this)
//            window.navigationBarColor  = 0
//        }, 3000)
//        Tools.showKeyboard(bind.edit)
//        TestDialog.showDialog(this)
//        TestDialog.showAlertDialog(this)
//        TestDialog.showMyDialog(this,"121212",-1)
//        TestDialog.showLayout(this)
//        TestUtil.testSensorData(mContext);
//        L.dd("isTelephonyNetEnable:" + networkManager.isTelephonyDataEnable());WifiActivity
//        networkManager.switchTelephonyDataEnable();
//        Toast.makeText(this, "测试Toast $index", Toast.LENGTH_SHORT).show()

//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        newConfig.apply {
            L.dd("language=${locale.language} country=${locale.country} uiMode=${uiMode}")
        }
    }

    @SuppressLint("WrongConstant")
    private fun bindClientService() {
        L.dd()
        val intent = Intent()
        intent.component = ComponentName(
            "com.exa.companyclient",
            "com.exa.companyclient.service.MService"
        )
        val isBind = bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                L.d("bindService onServiceConnected")
            }

            override fun onServiceDisconnected(name: ComponentName) {
                L.d("bindService onServiceDisconnected")
            }
        }, Context.BIND_AUTO_CREATE or 0x04000000)
        L.e("bind-result:$isBind")
    }

    private fun registerReceiver() {
        L.dd("registerTimeUpdateReceiver")
        val filter = IntentFilter()
        filter.addAction("com.gxatek.cockpit.datacenter.action.UPLOAD")
        registerReceiver(mTimeUpdateReceiver, filter)
        setText(DateUtil.getNowDateHM())
    }

    private fun registerTimeUpdateReceiver() {
        L.dd("registerTimeUpdateReceiver")
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        filter.addAction(Intent.ACTION_TIME_CHANGED)
        registerReceiver(mTimeUpdateReceiver, filter)
        setText(DateUtil.getNowDateHM())
    }

    private val mTimeUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            L.d("收到广播：$action")
            if (action == null || action.isEmpty()) return
            if (action == Intent.ACTION_TIME_TICK) {
                //系统每1分钟发送一次广播
                L.d(DateUtil.getNowDateHM())
                setText(DateUtil.getNowDateHM())
            } else if (action == Intent.ACTION_TIME_CHANGED) {
                //系统手动更改时间发送广播
                L.d(DateUtil.getNowDateHM())
                setText(DateUtil.getNowDateHM())
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        L.w("onWindowFocusChanged: hasFocus = $hasFocus")
    }

    private fun checkPermission() {
        val ps = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        //        requestPermissions(ps,1);
        PermissionUtil.requestPermission(this, { L.d("已授权") }, ps)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        L.d("点击按钮:" + v.id)
        setText(L.msg)
        when (v.id) {
            R.id.btnSystemUI -> {
                isFullScreen = !isFullScreen
                if (isFullScreen) {
                    SystemBarUtil.hideStatusBars(this)
//                    SystemBarUtil.hideStatusBar(this)
                } else {
                    SystemBarUtil.showStatusBars(this)
                }
            }
            R.id.btnLocation -> startActivity(LocationActivity::class.java)
            R.id.btnPlay -> startActivity(VideoPlayerActivity::class.java)
            R.id.btnAppList -> startActivity(AppInfoActivity::class.java)
            R.id.btnTestActivity -> startActivity(TestActivity::class.java)
            R.id.btnToast -> startActivity(ToastTestActivity::class.java)
            R.id.btnTcpip -> {
                CmdUtil.exeCommand(CmdUtil.SET_TCP_IP_PORT_5555, true, null)
            }
            R.id.btnNightMode -> {
                if (modeManager!!.nightMode == UiModeManager.MODE_NIGHT_YES) {
                    modeManager!!.nightMode = UiModeManager.MODE_NIGHT_NO
                } else {
                    modeManager!!.nightMode = UiModeManager.MODE_NIGHT_YES
                }
                BaseConstants.getHandler().postDelayed({
                    L.w("白天黑夜模式:" + TestUtil.getUiModeStr(modeManager))
                }, 2000)
            }
            R.id.btnEngineMode -> {
                val apps = resources.getStringArray(com.exa.baselib.R.array.engine_mode_pkgs)
                for (item in apps) {
                    if (Utils.openApp(this, item)) {
                        return
                    }
                }
            }
            R.id.btnTest -> try {
                test()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(L.TAG, "执行测试异常：" + e.message, e)
            }
            else -> {}
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setText(msg: String) {
        val format = SimpleDateFormat(DateUtil.PATTERN_FULL_MS)
        val date = format.format(Date())
        runOnUiThread {
            val n = "$date $msg ${bind.msgT.text}"
            bind.msgT.text = n
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        var msg = javaClass.simpleName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            msg += " displayId=" + display?.displayId
        }
        L.dd(msg)
    }

    override fun onRestart() {
        super.onRestart()
        val msg = javaClass.simpleName
        L.dd(msg)
    }

    override fun onStart() {
        super.onStart()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onResume() {
        super.onResume()
        val msg = javaClass.simpleName
        L.dd(msg)
//        checkPermission();
//        bindScreenSaver();
//        finish();
    }

    override fun onPause() {
        super.onPause()
        L.dd()
    }

    override fun onStop() {
        super.onStop()
        val msg = javaClass.simpleName
        L.dd(msg)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event?.apply {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                L.d("Do nothing with KEYCODE_BACK")
//                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.dd(javaClass.simpleName)
        if (isRegisterBroadCast) {
            unregisterReceiver(mReceiver)
        }
//        App.exit()
    }
}