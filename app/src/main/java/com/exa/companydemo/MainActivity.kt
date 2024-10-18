package com.exa.companydemo

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.UiModeManager
import android.content.*
import android.content.Intent.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.Button
import android.window.SplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.exa.baselib.BaseConstants
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.base.adapter.BaseRecyclerAdapter
import com.exa.baselib.base.adapter.OnClickItemListener
import com.exa.baselib.utils.*
import com.exa.baselib.utils.Tools
import com.exa.companydemo.TestDialog.showDialogFragment
import com.exa.companydemo.TestUtil.*
import com.exa.companydemo.common.AppInfoActivity
import com.exa.companydemo.common.VideoPlayerActivity
import com.exa.companydemo.common.WebActivity
import com.exa.companydemo.databinding.ActivityMainBinding
import com.exa.companydemo.locationtest.LocationActivity
import com.exa.companydemo.toasttest.ToastTestActivity
import com.exa.companydemo.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @Author lsh
 * @Date 2023/10/23 15:35
 * @Description
 */
class MainActivity : BaseBindActivity<ActivityMainBinding>(), OnClickItemListener {
    private var isFullScreen = false

    private var modeManager: UiModeManager? = null
    private var index = 0
    private var mObjAnim: ObjectAnimator? = null
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var powerUtil: PowerUtil
    private lateinit var bitmap: Bitmap
    private var mShow = false
    private val handler = Handler(Looper.getMainLooper())

    override fun setContentViewLayoutId(): Int {
//        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
//                or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
//                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//                or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
//        )
//        window.attributes.fitInsetsTypes = WindowInsets.Type.systemBars()
        return R.layout.activity_main
    }

    override fun initData() {
//        SystemBarUtil.setInvasionStatusBar(this)
//        bind.rv.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        window.insetsController?.hide(WindowInsets.Type.systemBars())
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun initView() {
        bind.rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rv.adapter =
            object : BaseRecyclerAdapter<String>(this, R.layout.item_main, btnList, this) {
                override fun onViewHolder(view: View, data: String, position: Int) {
                    view.findViewById<Button>(R.id.btn).text = data
                }
            }
        modeManager = getSystemService(UiModeManager::class.java)
        L.d("黑夜模式：" + getUiModeStr(modeManager))

        val title = (getString(R.string.back) + " (wifi:" + NetworkManager.getInstance(this)
            .getWifiIp() + ":" + SystemProperties.get("service.adb.tcp.port") + ")")
        bind.toolbar.setSubTitle(title)
        bind.toolbar.setNavigationOnClickListener { finish() }
        bind.edit.setOnEditorActionListener { _, _, _ ->
            Tools.hideKeyboard(bind.edit)
            false
        }
        doAfterInitView()
        bind.surfaceView.visibility = View.GONE
        bind.surfaceView.setZOrderOnTop(false)
//        hardKeyTest(this)

//        test()
    }

    private fun doAfterInitView() {
        //设置屏幕亮度
//        Tools.setScreenBrightness(this, 50)
//        checkPermission()
//        test()
        // 加载要显示的图片资源
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.win_bg)
//        FordTest.get().test()

//        SystemBarUtil.hideStatusBars(this)
    }

    @SuppressLint(
        "RestrictedApi",
        "WrongConstant",
        "Range",
        "UnspecifiedImmutableFlag",
        "ClickableViewAccessibility", "SetTextI18n"
    )
    @Throws(Exception::class)
    private fun test() {
        App.index++
        val bool = App.index % 2 == 0
        L.dd("${App.index} start------------")

        showDialogFragment(this)
//        startActivity(TestActivity::class.java)

//        moveTaskToBack(true)
//        startService(Intent(this, MDialogService::class.java))
//        FordTest.get().test()
//        if (bool) {
//            TestDialog.getInstance().releaseTimer()
//        } else {
//            TestDialog.getInstance().startPublishTimer()
//        }
//            TestDialog.showMyDialog(this,"12121",2521)
//        showToast(this)

//        TestDialog.showMyDialog(this,"1212",0)
//        TestDialog.showDialogFragment(this)
//        showDialogFragment(this)

//        val packageName = "com.zlingsmart.demo.mtestapp"
//        com.exa.companydemo.utils.Tools.uninstall(this, packageName)
//
//        val deleteObserver = PackageDeleteObserver()
//        val unInstallMethod = packageManager.javaClass.getMethod("deletePackage", String.javaClass, deleteObserver.javaClass, Int.javaClass);
//        unInstallMethod.invoke(packageManager, arrayOf(packageName, deleteObserver, 0))


//        val intent = Intent(this, javaClass)
//        val sender = PendingIntent.getActivity(this, 0, intent, 0)
//        packageManager.packageInstaller.uninstall(packageName,sender)
//            VersionedPackage(
//                packageName,
//                PackageManager.VERSION_CODE_HIGHEST
//            ), sender
//        )

//        mShow = true
//        val isAlive = mInterceptor.asBinder().isBinderAlive
//        L.dd(isAlive)

//        mStartupInterceptor.unregisterHomeKeyInterceptor(mInterceptor)


//        DialogFragmentTest().show(supportFragmentManager, javaClass.name)
//        startShowAnim(bind.image)
//        doSurfaceViewAnimation(this, bind.frame)

//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        L.dd("${App.index} end------------")
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
            "com.exa.companyclient", "com.exa.companyclient.service.MService"
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
            if (action.isNullOrEmpty()) return
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

    @SuppressLint("SimpleDateFormat")
    private fun setText(msg: String) {
        val format = SimpleDateFormat(DateUtil.PATTERN_FULL_MS)
        val date = format.format(Date())
        runOnUiThread {
            val n = "$date $msg ${bind.msgT.text}"
            bind.msgT.text = n
        }
    }

    override fun onNewIntent(intent: Intent) {
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
        L.dd(javaClass.simpleName)
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
            if (keyCode == KeyEvent.KEYCODE_HOME) {
                L.d("Do nothing with KEYCODE_HOME")
//                return true
                return true
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
        App.exit()
    }

    private val btnList = mutableListOf(
        "SystemUI测试",
        "工程模式",
        "原生设置",
        "白天黑夜",
        "Location测试",
        "Toast测试",
        "App列表",
        "视频播放",
        "WebActivity",
        "测试按钮"
    )

    override fun onClickItem(position: Int) {
        L.d("点击按钮:" + btnList[position])
        setText(L.msg)
        when (position) {
            0 -> {
                isFullScreen = !isFullScreen
                if (isFullScreen) {
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    SystemBarUtil.hideStatusBars(this)
//                    SystemBarUtil.hideStatusBar(this)
//                    SystemBarUtil.setInvasionStatusBar(this)
                } else {
//                    SystemBarUtil.setInvasionNone(window)
                    SystemBarUtil.showStatusBars(window)
                }
            }

            1 -> {
                val apps = resources.getStringArray(com.exa.baselib.R.array.engine_mode_pkgs)
                L.dd(Arrays.toString(apps))
                for (item in apps) {
                    if (Utils.openApp(this, item)) {
                        return
                    }
                }
            }

            2 -> {
                val apps = resources.getStringArray(com.exa.baselib.R.array.setting_pkgs)
                for (item in apps) {
                    if (Utils.openApp(this, item)) {
                        return
                    }
                }
            }

            3 -> {
                if (modeManager!!.nightMode == UiModeManager.MODE_NIGHT_YES) {
                    modeManager!!.nightMode = UiModeManager.MODE_NIGHT_NO
                } else {
                    modeManager!!.nightMode = UiModeManager.MODE_NIGHT_YES
                }
                BaseConstants.getHandler().postDelayed({
                    L.w("白天黑夜模式:" + TestUtil.getUiModeStr(modeManager))
                }, 2000)
            }

            4 -> startActivity(LocationActivity::class.java)
            5 -> startActivity(ToastTestActivity::class.java)
            6 -> startActivity(AppInfoActivity::class.java)
            7 -> startActivity(VideoPlayerActivity::class.java)
            8 -> startActivity(WebActivity::class.java)
            9 -> try {
                test()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(L.TAG, "执行测试异常：" + e.message, e)
            }

            else -> {}
        }
    }

    override fun onLongClickItem(position: Int) = Unit
}
