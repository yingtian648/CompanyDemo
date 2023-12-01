package com.exa.companydemo.utils

import android.content.Context
import android.os.Environment
import com.exa.baselib.utils.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object PathUtil {
    lateinit var userAppPrivate: String
        private set
    var storageAndroidAppPrivate: String? = null
        private set
    var mobileRoot: String? = null
        private set

    private const val appPrivateDir = "/ACompanyDemo"
    private const val musicDir = "/Music"
    private const val musicLyricDir = "/Lyric"

    fun init(context: Context) {
        //指向私有目录/data/user/0/com.exa.companydemo/files/acompanydemo
        //不需要读写权限 应保存相对路径
        userAppPrivate = context.filesDir.absolutePath.plus(appPrivateDir)
        makeDirPath(userAppPrivate)
        //指向私有目录/storage/emulated/0/Android/data/com.exa.companydemo/files
        //不需要读写权限 可能并不总是可用的
        storageAndroidAppPrivate = context.getExternalFilesDir(null)?.absolutePath
        //指向设备根目录/storage/emulated/0/acompanydemo
        mobileRoot = Environment.getExternalStorageDirectory()?.absolutePath.plus(appPrivateDir)
        L.w("userAppPrivate $userAppPrivate")
        L.w("storageAndroidAppPrivate $storageAndroidAppPrivate")
        L.w("mobileRoot $mobileRoot")
    }

    suspend fun getScreenRecordPath(): String {
        val path = storageAndroidAppPrivate.plus("/ScreenRecord")
        withContext(Dispatchers.IO) {
            makeDirPath(path)
        }
        return path
    }

    fun getScreenShotPath(): String {
        val path = storageAndroidAppPrivate.plus("/ScreenShot")
        makeDirPath(path)
        return path
    }

    fun getAppPath(): String {
        val path = mobileRoot.plus("/apps")
        makeDirPath(path)
        return path
    }

    fun getReceiveFilePath(): String {
        val path = mobileRoot.plus("/download")
        makeDirPath(path)
        return path
    }

    fun getVideoRecordPath(): String {
        val path = "$mobileRoot/video"
        makeDirPath(path)
        return path;
    }

    fun getPrivateVideoRecordPath(): String {
        val path = "$userAppPrivate/video"
        makeDirPath(path)
        return path;
    }

    fun getPhotoPath(): String {
        val path = "$mobileRoot/photo"
        makeDirPath(path)
        return path
    }

    /**
     * 音频存储路径
     */
    fun getAudioPath(): String {
        val path = "$mobileRoot/audio"
        makeDirPath(path)
        return path
    }

    /**
     * 文件缓存路径
     */
    fun getCachePath(): String {
        val path = "$mobileRoot/cache"
        makeDirPath(path)
        return path
    }

    /**
     *  本地歌曲缓存路径
     */
    fun getMusicPath(): String {
        val path = mobileRoot!!.replace(appPrivateDir, musicDir)
        makeDirPath(path)
        return path
    }

    /**
     * 文件缓存路径
     */
    fun getMusicLyricPath(): String {
        val path = mobileRoot!!.replace(appPrivateDir, musicDir)
            .plus(musicLyricDir)
        makeDirPath(path)
        return path
    }

    /**
     * 输出文件路径
     */
    fun getOutputPath(): String {
        val path = "$mobileRoot/output"
        makeDirPath(path)
        return path
    }

    /**
     * 输出文件路径
     */
    fun getBookPath(): String {
        val path = "$mobileRoot/book"
        makeDirPath(path)
        return path
    }

    /**
     * 搜藏页面本地保存，防止卸载重装后数据丢失
     */
    fun getWebCollectPath(): String {
        return "$mobileRoot/webCollection.jpg"
    }

    private fun makeDirPath(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        } else if (!file.isDirectory) {
            file.delete()
            file.mkdir()
        }
    }

    fun clearPath(path: String?) {
        path?.apply {
            val file = File(this)
            if (file.exists()) {
                if (!file.isDirectory) {
                    file.delete();
                } else {
                    file.listFiles()?.forEach {
                        if (it.isDirectory) {
                            clearPath(it.absolutePath)
                        } else {
                            it.delete()
                        }
                    }
                }
            }
        }
    }
}