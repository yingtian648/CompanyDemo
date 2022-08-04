package com.exa.baselib.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;


import java.io.File;
import java.io.IOException;

/**
 * @作者 刘世华
 * @创建日期 2021-2-2
 * @描述 视频播放器
 */

public class VideoViewPlayer {
    private final String lOG_TAG = "VideoPlayerUtil";
    private VideoView videoView;
    private Context context;
    private MediaController controller;
    private boolean isPause = false;
    private Callback callback;
    private MediaPlayer mediaPlayer;

    public interface Callback {
        void onError(String msg);

        void onStarted();

        void onComplete();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public VideoViewPlayer(Context context, VideoView videoView) {
        this.context = context;
        this.videoView = videoView;
        initVideoView();
    }

    private void initVideoView() {
        controller = new MediaController(context);
        controller.setPrevNextListeners(v -> {
            L.d("click v");
        }, v1 -> {
            L.d("click v1");
        });
        //用来设置控制台样式
        videoView.setMediaController(controller);
        videoView.setOnPreparedListener(mediaPlayer -> {
            //开始播放
            videoView.start();
            if (this.callback != null) {
                callback.onStarted();
            }
        });
        //监听视频播放完的代码
        videoView.setOnCompletionListener(mPlayer -> {
            if (this.callback != null) {
                callback.onComplete();
            }
        });
        /**
         * Called to indicate an error.
         *
         * @param mp      the MediaPlayer the error pertains to
         * @param what    the type of error that has occurred:
         * <ul>
         * <li>{@link #MEDIA_ERROR_UNKNOWN 1}
         * <li>{@link #MEDIA_ERROR_SERVER_DIED 100} Media server died. In this case, the application must release the
         *      * MediaPlayer object and instantiate a new one.
         * </ul>
         * @param extra an extra code, specific to the error. Typically
         * implementation dependent.
         * <ul>
         * <li>{@link #MEDIA_ERROR_IO}
         * <li>{@link #MEDIA_ERROR_MALFORMED}
         * <li>{@link #MEDIA_ERROR_UNSUPPORTED}
         * <li>{@link #MEDIA_ERROR_TIMED_OUT}
         * <li><code>MEDIA_ERROR_SYSTEM (-2147483648)</code> - low-level system error.
         * </ul>
         * @return True if the method handled the error, false if it didn't.
         * Returning false, or not having an OnErrorListener at all, will
         * cause the OnCompletionListener to be called.
         */
        videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
            L.d(lOG_TAG, "videoView_onError:what=" + 1 + "\textra=" + extra);
            return true;
        });
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                videoView.setBackground(null);
            }
            return false;
        });
    }

    public void play(String path, boolean isOnInterNet) {
        L.d("VideoPlayerUtil.play:" + path);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        videoView.resume();
        //用来设置要播放的mp4文件
        if (isOnInterNet) {
            if (TextUtils.isEmpty(path)) {
                L.e(lOG_TAG, "未找到播放地址");
                return;
            }
            Uri uri = Uri.parse(path);
            videoView.setVideoURI(uri);
            videoView.setMediaController(controller);
            isPause = false;
        } else {
            File file = new File(path);
            if (!file.exists()) {
                L.e(lOG_TAG, "视频文件未找到");
                return;
            }
            if (file.length() < 5 * 1024) {//文件小于5K
                L.e(lOG_TAG, "视频文件小与5k，无法播放");
                return;
            }
            videoView.setVideoPath(path);
        }
        //用来设置起始播放位置，为0表示从开始播放
        videoView.seekTo(0);
        //用来设置mp4播放器是否可以聚焦
        videoView.requestFocus();
        videoView.bringToFront();
    }

    /**
     * 播放音频
     *
     * @param path
     */
    public void playAudio(String path) {
        L.d("VideoPlayerUtil.playAudio:" + path);
        stop();
        mediaPlayer = new MediaPlayer();
        try {
            if (path.startsWith("http:") || path.startsWith("https:")) {
                mediaPlayer.setDataSource(this.context, Uri.parse(path));
            } else {
                mediaPlayer.setDataSource(path);
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (IOException e) {
            e.printStackTrace();
            L.e("playAudio IOException:" + e.getMessage());
        }
    }

    /**
     * 停止播放音频
     *
     * @param
     */
    public void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public int getCurrentPostion() {
        return videoView.getCurrentPosition();
    }

    /**
     * 获取总时长
     *
     * @return
     */
    public int getDuration() {
        return videoView.getDuration();
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return videoView.isPlaying();
    }

    //停止播放
    public void stop() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            isPause = true;
        }
        stopAudio();
    }

    //继续播放
    public void resume() {
        if (videoView != null && isPause) {
            videoView.start();
            isPause = false;
        }
    }

    //摧毁播放器
    public void destroy() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            videoView = null;
        }
    }
}

