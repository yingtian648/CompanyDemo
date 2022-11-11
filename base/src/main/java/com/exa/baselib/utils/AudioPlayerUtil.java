package com.exa.baselib.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;


import java.io.File;
import java.io.IOException;

/**
 * @作者 刘世华
 * @创建时间 2021-2-2 15:15
 * @描述 音频播放器-播放一次
 * <p>
 * mediaPlayer.setLooping(true); 设置循环播放-不回调OnCompleteListener
 */
public class AudioPlayerUtil {
    private static AudioPlayerUtil playerUtil;
    private MediaPlayer mPlayer;
    private boolean isPlaying = false;

    /**
     * 播放回调
     */
    public interface AudioPlayerListener {
        void onStarted();

        void onErr(String msg);

        void onComplete();
    }

    public static AudioPlayerUtil getInstance() {
        return ClassHolder.playerUtil;
    }

    private static class ClassHolder {
        private static AudioPlayerUtil playerUtil = new AudioPlayerUtil();
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

    public void play(Context context, Uri uri, AudioPlayerListener listener) {
        if (mPlayer == null) {
            //创建MediaPlayer和设置监听
            mPlayer = new MediaPlayer();
        }
        setListener(listener);
        if (isPlaying) {
            mPlayer.stop();
            mPlayer.reset();
        }
        try {
            mPlayer.setDataSource(context, uri);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onErr("mPlayer.prepareAsync-IOException:" + e.getMessage());
            }
            L.e("mPlayer.prepareAsync-IOException:" + e.getMessage(), e);
        }catch (IllegalArgumentException e1){
            L.e("mPlayer.prepareAsync-IllegalArgumentException:" + e1.getMessage(), e1);
        }catch (SecurityException se){
            L.e("mPlayer.prepareAsync-SecurityException:" + se.getMessage(), se);
        }catch (IllegalStateException ie){
            L.e("mPlayer.prepareAsync-IllegalStateException:" + ie.getMessage(), ie);
        }
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
            if (listener != null) {
                listener.onErr("mPlayer.prepareAsync-IOException:" + e.getMessage());
            }
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
    public void ondestory() {
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
