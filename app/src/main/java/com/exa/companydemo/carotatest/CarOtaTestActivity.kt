package com.exa.companydemo.carotatest

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.exa.baselib.base.BaseBindActivity
import com.exa.companydemo.R
import com.exa.companydemo.databinding.ActivityCarOtaTestBinding
import com.google.gson.Gson
import com.gxa.ota.sdk.*
import java.io.File


class CarOtaTestActivity : BaseBindActivity<ActivityCarOtaTestBinding>(), IOtaManagerListener,
    OtaDownloadManager.Listener, IOtaProxyListener {
    private val TAG = "CarOtaTest"
    private var downloadId = 0
    private lateinit var downloadInfoPacket: DownloadInfoPacket
    private var mToast: Toast? = null
    private var proxy: OtaProxy? = null
    private lateinit var btStart: Button
    private lateinit var tvResult: TextView

    override fun setContentViewLayoutId(): Int = R.layout.activity_car_ota_test

    override fun initView() {
        tvResult = bind.tvResult
        btStart = bind.btStartDownload
        initDownloadInfoPacket();
    }

    private fun initDownloadInfoPacket() {
        downloadInfoPacket = DownloadInfoPacket()

        downloadInfoPacket.fileName = "/fotadata/CarOTA_test.mp3"
        downloadInfoPacket.hash = "hash"
        downloadInfoPacket.url =
            "http://online2.tingclass.net/lesson/shi0529/0008/8694/as_it_is_20160523d.mp3"
        downloadInfoPacket.length = 2657472
        downloadInfoPacket.length = 1328736
        downloadInfoPacket.hash = "0778ebe37fd50de20832e0ee44f3f8ad9953c92baed78cb467faf227e68275ed"
    }


    override fun initData() {

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.bt_connect -> {
                OtaManager.getInstance(applicationContext).registerListener(this)
                OtaManager.getInstance(applicationContext).connect()
                OtaDownloadManager.getInstance(applicationContext)
                Log.d(TAG, "connect bindService")
                tvResult.setText("connecting...")
                return
            }
            R.id.bt_getotadir -> {
                val otaProxy: OtaProxy? = this.proxy
                if (otaProxy == null) {
                    Log.e(
                        TAG,
                        "OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，getOtaDir失败: proxy == null"
                    )
                    tvResult.setText("OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，getOtaDir失败")
                    return
                }
                tvResult.setText(otaProxy.otaDir)
                return
            }
            R.id.bt_packagedir -> {
                val packageName = OtaDownloadManager.getInstance(applicationContext).packageName
                Log.d(TAG, "getPackageName packageName : $packageName")
                tvResult.setText(packageName)
                btStart.setClickable(true)
                return
            }
            R.id.bt_init_download -> {
                this.downloadId = OtaDownloadManager.getInstance(applicationContext)
                    .initDownload(downloadInfoPacket, this)
                Log.d(TAG, "initDownload downloadId : " + this.downloadId)
                downloadInfoPacket.setId(this.downloadId)
                tvResult.setText("initDownload downloadId : " + this.downloadId)
                return
            }
            R.id.bt_start_download -> {
                val downloadPath: String = downloadInfoPacket.getFileName()
                if (!TextUtils.isEmpty(downloadPath)) {
                    val file = File(downloadPath)
                    if (file.exists()) {
                        Log.d(TAG, "bt_start deleteFlag = " + file.delete())
                    }
                }
                val start = OtaDownloadManager.getInstance(applicationContext)
                    .start(downloadInfoPacket.getId())
                Log.d(TAG, "start download : $start")
                tvResult.setText("start download : $start")
                if (start) {
                    btStart.setText("下载中")
                    btStart.setClickable(false)
                    return
                }
                return
            }
            R.id.bt_pause_download -> {
                val pause = OtaDownloadManager.getInstance(applicationContext)
                    .pause(downloadInfoPacket.getId())
                Log.d(TAG, "pause download : $pause")
                tvResult.setText("pause download : $pause")
                btStart.setText("开始下载")
                btStart.setClickable(true)
                return
            }
            R.id.bt_policydir -> {
                val policyDir = OtaDownloadManager.getInstance(applicationContext).policyDir
                Log.d(TAG, "getPolicyDir policyDir : $policyDir")
                tvResult.setText(policyDir)
                btStart.setClickable(true)
                return
            }
            R.id.bt_proxy_connect -> {
                val otaProxy2: OtaProxy? = this.proxy
                if (otaProxy2 == null) {
                    Log.e(
                        TAG,
                        "OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，getOtaConnectionState失败: proxy == null"
                    )
                    tvResult.setText("OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，getOtaConnectionState失败")
                    return
                }
                val otaConnectionState = otaProxy2.otaConnectionState
                Log.d(TAG, "OtaProxy getOtaConnectionState：" + this.proxy?.getOtaConnectionState())
                tvResult.setText("OtaProxy getOtaConnectionState：$otaConnectionState")
                return
            }
            R.id.bt_proxy_register -> {
                val otaProxy3: OtaProxy? = this.proxy
                if (otaProxy3 == null) {
                    Log.e(
                        TAG,
                        "OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，proxyRegister失败: proxy == null"
                    )
                    tvResult.text =
                        "OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，proxyRegister失败"
                    return
                }
                otaProxy3.registerOtaProxyListener(this)
                Log.d(TAG, "OtaProxy registerOtaProxyListener")
                tvResult.text = "OtaProxy registerOtaProxyListener success"
                return
            }
            R.id.bt_proxy_unregister -> {
                if (this.proxy == null) {
                    Log.e(
                        TAG,
                        "OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，proxyUnregister失败: proxy == null"
                    )
                    tvResult.setText("OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，proxyUnregister失败")
                    return
                }
                Log.d(TAG, "OtaProxy unregisterOtaProxyListener")
                tvResult.setText("OtaProxy unregisterOtaProxyListener success")
                return
            }
            R.id.bt_register -> {
                OtaManager.getInstance(applicationContext).registerListener(this)
                Log.d(TAG, "registerListener")
                tvResult.setText("OtaManger registerLister success")
                return
            }
            R.id.bt_release -> {
                val release0 = OtaDownloadManager.getInstance(applicationContext).release(0)
                val release1 = OtaDownloadManager.getInstance(applicationContext).release(1)
                val release2 = OtaDownloadManager.getInstance(applicationContext).release(2)
                Log.d(
                    TAG,
                    "release download : release0 = $release0,release1 = $release1,release2 = $release2"
                )
                tvResult.setText("release download : release0 = $release0,release1 = $release1,release2 = $release2")
                btStart.setText("开始下载")
                btStart.setClickable(true)
                return
            }
            R.id.bt_requestota -> {
                if (this.proxy == null) {
                    Log.e(
                        TAG,
                        "OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，requestOta失败: proxy == null"
                    )
                    tvResult.setText("OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，requestOta失败")
                    return
                }
                val hashMap: HashMap<String, Int> = HashMap()
                hashMap["eOnline"] = 1
                val requestOtatring: Boolean =
                    this.proxy?.requestOtatring(2304, Gson().toJson(hashMap)) ?: false
                Log.d(TAG, "OtaProxy requestOta ：$requestOtatring")
                tvResult.setText("OtaProxy requestOta ：$requestOtatring")
                return
            }
            R.id.bt_responseota -> {
                if (this.proxy == null) {
                    Log.e(
                        TAG,
                        "OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，responseOta失败: proxy == null"
                    )
                    tvResult.setText("OtaProxy代理类对象为空，OtaManger调用connect绑定服务时失败没回调connect，responseOta失败")
                    return
                }
                val hashMap2: HashMap<String, Int> = HashMap()
                hashMap2["state"] = 1
                val responeOtaString: Boolean =
                    this.proxy?.responeOtaString(2304, Gson().toJson(hashMap2)) ?: false
                Log.d(TAG, "OtaProxy responseOta  $responeOtaString")
                tvResult.setText("OtaProxy responseOta  $responeOtaString")
                return
            }
            R.id.bt_unregister -> {
                OtaManager.getInstance(applicationContext).unregisterListener(this)
                Log.d(TAG, "unregisterListener")
                tvResult.setText("OtaManger unRegisterLister success")
                return
            }
            else -> return
        }
    }

    private fun showToast(str: String) {
        val toast: Toast? = this.mToast
        if (toast == null) {
            this.mToast = Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT)
        } else {
            toast.setText(str)
            this.mToast?.duration = Toast.LENGTH_SHORT
        }
        this.mToast?.show()
    }

    override fun connect(otaProxy: OtaProxy?) {
        Log.d(TAG, "connect OtaManger OtaService connect")
        tvResult.text = "OtaManger bind OtaService success!"
        if (otaProxy == null) {
            showToast("绑定服务失败：proxy == null")
            Log.e(TAG, "绑定服务失败：proxy == null")
        } else if (otaProxy.otaConnectionState == -1) {
            showToast("绑定服务连接失败：proxy.getOtaConnectionState() == " + otaProxy.otaConnectionState)
            Log.e(TAG, "绑定服务连接失败：proxy.getOtaConnectionState() == " + otaProxy.otaConnectionState)
        } else {
            this.proxy = otaProxy
        }
    }

    override fun disconnect() {
        this.proxy = null
        Log.d(TAG, "disconnect OtaManger OtaService disconnect")
    }

    override fun onOtaRequestString(code: Int, msg: String) {
        Log.d(TAG, "onOtaRequestString code：$code msg:$msg")
    }

    override fun onOtaResponeString(code: Int, msg: String) {
        Log.d(TAG, "onOtaResponseString code：$code msg:$msg")
    }

    override fun onOtaConnectionStateChanged(i: Int) {
        Log.d(TAG, "onOtaConnectionStateChanged：$i")
    }

    override fun onFailure(downloadInfoPacket2: DownloadInfoPacket) {
        Log.d(TAG, "download onFailure:" + downloadInfoPacket2.url)
        resetDownLoadDesc()
    }

    override fun onSuccess(downloadInfoPacket2: DownloadInfoPacket) {
        Log.d(TAG, "download onSuccess:" + downloadInfoPacket2.url)
        resetDownLoadDesc()
    }

    override fun onProcess(downloadProPacket: DownloadProPacket) {
        Log.d(TAG, "download onProcess:" + downloadProPacket.progress)
    }

    override fun onPaused(downloadProPacket: DownloadProPacket) {
        Log.d(TAG, "download onPaused:" + downloadProPacket.url)
        resetDownLoadDesc()
    }

    private fun resetDownLoadDesc() {
        runOnUiThread {
            btStart.text = "开始下载"
            btStart.isClickable = true
        }
    }
}