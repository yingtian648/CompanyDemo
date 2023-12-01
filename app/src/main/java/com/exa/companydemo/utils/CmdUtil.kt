package com.exa.companydemo.utils

import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.exa.baselib.utils.L
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.LineNumberReader

object CmdUtil {

    const val CMD_DEVINFO = "getprop"   //获取系统属性
    const val ABD_CMD_DEVINFO =
        "adb shell getprop"   //adb 获取设备信息【获取指定信息adb shell getprop ro.build.version.sdk】

    const val HANDLER_EXE_COMMAND = 12345

    /**
     * 执行命令的方法
     *
     * @param command
     * @param isRoot
     * @param handler result = 0正常 1失败 -1异常
     */
    fun exeCommand(command: String?, isRoot: Boolean, handler: Handler?) {
        if (command == null || command == "") {
            L.d("没有需要执行的命令")
            return
        } else {
            L.d("执行命令：$command isRoot=$isRoot")
        }
        object : Thread() {
            override fun run() {
                super.run()
                var process: Process? = null
                var os: DataOutputStream? = null
                var result = -1 //0正常 1失败 -1异常
                var errMsg: String? = null
                var norMsg: String? = null
                try {
                    process = Runtime.getRuntime().exec(if (isRoot) "su root" else "sh")
                    os = DataOutputStream(process.outputStream)
                    os.writeBytes("$command\n")
                    os.writeBytes("exit\n")
                    os.flush()
                    process.waitFor()
                    result = process.waitFor()
                    if (result != 0) {
                        val err = InputStreamReader(process.errorStream)
                        val returnDataErr = LineNumberReader(err)
                        var line: String?
                        while (returnDataErr.readLine().also { line = it } != null) {
                            errMsg += "$line\n"
                        }
                        err.close()
                    } else {
                        val nor = InputStreamReader(process.inputStream)
                        val returnDataNor = LineNumberReader(nor)
                        var line: String? = null
                        while (returnDataNor.readLine().also { line = it } != null) {
                            if (norMsg == null) {
                                norMsg = line
                            } else {
                                norMsg += "$line\n"
                            }
                        }
                        nor.close()
                    }
                } catch (e: Exception) {
                    errMsg = "Exception:" + e.message
                } finally {
                    try {
                        os?.close()
                        process!!.destroy()
                    } catch (e: Exception) {
                    }
                }
                if (handler != null) {
                    val message = Message()
                    message.obj = result
                    val bundle = Bundle()
                    bundle.putString("command", command)
                    if (errMsg != null) bundle.putString("err", errMsg)
                    if (norMsg != null) bundle.putString("nor", norMsg)
                    message.data = bundle
                    message.what = HANDLER_EXE_COMMAND
                    handler.sendMessage(message)
                }
            }
        }.start()
    }
}