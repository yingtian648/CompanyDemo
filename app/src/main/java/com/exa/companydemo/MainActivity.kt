package com.exa.companydemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.*
import android.content.Intent.*
import android.content.res.Configuration
import android.os.*
import android.view.*
import android.widget.Toast
import com.exa.baselib.BaseConstants
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.*
import com.exa.companydemo.TestUtil.*
import com.exa.companydemo.common.AppInfoActivity
import com.exa.companydemo.databinding.ActivityMainBinding
import com.exa.companydemo.locationtest.LocationActivity
import com.exa.companydemo.service.DemoService
import com.exa.companydemo.toasttest.ToastTestActivity
import com.exa.companydemo.utils.NetworkManager
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
    private val networkManager: NetworkManager? = null
    private var lastDisplayId = Display.DEFAULT_DISPLAY
    private var isDisplayChange = false

    override fun setContentViewLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        val clickIds = intArrayOf(
            R.id.btnLocation, R.id.btnSystemUI, R.id.btnPlay,
            R.id.btnAppList, R.id.btnTestActivity, R.id.btnToast,
            R.id.btnNightMode, R.id.btnEngineMode, R.id.btnTest
        )
        for (clickId in clickIds) {
            findViewById<View>(clickId).setOnClickListener(this)
        }
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun initView() {
        lastDisplayId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.displayId ?: Display.DEFAULT_DISPLAY
        } else {
            Display.DEFAULT_DISPLAY
        }
        modeManager = getSystemService(UiModeManager::class.java)
        L.d("黑夜模式：" + TestUtil.getUiModeStr(modeManager))
        bind.toolbar.setSubTitle("返回 (wifi:" + NetworkManager.getInstance(this).getWifiIp() + ")")
        bind.toolbar.setNavigationOnClickListener { v -> finish() }
        bind.toolbar.subTitleTextView?.setOnClickListener { v -> finish() }
        bind.edit.setOnEditorActionListener { v, actionId, event ->
            Tools.hideKeyboard(bind.edit)
            false
        }
        doAfterInitView()
    }

    private fun doAfterInitView() {
        bind.elv.disable()
        //设置屏幕亮度
//        Tools.setScreenBrightness(this, 50)
//        checkPermission()
//        TestUtil.registerFullScreenListener(this);
//        TestUtil.registerBroadcast(this);
        // 沉浸式
        //ScreenUtils.setStatusBarInvasion(this)
        L.dd("55")
    }

    @SuppressLint(
        "RestrictedApi", "WrongConstant", "Range", "UnspecifiedImmutableFlag",
        "ClickableViewAccessibility"
    )
    @Throws(Exception::class)
    private fun test() {
        index++
        L.dd("$index 111")
//        startActivity(WifiActivity::class.java)
//        bind.imageView.setCurrentAngle(index*30);
//
//        TestUtil.testSensorData(this)
//        startActivity(MDialogActivity::class.java)
//        TestUtil.testDialog(this,"ssssss",-1)
//        BuildTestDialog.getInstance().addNoteView(this)
//        BaseConstants.getHandler().postDelayed({
//            TestDialog.showLayout(this)
//            window.navigationBarColor  = 0
//        }, 3000)
//        startService(Intent(this, DemoService::class.java))
        Tools.showKeyboard(bind.edit)
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
//        window.statusBarColor = getColor(R.color.gray)
//        window.navigationBarColor = getColor(R.color.gray)
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
                    ScreenUtils.hideStatusBars(this)
                } else {
                    ScreenUtils.showStatusBars(this)
                }
            }
            R.id.btnLocation -> startActivity(LocationActivity::class.java)
            R.id.btnPlay -> startActivity(VideoPlayerActivity::class.java)
            R.id.btnAppList -> startActivity(AppInfoActivity::class.java)
            R.id.btnTestActivity -> startActivity(TestActivity::class.java)
            R.id.btnToast -> startActivity(ToastTestActivity::class.java)
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
                L.e("执行测试异常：" + e.message)
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
        var msg = javaClass.simpleName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            msg += " displayId=" + display?.displayId
        }
        L.dd(msg)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onResume() {
        super.onResume()
        var msg = javaClass.simpleName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            msg += " displayId=" + display?.displayId + ", isDisplayChange=" + isDisplayChange
        }
        L.dd(msg)
        if (isDisplayChange) {
            isDisplayChange = false
        }
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
        var msg = javaClass.simpleName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            msg += " displayId=" + display?.displayId
        }
        L.dd(msg)

        val currDisplayId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.displayId ?: Display.DEFAULT_DISPLAY
        } else {
            Display.DEFAULT_DISPLAY
        }
        if (currDisplayId != lastDisplayId) {
            lastDisplayId = currDisplayId
            isDisplayChange = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        L.dd(javaClass.simpleName)
        if (isRegisterBroadCast) {
            unregisterReceiver(mReceiver)
        }
        App.exit()
    }
}