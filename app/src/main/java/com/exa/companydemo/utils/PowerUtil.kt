package com.exa.companydemo.utils

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.BatterySaverPolicyConfig
import android.os.Build
import android.os.PowerExemptionManager
import android.os.PowerManager
import android.os.SystemClock
import com.exa.baselib.utils.L
import java.io.InterruptedIOException
import java.time.Duration


/**
 * @author lsh
 * @date 2024/5/27 14:38
 * @description
 * DeviceIdleController在SystemServer中启动
 * ThermalManagerService 热量管理服务
 */
class PowerUtil(private val mContext: Context) {
    private val TAG = "PowerUtil"
    private val ACTION_DEV_IDLE_MODE_CHANGE = "android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED"
    private val powerManager = mContext.getSystemService(PowerManager::class.java)
    // 用于添加和移除省电模式白名单包名
    private val powerExemptionManager = mContext.getSystemService(PowerExemptionManager::class.java)
    private val mLock = Any()
    private var mIsPluggedIn = false

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            L.dd(intent.action)
            when (intent.action) {
                Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF -> {
                    // 亮屏/熄屏
                }

                Intent.ACTION_BATTERY_CHANGED -> {
                    // 充电状态改变
                    synchronized(mLock) {
                        // 是否接通电源
                        mIsPluggedIn = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
                    }
                    updateBatterySavingStats(intent)
                }

                PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED, ACTION_DEV_IDLE_MODE_CHANGE ->
                    updateBatterySavingStats(intent)
            }
        }
    }

    private fun updateBatterySavingStats(intent: Intent) {
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        // 默认100
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPercent = level * 100 / scale.toFloat()
        L.dd("mIsPluggedIn=$mIsPluggedIn batteryPercent=$batteryPercent")
    }

    init {
        registerReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener {
                L.d("onThermalStatusChanged: $it")
            }
        }

        L.d(
            "是否省电模式：${powerManager.isPowerSaveMode}" +
                    ", locationPowerSaveMode=${powerManager.locationPowerSaveMode}"
                    + ", isDeviceIdleMode=${powerManager.isDeviceIdleMode}"
                    + ", isInteractive=${powerManager.isInteractive}"
                    // PowerManager.SHUTDOWN_REASON_SHUTDOWN 1,SHUTDOWN_REASON_REBOOT 2
                    + ", getLastShutdownReason=${powerManager.getLastShutdownReason()}"
                    // 温度状态PowerManager.THERMAL_STATUS_NONE 0-6
                    + ", getCurrentThermalStatus=${powerManager.getCurrentThermalStatus()}"
        )
    }

    private fun registerReceiver() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
        filter.addAction(ACTION_DEV_IDLE_MODE_CHANGE)
        mContext.registerReceiver(mReceiver, filter)
    }

    /**
     * 休眠
     * 按电源键时会触发
     * KEYCODE_SLEEP 223
     * adb shell input keyevent 223
     */
    fun goToSleep(){
        powerManager.goToSleep(SystemClock.uptimeMillis())
    }

    /**
     * 唤醒
     * 按电源键时会触发
     * KEYCODE_WAKEUP 224
     * adb shell input keyevent 224
     */
    fun wakeUp(){
        powerManager.wakeUp(SystemClock.uptimeMillis())
    }

    /**
     * 判断应用是否在省电模式白名单中
     * @return true 省电模式下不会限制白名单应用功能
     */
    fun isIgnoringBatteryOptimizations(pkgName: String): Boolean {
        return powerManager.isIgnoringBatteryOptimizations(pkgName)
    }

    /**
     * 设置放电预测
     * 充电时设置此项会报错：Discharge prediction can't be set while the device is charging
     * @param minutes 预测使用时长
     */
    fun setBatteryDischargePrediction(minutes: Int) {
        L.dd()
        try {
            // 放电预测可以使用时长
            val duration = Duration.ofMillis(minutes.toLong() * 60 * 1000)
            powerManager.setBatteryDischargePrediction(duration, false)
        } catch (e: Exception) {
            L.de(e)
        }
    }

    /**
     * 设置省电模式策略
     */
    fun setAdaptivePowerSavePolicy() {
        L.dd()
        val policyConfig = BatterySaverPolicyConfig.Builder()
            .setEnableAdjustBrightness(true)
            // 亮度
            .setAdjustBrightnessFactor(0.5F)
            // 是否在省电状态下禁用“始终显示”
            .setDisableAod(true)
            // 省电模式下的定位模式——仅支持前台定位，会停止应用后台定位
            .setLocationMode(PowerManager.LOCATION_MODE_FOREGROUND_ONLY)
            // 是否禁用防火墙
            .setEnableFirewall(false)
            // 禁用动画
            .setDisableAnimation(true)
            // 是否告知系统和其它App已启动省电模式
            .setAdvertiseIsEnabled(true)
            // 设置延迟完全备份
            .setDeferFullBackup(true)
            // 设置延迟键值备份
            .setDeferKeyValueBackup(true)
            // 禁止震动
            .setDisableVibration(true)
            // 禁用启动增强
            .setDisableLaunchBoost(true)
            // 禁用可选传感器
            .setDisableOptionalSensors(true)
            // 启用数据保护
            .setEnableDataSaver(true)
            // 启用黑夜模式
            .setEnableNightMode(false)
            // 强制所有应用进入待机模式
            .setForceAllAppsStandby(false)
            // 设置是否对所有应用程序（不仅仅是针对Android的应用程序）强制进行背景检查（禁止后台服务和显示广播接收器）
            .setForceBackgroundCheck(false)
            // 设置声音触发模式
            // PowerManager.SOUND_TRIGGER_MODE_ALL_ENABLED=0
            // PowerManager.SOUND_TRIGGER_MODE_CRITICAL_ONLY=1 用于将声音触发器识别限制为仅系统认为必要的识别
            // PowerManager.SOUND_TRIGGER_MODE_ALL_DISABLED=2
            .setSoundTriggerMode(0)
            .build()
        powerManager.setAdaptivePowerSavePolicy(policyConfig)
        // 设置全省电模式策略
//        powerManager.setFullPowerSavePolicy(policyConfig)
    }

    /**
     * 设置在熄屏和熄屏动画执行完成后才进入休眠
     */
    fun setDozeAfterScreenOff() {
        L.dd()
        powerManager.setDozeAfterScreenOff(true)
    }

    /**
     * 设置动态省电阀值提醒
     * 1.是否激活省电模式
     * 2.省电模式的阀值（电池电量）
     */
    fun setDynamicPowerSaveHint() {
        L.dd()
        powerManager.setDynamicPowerSaveHint(true, 20)
    }
}