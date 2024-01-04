package com.exa.companydemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.app.WallpaperManager
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.view.View
import android.view.WindowManager
import com.exa.baselib.BaseConstants
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.*
import com.exa.companydemo.TestUtil.isRegisterBroadCast
import com.exa.companydemo.TestUtil.mReceiver
import com.exa.companydemo.common.AppInfoActivity
import com.exa.companydemo.databinding.ActivityMainBinding
import com.exa.companydemo.locationtest.LocationActivity
import com.exa.companydemo.service.MDialogService
import com.exa.companydemo.toasttest.ToastTestActivity
import com.exa.companydemo.utils.NetworkManager
import com.exa.companydemo.utils.Tools.getPackageList
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
    private var networkManager: NetworkManager? = null
    private var index = 0

    override fun setContentViewLayoutId(): Int {
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return R.layout.activity_main
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun initView() {
        val clickIds = intArrayOf(
            R.id.btnLocation, R.id.btnSystemUI, R.id.btnPlay,
            R.id.btnAppList, R.id.btnTestActivity, R.id.btnToast,
            R.id.btnNightMode, R.id.btnEngineMode, R.id.btnTest
        )
        for (clickId in clickIds) {
            findViewById<View>(clickId).setOnClickListener(this)
        }
        networkManager = NetworkManager.getInstance(this)
        modeManager = getSystemService(UiModeManager::class.java)
        L.d("黑夜模式：" + TestUtil.getUiModeStr(modeManager))
        bind.toolbar.setNavigationOnClickListener { v -> finish() }
        bind.toolbar.setSubTitle("返回 (wifi:${networkManager?.getWifiIp()})")
        bind.toolbar.subTitleTextView?.setOnClickListener { v -> finish() }
        bind.edit.setOnEditorActionListener { v, actionId, event ->
            Tools.hideKeyboard(bind.edit)
            false
        }
    }

    override fun initData() {
        //设置屏幕亮度
//        Tools.setScreenBrightness(this, 50)
//        checkPermission()
//        TestUtil.registerFullScreenListener(this);
//        TestUtil.registerBroadcast(this);
        // 沉浸式
//        ScreenUtils.setStatusBarAndNavigationBarInvasion(this)
//        ScreenUtils.setStatusBarInvasion(this)

        L.dd("777")
    }

    @SuppressLint("RestrictedApi", "WrongConstant", "Range", "UnspecifiedImmutableFlag")
    @Throws(Exception::class)
    private fun test() {
        index++
        L.dd("$index")
//        startActivity(WifiActivity::class.java)
//        bind.imageView.setCurrentAngle(index*30);
//        startService(Intent(this, MDialogService::class.java))

//        TestUtil.testSensorData(this);
//        startActivity(MDialogActivity::class.java)
//        startService(new Intent(this, MDialogService.class));
//        TestUtil.testDialog(this,"ssssss",-1);
//        BuildTestDialog.getInstance().addNoteView(this)
//        BaseConstants.getHandler().postDelayed({
//            TestDialog.showLayout(this)
//            window.navigationBarColor  = 0
//        }, 3000)
//        TestDialog.showDialog(this)
//        TestDialog.showAlertDialog(this)
//        TestDialog.showMyDialog(this,"121212",-1)
//        TestDialog.showLayout(this)

//        getPackageList(packageManager,10)

//        TestUtil.testSensorData(mContext);
//        L.dd("isTelephonyNetEnable:" + networkManager.isTelephonyDataEnable());WifiActivity
//        networkManager.switchTelephonyDataEnable();
//        Toast.makeText(this, "测试Toast: " + index, Toast.LENGTH_SHORT).show();
    }


    private fun delayCheck() {
        BaseConstants.getHandler().postDelayed({
            val arr: Int = window.attributes.systemUiVisibility
            val sysUiVis: Int = window.decorView.systemUiVisibility
            L.dd("activity arr=$arr, sysUiVis=$sysUiVis")
            if (sysUiVis and View.SYSTEM_UI_FLAG_FULLSCREEN != 0) {
                L.dd("状态栏 已隐藏")
            }
            if (sysUiVis and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0) {
                L.dd("导航栏 已隐藏")
            }
        }, 3000)
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
        L.w("onWindowFocusChanged: hasFocus 1 = $hasFocus")
        if (hasFocus) {
            BaseConstants.getHandler().postDelayed({
//                ScreenUtils.hideStatusBars(this)
            }, 500)
        }
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
                delayCheck()
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
                    L.w(
                        "白天黑夜模式:" + TestUtil.getUiModeStr(
                            modeManager
                        )
                    )
                }, 2000)
            }
            R.id.btnEngineMode -> {
                val engines = resources.getStringArray(com.exa.baselib.R.array.engine_mode_pkgs)
                for (item in engines) {
                    if (Utils.openApp(this, item)) {
                        break
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
        L.dd(javaClass.simpleName)
    }

    override fun onRestart() {
        super.onRestart()
        L.dd(javaClass.simpleName)
    }

    override fun onResume() {
        super.onResume()
        L.dd(javaClass.simpleName)
//        checkPermission();
//        bindScreenSaver();
//        finish();
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        L.dd(javaClass.simpleName)
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