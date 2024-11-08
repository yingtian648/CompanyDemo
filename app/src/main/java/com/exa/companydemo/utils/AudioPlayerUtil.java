package com.exa.companydemo.utils;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import com.exa.baselib.utils.L;

import java.io.IOException;


/**
 * @author lsh
 * @date 2021-2-2 15:15
 * @描述 音频播放器-播放一次
 *
 * mediaPlayer.setLooping(true); 设置循环播放-不回调OnCompleteListener
 */
public class AudioPlayerUtil {
    private static AudioPlayerUtil playerUtil;
    private MediaPlayer mPlayer;
    private boolean isPlaying = false;

    /**
     * 播放回调
     */
    interface AudioPlayerListener {
        void onStarted();

        void onComplete();
    }

    public static AudioPlayerUtil getInstance() {
        return ClassHolder.PLAYER_UTIL;
    }

    private static class ClassHolder {
        private static final AudioPlayerUtil PLAYER_UTIL = new AudioPlayerUtil();
    }

    private AudioPlayerUtil() {

    }

    /**
     * 设置播放回调
     */
    public void setListener(AudioPlayerListener listener) {
        if (listener != null) {
            // 准备好播放
            mPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();// 播放器准备好后直接播放
                isPlaying = true;
                listener.onStarted();
            });
            mPlayer.setOnCompletionListener(mediaPlayer -> {
                isPlaying = false;
                listener.onComplete();
            });
        } else {
            // 准备好播放
            mPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();// 播放器准备好后直接播放
                isPlaying = true;
            });
            mPlayer.setOnCompletionListener(mediaPlayer -> {
                isPlaying = false;
            });
        }
    }

    /**
     * 播放音频
     *
     * @param path 音频本地路径/网络地址
     */
    public void play(String path) {
        this.play(path, null);
    }

    /**
     * 播放音频
     *
     * @param path 音频本地路径/网络地址
     */
    public void play(String path, AudioPlayerListener listener) {
        if (mPlayer == null) {
            //创建MediaPlayer和设置监听
            mPlayer = new MediaPlayer();
            AudioAttributes attributes = new AudioAttributes.Builder()
                    // 播放通道
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    // 声音类型
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mPlayer.setAudioAttributes(attributes);
        }
        setListener(listener);
        if (isPlaying) {
            mPlayer.stop();
            mPlayer.reset();
        }
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            L.e("mPlayer.prepareAsync-IOException:" + e.getMessage(), e);
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        isPlaying = false;
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.reset();
            }
        }
    }

    /**
     * activity/fragment ondestory中调用
     */
    public void onDestroy() {
        isPlaying = false;
        if (mPlayer != null) {
            mPlayer.setOnPreparedListener(null);
            mPlayer.setOnCompletionListener(null);
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.reset();
            }
            mPlayer.release();
            mPlayer = null;
        }
    }
}
