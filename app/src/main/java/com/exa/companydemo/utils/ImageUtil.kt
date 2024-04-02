package com.exa.companydemo.utils

import android.graphics.*
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.os.SystemClock
import android.os.Trace
import com.exa.baselib.BaseConstants
import com.exa.baselib.utils.DateUtil
import com.exa.baselib.utils.FileUtils
import com.exa.baselib.utils.L
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @Author lsh
 * @Date 2024/2/23 11:09
 * @Description
 */
object ImageUtil {
    private val list = arrayListOf<String>()
    private val pictureDirFile = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)

    /** 等同于ImageView.ScaleType.FIT_XY */
    const val SCALE_XY = 1

    /** 等比例缩放 */
    const val SCALE_RES = 2

    /** 从中间裁剪 */
    const val SCALE_CENTER = 3

    interface Callback {
        fun onSplitResult(pathList: ArrayList<String>) {}
        fun onComposeResult(path: String) {}
        fun onError(msg: String) {}
    }

    init {
        pictureDirFile.listFiles()?.forEach {
            if (!it.isDirectory) {
                list.add(it.absolutePath)
            }
        }
        list.sort()
    }

    fun composeImages() {
        composeImages(list, hasDivider = true, whiteDivider = true, scaleType = SCALE_CENTER)
    }

    fun splitImage() {
        var path = ""
        list.forEach {
            if (it.contains("6.jpg")) {
                path = it
                return@forEach
            }
        }
        splitImage(path)
    }

    fun makeVideoFromImages() {
        makeVideoFromImages(list)
    }

    /**
     * 将多张图片合并成1张图片
     */
    fun composeImages(
        list: List<String>,
        hasDivider: Boolean = false,
        whiteDivider: Boolean = true,
        scaleType: Int = SCALE_XY,
        callback: Callback? = null
    ) {
        Trace.beginSection("composeImages")
        if (list.size == 4 || list.size == 9 || list.size == 16) {
            // 合成后的图片宽高
            val outW = 1920
            val outH = 1080
            // 图片距离
            val rowSize = sqrt(list.size.toDouble()).toInt()
            val imgDis = if (!hasDivider) 0 else if (rowSize == 3) 3 else if (rowSize == 4) 4 else 2
            val imgW = (outW - (rowSize - 1) * imgDis) / rowSize
            val imgH = (outH - (rowSize - 1) * imgDis) / rowSize
            // 图片宽高比
            val imgProp = imgW.toFloat() / imgH
            L.d("composeImages w=$imgW h=$imgH rowSize=$rowSize")
            BaseConstants.getFixPool().execute {
                val startTime = SystemClock.elapsedRealtime()
                L.d("composeImages start ${list.size}")
                val bitmap = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                var orx = 0
                var ory = 0
                if (whiteDivider) {
                    canvas.drawColor(Color.WHITE)
                }
                for (i in list.indices) {
                    if (orx == rowSize) {
                        ory++
                        orx = 0
                    }
                    val rect = Rect(
                        orx * imgW + orx * imgDis,
                        ory * imgH + ory * imgDis,
                        orx * imgW + orx * imgDis + imgW,
                        ory * imgH + ory * imgDis + imgH,
                    )
                    BitmapFactory.decodeFile(list[i]).apply {
                        val proportion = width.toFloat() / height
                        val resRect = Rect(0, 0, width, height)
                        // 宽高比与目标宽高比差距很小，则直接忽略，选用SCALE_XY
                        if (abs(imgProp - proportion) < 0.2F) {
                            rect.set(rect.left, rect.top, rect.right, rect.bottom)
                        } else if (scaleType == SCALE_RES) {
                            if (imgProp > proportion) {
                                // 获取目标宽度差——使用高度缩放比获取目标显示宽度
                                val propW = (imgW - width * imgH.toFloat() / height) / 2
                                rect.set(
                                    rect.left + propW.toInt(),
                                    rect.top,
                                    rect.right - propW.toInt(),
                                    rect.bottom
                                )
                            } else {
                                // 获取目标高度差——使用宽度缩放比获取目标显示高度
                                val propH = (imgH - height * imgW.toFloat() / width) / 2
                                rect.set(
                                    rect.left,
                                    rect.top + propH.toInt(),
                                    rect.right,
                                    rect.bottom - propH.toInt()
                                )
                            }
                        } else if (scaleType == SCALE_CENTER) {
                            if (imgProp > proportion) {
                                // 获取裁剪的高度差——使用宽度缩放比
                                val h = (height - height * imgW.toFloat() / width) / 2
                                resRect.set(0, h.toInt(), width, height - h.toInt())
                            } else {
                                // 获取裁剪的宽度差——使用高度缩放比
                                val w = (width - width * imgH.toFloat() / height) / 2
                                resRect.set(w.toInt(), 0, width - w.toInt(), height)
                            }
                        }
                        L.d("composeImages $i $resRect $rect ${list[i]}")
                        canvas.drawBitmap(
                            this, resRect, rect, Paint()
                        )
                    }.recycle()
                    orx++
                }
                canvas.save()
                val outDir = File(pictureDirFile.absolutePath, "temp").apply {
                    if (exists()) {
                        listFiles()?.forEach {
                            if (it.name.startsWith("compose")) {
                                it.delete()
                            }
                        }
                    } else {
                        mkdir()
                    }
                }.absolutePath
                val savePath = "$outDir/compose${DateUtil.getNowTimeNum()}.jpg"
                FileUtils.saveBitmapToImage(bitmap, savePath)
                callback?.onComposeResult(savePath)
                L.d("composeImages end " + (SystemClock.elapsedRealtime() - startTime))
            }
        }
        Trace.endSection()
    }

    /**
     * 将图片分割成9等份
     */
    fun splitImage(path: String, callback: Callback? = null) {
        BaseConstants.getFixPool().execute {
            val startTime = SystemClock.elapsedRealtime()
            L.dd("start")
            try {
                val outDir = File(pictureDirFile.absolutePath, "temp").apply {
                    mkdir()
                }.absolutePath
                val bitmap = BitmapFactory.decodeFile(path)
                val outW = bitmap.width / 3
                val outH = bitmap.height / 3
                var orx = 0
                var ory = 0
                val arrayList = arrayListOf<String>()
                var outTemp = ""
                for (index in 0 until 9) {
                    if (orx == 3) {
                        ory++
                        orx = 0
                    }
                    outTemp = outDir + "/split" + (index + 1) + ".jpg"
                    Bitmap.createBitmap(bitmap, orx * outW, ory * outH, outW, outH)?.let {
                        FileUtils.saveBitmapToImage(it, outTemp)
                        arrayList.add(outTemp)
                    }
                    orx++
                }
                callback?.onSplitResult(arrayList)
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.onError("splitImage fail!" + e.message)
            }
            L.dd("end " + (SystemClock.elapsedRealtime() - startTime))
        }
    }

    /**
     * 将多张图片合成视频
     */
    fun makeVideoFromImages(paths: List<String>, callback: Callback? = null) {
        L.dd("start $paths")
        BaseConstants.getFixPool().execute {
            val startTime = SystemClock.elapsedRealtime()
            try {
                val outDir = File(pictureDirFile.absolutePath, "temp").apply {
                    mkdir()
                }.absolutePath
                val outTemp = "$outDir/video.mp4"
                // 输出格式
                val mediaMuxer = MediaMuxer(outTemp, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                // 输出格式
                val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1920, 1080)
                // 颜色格式
                mediaFormat.setInteger(
                    MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
                )
                // 平均比特率（以比特/秒为单位）
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1920 * 1080 * 3)
                // 每秒的帧数
                mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
                // 关键帧之间的频率（以秒为单位）
                mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
                mediaMuxer.addTrack(mediaFormat)
                mediaMuxer.start()
                paths.forEach {
                    val bitmap = BitmapFactory.decodeFile(it)
                    val byteBuffer = ByteBuffer.allocate(bitmap.byteCount)
                    bitmap.copyPixelsToBuffer(byteBuffer)
                    val byteBufferArray = byteBuffer.array()
                    val byteBufferInput = ByteBuffer.wrap(byteBufferArray)
                    val bufferInfo = MediaCodec.BufferInfo()
                    bufferInfo.presentationTimeUs = SystemClock.elapsedRealtimeNanos() / 1000
                    bufferInfo.size = byteBufferInput.capacity()
                    bufferInfo.offset = 0
                    bufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                    mediaMuxer.writeSampleData(0, byteBufferInput, bufferInfo)
                }
                mediaMuxer.stop()
                mediaMuxer.release()
                callback?.onComposeResult(outTemp)
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.onError("makeVideoFromImages fail!" + e.message)
            }
            L.dd("end " + (SystemClock.elapsedRealtime() - startTime))
        }
    }
}