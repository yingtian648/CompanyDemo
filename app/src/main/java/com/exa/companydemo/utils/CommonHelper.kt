package com.exa.companydemo.utils


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.media.AudioManager
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.view.*
import com.exa.baselib.utils.L
import com.exa.companydemo.App
import com.gxa.car.scene.SceneInfo
import com.gxa.car.scene.SceneManager
import com.gxa.car.scene.WindowChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/*
Copyright (C) 2021 Galaxy Auto Technology
All Rights Reserved by Galaxy Auto Technology Co., Ltd and its affiliates.
You may not use, copy, distribute, modify, transmit in any form this file
except in compliance with Galaxy Auto Technology in writing by applicable law.
*/
class CommonHelper {
    companion object {
        const val SECONDARY_DISPLAYID = "secondary_displayid"
        /**
         * 吸顶屏的context.
         */
        var displayContext: Context? = null
        /**
         * 现代按键音音效.
         * @see .playSoundEffect
         */
        const val FX_KEYPRESS_MODERN = 10

        /**
         * 复古按键音音效.
         * @see .playSoundEffect
         */
        const val FX_KEYPRESS_RETRO = 11

        /**
         * 界面音效.
         * @see .playSoundEffect
         */
        const val FX_KEYPRESS_ENTRY = 12

        /**
         * 卡片伸展音效.
         * @see .playSoundEffect
         */
        const val FX_KEYPRESS_CARD = 13

        /**
         * 弹框音效.
         * @see .playSoundEffect
         */
        const val FX_KEYPRESS_TOAST = 14
        const val CAR_SPEED_20 = 20
        const val AUTO_PARKING_ONE = 1
        /**
         * 主控的屏幕id默认是0
         * @see .main_DisplayId
         */
        const val MAIN_DISPLAYID = 0
        private const val TAG = "CommonHelper"
        const val SPLIT_SCREEN_FUNL_SCREEN = "split_screen_full_screen"
        const val LAUNCEHR_SCENE = "launcher_scene"
        const val PKG_LAUNCHER = "com.gxatek.cockpit.launcher"
        const val PKG_SPEECHCLIENT = "com.iflytek.cutefly.speechclient.hmi"
        const val PKG_PROJECTION = "com.gxatek.cockpit.screen.projection"
        const val PKG_MAP = "com.iflytek.autofly.map"
        const val PKG_ADIGO_SOUND = "space.syncore.cockpit.soundeffect"
        const val PKG_SCHEDULE = "com.gxatek.cockpit.schedule"
        const val PKG_DVR = "com.gxatek.cockpit.dvr"
        const val PKG_SETTING_ACTIVITY ="com.gxatek.cockpit.carsetting.view.activity.SettingActivity"
        const val PKG_SETTING = "com.gxatek.cockpit.car.settings"
        const val PKG_CAR_SETTING = "com.gxatek.cockpit.car.settings"
        const val PKG_ISPACE = "com.gxatek.cockpit.ispace"
        const val PKG_ISPACE_CINEMAMODEDIALOG_ACTIVITY = "com.gxatek.cockpit.ispace.view.CinemaModeDialogActivity"
        const val PKG_ISPACE_ACTIVITY = "com.gxatek.cockpit.ispace.view.MainActivity"
        const val PKG_MAP_2 = "space.syncore.cockpit.map"
        const val PKG_CAR_2 = "com.gxatek.cockpit.launcher.CarLauncher"
        const val PKG_BTLACL = "com.gxatek.cockpit.btcall"
        const val PKG_DESAY_MONITOR = "com.desay.aroundviewmonitor"
        const val CN_INVO_AVM = "cn.invo.avm_a09"
        const val PKG_BTLACL_ACTIVITY = "com.gxatek.cockpit.btcallBtCallMainActivity"
        const val PKG_GALLERY = "com.gxatek.cockpit.gallery"
        const val COM_ANDROID_PERMISSSION = "com.android.permissioncontroller"
        const val PKG_MEDIAX = "com.iflytek.autofly.mediax"
        const val PKG_SWITCH = "com.gxatek.cockpit.screen.projection"
        const val PKG_DIAGNOSTIC ="com.gxatek.cockpit.diagnostic"
        const val PKG_GACI_IVI_IDS = "com.gaci.ivi.ids"
        const val PKG_AUTHORIZE = "com.gxa.authorize"
        const val PKG_SYSTEMLOG = "com.gxa.car.systemlog"
        const val PKG_ANDROID_CAR_STTINGS="com.android.car.settings"
        const val PKG_ADIGOSTORE = "com.gxa.service.adigostore"
        const val PKG_ADIGOSTORE_ACTIVITY ="com.gxa.service.adigostore.ui.activity.product.ProductDetailsActivity"
        const val PKG_ADIGOSTORE_URI ="os://space.syncore.cockpit.adigostore/spu/detail?featId="
        const val PKG_LAUNCHER_SWITCH_LAUNCHER_SCENE="com.gxatek.cockpit.launcher.SWITCH_LAUNCHER_SCENE"
        const val PKG_UPGRADE = "com.desaysv.ivi.vds.upgrade"
        const val CINEMA_MODE_STATE = "ispace.cinema.mode.state"
        const val CONST_VIDEO_STATUS = "CONST_VIDEO_STATUS"
        const val CINEMA_MODE_STATE_VALUE = "source.cinema.mode.state"
        const val PKG_HVAC = "com.gxa.cockpit.hvac"
        const val PKG_ENGMODE = "com.android.engmode"
        const val PKG_YOUKU = "com.youku.car"
        var FULL_SCREEN_APP_PKG = listOf(
                "com.gxatek.cockpit.car.settings",
                "com.gxatek.cockpit.ispace",
                "com.gxa.car.systemlog",    //日志
                "com.gxatek.cockpit.carsetting",    //我的车
                "com.android.engmode",
                "space.syncore.cockpit.map",
                "com.iflytek.inputmethod",      //输入法
                "com.gxatek.cockpit.diagnostic", //工程模式
                "com.gxatek.cockpit.screen.projection", //switch 投屏
//                "com.iflytek.cutefly.speechclient.hmi",  //语音助理
                "com.holomatic.holopilotparking",       //泊车
                "space.syncore.cockpit.soundeffect",  //AdigoSound
                "com.android.permissioncontroller", //应用授权
                "com.gxa.authorize" //授权弹窗
        )
        var LOCAL_PKG = listOf(
                "com.gxatek.cockpit.launcher",  //桌面
                "com.gxa.car.systemlog",    //日志
                "com.gxatek.cockpit.car.settings",  //我的车
                "com.gxatek.cockpit.carsetting",    //我的车
                "com.gxatek.cockpit.weather",  //天气
                "com.iflytek.autofly.mediax",  //听服务
                "com.desaysv.ivi.vds.upgrade",  //OTA
                "com.gxatek.cockpit.upgrade",   //OTA
                "com.intellisense.gac",  //智能感知
                "com.gxa.service.adigostore",  //AdigoStore
                "com.gxatek.cockpit.dvr",  //行车记录仪
                "com.gxatek.cockpit.btcall",  //蓝牙电话
                "com.gxa.cockpit.btcall",   //蓝牙电话
                "com.gxa.service.ebcall",   //服务电话
                "com.android.engmode",  //工程模式
                "com.gxatek.cockpit.diagnostic", //工程模式
                "com.android.documentsui",  //文件
                "com.android.car.settings", //系统设置
                "com.gxatek.cockpit.scenecube", //广汽魔方
                "com.gxatek.cockpit.carservice", //车服务
                "com.gxatek.cockpit.schedule",      //日程助手
                "com.gxatek.cockpit.ispace", //I-Space
                "com.holomatic.holopilotparking", //自动泊车
                "cn.gaei.appstore",     //应用商店
                "android.car.usb.handler",      //USB弹窗
                "com.iflytek.autofly.map",     //地图
                "space.syncore.cockpit.map",    //地图
                "com.iflytek.inputmethod",      //输入法
                "com.iflytek.cutefly.speechclient.hmi",  //语音助理
                "com.gxatek.cockpit.exploreaions",  //探索埃安
                "space.syncore.cockpit.soundeffect",  //AdigoSound
                "com.ximalaya.ting.android",//喜马拉雅
                 "com.netease.cloudmusic" ,//网易云
                "com.android.permissioncontroller", //应用授权
                "com.google.android.car.kitchensink" , //X66
                "com.gxa.usbupdatehmi", //X66工程模式
                "com.gaci.ivi.ids" , //车机助手
                "com.desaysv.mediadvp",//USB测试界面
                 "com.gxa.car.systemlog",//log导出界面
                "cn.gaei.batterychange", //换电
                "com.gxa.authorize",
                "com.gxatek.cockpit.speak",//车外喊话
                "com.gxatek.cockpit.gameCenter"//游戏
        )

        //应广研要求，下面6个三方app全屏显示，只需要显示X按钮
        var THIRD_FULL_PKG = listOf(
            "com.douyu.xl.douyucar", //斗鱼
            "com.huya.android.pad", //虎牙
            "io.dushu.car", //攀登读书
            "com.twentyfirstcbh.auto", //21财经
            "com.lizhi.smartlife.lzbk.car", //荔枝播客
            "com.cmgame.gamehalltv" //咪咕
        )
        //应要求，下面6个三方app全屏显示，才显示影院模式按钮
        var MOVE_MODE_PKG_LIST = listOf(
            "com.gxatek.cockpit.gallery", //图片与视频
            "com.qiyi.video.iv", //爱奇艺
            "com.bilibili.bilithings", //哔哩哔哩
            "com.youku.car", //优酷
            "com.mgtv.auto" //芒果TV
        )
        private const val PLATFORM_TYPE_A02 = 0x48
        private const val PLATFORM_TYPE_A02L = 0x64
        private const val PLATFORM_TYPE_A09= 0x52
        private const val PLATFORM_TYPE_A19 = 0x55
        private const val PLATFORM_TYPE_AH8 = 0x63

        fun logThread() {
            L.i("CommonHelper", "currentThread id: ${Thread.currentThread().id}, name: ${Thread.currentThread().name}")
        }

        fun postDelayed(runnable: Runnable, time: Long) {
            Handler(Looper.getMainLooper()).postDelayed(runnable, time)
        }

        @SuppressLint("ServiceCast")
        //界面音效.
        fun playSoundCardEffect() {
//            GlobalScope.launch {
//                val mAudioManager = MyApplication.instance.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//                 mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_ENTRY)
//                L.i("CommonHelper","playSoundCardEffect>>FX_KEYPRESS_ENTRY>>>${AudioManager.FX_KEYPRESS_ENTRY}")
//            }
        }

        suspend fun isActivityOnForeground(className: String): Boolean = withContext(Dispatchers.IO) {
            val am = App.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.getRunningTasks(1)
            if (list != null && list.size > 0) {
                val cpn = list[0].topActivity
//                L.i(TAG, "className:${cpn.className}")
                if (className == cpn!!.className) {
                    return@withContext true
                }
            }
            return@withContext false
        }

        suspend fun isLauncherOnTop(): Boolean {
            return isActivityOnForeground("com.gxatek.cockpit.launcher.LauncherActivity")
        }

        @JvmStatic
        fun motionEventActionToString(action: Int): String {
            // Given an action int, returns a string description
            when (action) {
                MotionEvent.ACTION_DOWN -> return "Down"
                MotionEvent.ACTION_MOVE -> return "Move"
                MotionEvent.ACTION_POINTER_DOWN -> return "Pointer Down"
                MotionEvent.ACTION_UP -> return "Up"
                MotionEvent.ACTION_POINTER_UP -> return "Pointer Up"
                MotionEvent.ACTION_OUTSIDE -> return "Outside"
                MotionEvent.ACTION_CANCEL -> return "Cancel"
            }
            return ""


        }

        /**
         * 校验密码
         * @param password 密码
         * @param requireContainLetterAndNumber 是否必须包含数字和字母
         * @return true 密码合法，false 密码不合法
         */
        fun checkApPassword(password: String?, requireContainLetterAndNumber: Boolean): Boolean {
            password ?: return false
            val checkContainLetterAndNumber = checkContainLetterAndNumber(password)
            if (checkContainLetterAndNumber) {
                return password.length >= getPasswordLengthThreshold(false)
            }
            if (requireContainLetterAndNumber) {
                return false
            }
            val isAllLetterOrNumber = checkAllLetterOrNumber(password)
            return password.length >= getPasswordLengthThreshold(isAllLetterOrNumber)
        }

        fun checkApPassword(str: String): Boolean {
            //必须包含数字和大小写字母，并且个数为8个及以上
            val REG_NUMBER_LETTER = Regex("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,}\$")

            return if (!TextUtils.isEmpty(str)) {
                (str).matches(REG_NUMBER_LETTER)
            } else false
        }

        /**
         * 检验密码是否包含字母和数字
         */
        private fun checkContainLetterAndNumber(param: String): Boolean {
            //包含数字和字母
            val REG_CHAR_AND_NUMBER = Regex("^.*[a-zA-Z]+.*\\d+.*|^.*\\d+.*[a-zA-Z]+.*")
            return param.matches(REG_CHAR_AND_NUMBER)
        }

        /**
         * 检查是否是全数字或全字母
         */
        private fun checkAllLetterOrNumber(param: String): Boolean {
            //纯字母
            val REG_LETTER = Regex("^[a-zA-Z]+$")
            //纯数字
            val REG_NUMBER = Regex("^\\d+$")
            return param.matches(REG_LETTER) || param.matches(REG_NUMBER)
        }

        /**
         * 检查是否是同时包含字母和数字
         * 如果是纯数字或纯字母，长度超过12也可以设置
         */
        fun checkBothContainLetterAndNumber(param: String): Boolean {
            var isDigit = false
            var isLetter = false
            param.forEach {
                if (it.isDigit() && !isDigit) {
                    isDigit = true
                }
                if (it.isLetter() && !isLetter) {
                    isLetter = true
                }
                if (isDigit && isLetter) {
                    return true
                }
            }
            return false
        }

        /**
         * 获取密码最小长度
         * @param isAllLetterOrNumber 是否是纯字母或纯数字
         */
        private fun getPasswordLengthThreshold(isAllLetterOrNumber: Boolean): Int {
            //混合密码最小长度
            val MIX_RULE_MIN_LENGTH_THRESHOLD = 8
            //纯数字或者纯字母最小长度
            val SINGLE_RULE_MIN_LENGTH_THRESHOLD = 12

            return if (isAllLetterOrNumber) {
                SINGLE_RULE_MIN_LENGTH_THRESHOLD
            } else {
                MIX_RULE_MIN_LENGTH_THRESHOLD
            }
        }

        /**
         * intent转string
         */
        fun intentToString(intent: Intent?): String? {
            return if (intent == null) {
                null
            } else intent.toString() + " " + bundleToString(intent.extras)
        }

        private fun bundleToString(bundle: Bundle?): String {
            val out = StringBuilder("Bundle[")
            if (bundle == null) {
                out.append("null")
            } else {
                var first = true
                for (key in bundle.keySet()) {
                    if (!first) {
                        out.append(", ")
                    }
                    out.append(key).append('=')
                    val value = bundle[key]
                    if (value is IntArray) {
                        out.append(Arrays.toString(value))
                    } else if (value is ByteArray) {
                        out.append(Arrays.toString(value))
                    } else if (value is BooleanArray) {
                        out.append(Arrays.toString(value))
                    } else if (value is ShortArray) {
                        out.append(Arrays.toString(value))
                    } else if (value is LongArray) {
                        out.append(Arrays.toString(value))
                    } else if (value is FloatArray) {
                        out.append(Arrays.toString(value))
                    } else if (value is DoubleArray) {
                        out.append(Arrays.toString(value))
                    } else if (value is Array<*>) {
                        out.append(Arrays.toString(value as Array<String?>))
                    } else if (value is Bundle) {
                        out.append(bundleToString(value))
                    } else {
                        out.append(value)
                    }
                    first = false
                }
            }
            out.append("]")
            return out.toString()
        }

        /**
         * 根据包名判断是否是全屏应用
         */
        fun isFullScreenApp(pkg: String): Boolean {
            for (pkgName in FULL_SCREEN_APP_PKG) {
                if (pkgName == pkg) {
                    return true
                }
            }
            return false;
        }

        /**
         * 判断是否是三方应用
         */
        fun isThirdApp(pkg: String): Boolean {
            for (pkgName in LOCAL_PKG) {
                if (pkgName == pkg) {
                    return false
                }
            }
            return true
        }

        /**
         * 判断是否是三方全屏应用
         */
        fun isThirdFullApp(pkg: String): Boolean {
            for (pkgName in THIRD_FULL_PKG) {
                if (pkgName == pkg) {
                    return true
                }
            }
            return false
        }
        /**
         * 判断是否显示影院模式按钮
         */
        fun isShowMoveButton(pkg: String): Boolean {
            for (pkgName in MOVE_MODE_PKG_LIST) {
                if (pkgName == pkg) {
                    return true
                }
            }
            return false
        }

        /**
         * 判断应用是否在栈顶
         */
        fun isPackageOnTop(pkg: String): Boolean {
            val am = App.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.getRunningTasks(1)
            if (list != null && list.size > 0) {
                val topPkg = list[0].topActivity?.packageName
                if (pkg == topPkg) {
                    return true
                }
            }
            return false
        }
        var topPackageName: String? = null
        fun packageOnTop(pkg: String):Boolean {
            val sceneManager = SceneManager.getInstance(App.getContext())
            sceneManager.addWindowChangeListener(object : WindowChangeListener {
                override fun onWindowsChanged(sceneInfo: SceneInfo, type: Int) {
                }
                override fun onFocusChanged(sceneInfo: SceneInfo, sceneInfo1: SceneInfo) {

                        topPackageName = sceneInfo1.packageName
                    L.i(TAG, "topPackageName::$topPackageName")
                }

            })

            L.i(TAG, "onFocusChanged topPackageName::$topPackageName")
            if (topPackageName != null && topPackageName == pkg) {
                return true
            }
            return false
        }

        /**
         * 埃安车型的测试联调环境
         */
        const val X9E_DEBUG_URL = "https://pv-release.gacicv.com/"

        /**
         * 埃安车型的预发布环境
         */
        const val X9E_PRE_RELEASE_URL = "https://pre-public-access.gacicv.com/"

        /**
         * 埃安车型的生产环境
         */
        const val X9E_RELEASE_URL = "https://public-access.gacicv.com/"

        /**
         * 传祺车型的测试联调环境
         */
        const val GMC_DEBUG_URL = "https://pv-gmc-public-access.gacicv.com/"

        /**
         * 传祺车型的预发布环境
         */
        const val GMC_PRE_RELEASE_URL = "https://pre-gmc-public-access.gacicv.com/"

        /**
         * 传祺车型的生产环境
         */
        const val GMC_RELEASE_URL = "https://gmc-public-access.gacicv.com/"

        const val DEBUG_URL_SPUID= "SP51204713"
        const val PRE_RELEASE_SPUID = "SP61638686"


        /**
         *判断当前车型值(09需求)
         *0:传统车
         *1: EV车(油电混合车 不给进入舒缓空间)
         *2: HEV车
         *3: PHEV
         */
        const val VEHICLE_TRADITION = 0
        const val VEHICLE_EV = 1
        const val VEHICLE_HEV = 2
        const val VEHICLE_PHEV = 3


        /**
         * 初始化渠道管理类
         */

        enum class Vehicleplatform {
            A02, A02L, A09, A19, AH8
        }

    }
}