package com.exa.companydemo.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;

import com.exa.baselib.utils.FileUtils;
import com.exa.baselib.utils.L;

import java.io.File;
import java.text.DecimalFormat;

import androidx.annotation.NonNull;


/**
 * @author  刘世华
 * @date  2021-2-2
 *
 * 1.判断没有存储卡——不录音
 * 2.判断存储卡容量小于50M——不录音
 * 3.默认小于1秒不保存
 * 自动保存
 * mMediaRecorder.getMaxAmplitude() 获取声音高低（分贝）
 */

public class RecordAudioUtil {

    /**
     * 高质量为true, 低质量为false
     */
    private boolean highQuality = true;
    private MediaRecorder mMediaRecorder;
    private boolean isRunning = false;
    private File saveFile;
    private long startTimeLong;
    private final AudioCallBack callBack;
    /**
     * 音量大小
     */
    private final int HAND_VOICE_RATIO = 0x10;
    /**
     * 录音结束返回
     */
    private final int HAND_RECORD_FINISH = 0x11;
    /**
     * 录音异常返回
     */
    private final int HAND_RECORD_ERR = 0x12;
    /**
     * 录音时间到
     */
    private final int HAND_TIME_OUT = 0x13;
    /**
     * 停止录制后延时1秒反馈（用于存储时间）
     */
    private final long RECORD_SAVE_DELAY_TIME_LONG = 1000;
    /**
     * 停止录制后延时1秒反馈（用于存储时间）
     */
    private final long REPORT_VOICE_RATIO_TIME_INTERVAL = 200;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAND_TIME_OUT:
                    stopRecord();
                    postDelayed(() -> {
                        if (saveFile != null && saveFile.exists() && saveFile.length() > 10) {
                            callBack.onRecordComplete(saveFile.getAbsolutePath());
                        } else {
                            backErr("录制失败");
                        }
                    }, RECORD_SAVE_DELAY_TIME_LONG);
                    break;
                case HAND_VOICE_RATIO:
                    postDelayed(voiceRatioRunnable, REPORT_VOICE_RATIO_TIME_INTERVAL);
                    break;
                default:
                    break;
            }
        }
    };

    public RecordAudioUtil(@NonNull AudioCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 统一处理返回失败消息
     */
    private void backErr(@NonNull String msg) {
        callBack.onRecordAudioErr(msg);
    }

    /**
     * 开始录音
     *
     * @param timeout 录音超时时长（单位：秒）
     */
    public boolean startRecord(String savePath, int timeout) {
        L.dd(savePath);
        if (isRunning) {
            return false;
        }
        if (!hasSdcard()) {
            L.e("缺少存储设备");
            backErr("缺少存储设备");
            return false;
        }
        if (!isSDCanUseSize50M()) {
            backErr("存储空间不足");
            return false;
        }
        saveFile = FileUtils.createNewFile(savePath);
        if (saveFile == null) {
            backErr("文件创建失败");
            return false;
        }
        mMediaRecorder = new MediaRecorder();
        // 在setOutputFormat之前
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置输出路径【会直接保存】
        mMediaRecorder.setOutputFile(saveFile.getAbsolutePath());
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 96 kHz is a very high sample rate, and is not guaranteed to be supported. I suggest that
        // you try a common sample rate <= 48 kHz (e.g. 48000, 44100, 22050, 16000, 8000).
        // 取值越大声音越好，文件也会越大
        mMediaRecorder.setAudioSamplingRate(highQuality ? 44100 : 22050);
        mMediaRecorder.setAudioEncodingBitRate(16000);
        // 设置单双声道，1是单声道，2是双声道
        mMediaRecorder.setAudioChannels(2);
        // 设置编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            startTimeLong = System.currentTimeMillis();
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            if (timeout > 0) {
                handler.sendEmptyMessageDelayed(HAND_TIME_OUT, timeout * 1000L);
            }
            handler.sendEmptyMessage(HAND_VOICE_RATIO);
        } catch (Exception e) {
            //未授权也会在这里抛出异常
            e.printStackTrace();
            L.e("mMediaRecorder报错:" + e.getMessage(), e);
            backErr("设置MediaRecorder失败");
            return false;
        }
        isRunning = true;
        return true;
    }

    private final Runnable voiceRatioRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaRecorder != null) {
                double ratio = mMediaRecorder.getMaxAmplitude();
                double db = 0;// 分贝
                if (ratio > 1) {
                    db = (20 * Math.log10(ratio));
                }
                callBack.onVoiceRatio(Tools.getDoubleFormatD(db));
            }
            handler.postDelayed(voiceRatioRunnable, REPORT_VOICE_RATIO_TIME_INTERVAL);
        }
    };

    /**
     * 停止录音
     * 【不对外提供是否保存的功能——始终保存】
     */
    public void stopRecord() {
        handler.removeCallbacks(voiceRatioRunnable);
        if (!isRunning || mMediaRecorder==null) {
            return;
        }
        long endTimeLong = System.currentTimeMillis();

        // 开始时间与结束时间小于一秒则不保存
        if ((endTimeLong - startTimeLong) < 1000) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            isRunning = false;
            if (saveFile.exists()) {
                saveFile.delete();
            }
            return;
        }

        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        isRunning = false;
        handler.postDelayed(() -> {
            callBack.onRecordComplete(saveFile.getAbsolutePath());
        }, RECORD_SAVE_DELAY_TIME_LONG);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        // 有存储的SDCard
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获得sd卡剩余容量是否有50M，即可用大小
     */
    public static boolean isSDCanUseSize50M() {
        if (!hasSdcard()) {
            return false;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //SD卡的单位大小
        long size = sf.getBlockSize();
        //总数量
        long total = sf.getBlockCount();
        //可使用的数量
        long available = sf.getAvailableBlocks();
        DecimalFormat df = new DecimalFormat();
        //每3位分为一组
        df.setGroupingSize(3);
//        //总容量
//        String totalSize = (size * total) / 1024 >= 1024 ? df.format(((size * total) / 1024) / 1024) + "MB" : df.format((size * total) / 1024) + "KB";
//        //未使用量
//        String avalilable = (size * available) / 1024 >= 1024 ? df.format(((size * available) / 1024) / 1024) + "MB" : df.format((size * available) / 1024) + "KB";
//        //已使用量
//        String usedSize = size * (total - available) / 1024 >= 1024 ? df.format(((size * (total - available)) / 1024) / 1024) + "MB" : df.format(size * (total - available) / 1024) + "KB";
        if (size * available / 1024 / 1024 < 50) {
            return false;
        }
        return true;
    }
}
