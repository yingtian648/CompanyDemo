package com.exa.lsh.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import androidx.annotation.NonNull;


/**
 * @description 视频播放器 TextureView+MediaPlayer
 * @apiNote 1.播放网络视频需要添加权限 Manifest.permission.INTERNET
 * 2.need android:hardwareAccelerated="true" config in AndroidManifest
 */
public class VideoPlayer implements TextureView.SurfaceTextureListener {
    @SuppressLint("StaticFieldLeak")
    private static VideoPlayer player;
    private final Handler handler;
    private MediaPlayer mediaPlayer;
    private FrameLayout frameLayout;
    private String playPath;// 播放路径
    private Callback callback;// 回调
    private Context context;
    private ProgressBar progressBar;
    private ExecutorService service;
    private boolean isRunning = false;
    private boolean isLoop = true;
    private TextureView textureView;
    private BlockingDeque<String> playList;
    private AssetFileDescriptor assetFileDescriptor;
    private final int HANDLE_REPEAT_TIME = 1;

    public interface Callback {
        void onError(String msg);

        void onStarted(int duration);

        void onComplete();

        void onPlayTime(int timeMillis);

        void onScreenClick();
    }

    private VideoPlayer() {
        playList = new LinkedBlockingDeque<>();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == HANDLE_REPEAT_TIME) {
                    if (isRunning) {
                        if (callback != null) {
                            callback.onPlayTime(mediaPlayer.getCurrentPosition());
                        }
                        sendEmptyMessageDelayed(HANDLE_REPEAT_TIME, 1000);
                    }
                }
            }
        };
    }

    public static synchronized VideoPlayer getInstance() {
        if (player == null) {
            player = new VideoPlayer();
        }
        return player;
    }

    public void play(Context context, FrameLayout frameLayout, List<String> pathList) {
        playList.clear();
        playList.addAll(pathList);
        try {
            play(context, frameLayout, playList.take());
        } catch (InterruptedException e) {
            L.e("play InterruptedException:" + e.getMessage(), e);
        }
    }

    public void play(Context context, FrameLayout frameLayout, AssetFileDescriptor fileDescriptor) {
        playList.clear();
        assetFileDescriptor = fileDescriptor;
        play(context, frameLayout, (String) null);
    }

    /**
     * 播放视频
     */
    public void play(Context context, FrameLayout frameLayout, String path) {
        L.d("VideoPlayer.play:" + path);
        isRunning = true;
        this.context = context.getApplicationContext();
        this.playPath = path;
        this.frameLayout = frameLayout;
        if (frameLayout == null || (path == null && assetFileDescriptor == null)) return;
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            if (frameLayout.getChildAt(i) != null
                    && (frameLayout.getChildAt(i) instanceof TextureView
                    || frameLayout.getChildAt(i) instanceof ProgressBar)) {
                try {
                    frameLayout.removeView(frameLayout.getChildAt(i));
                } catch (Exception e) {
                    L.de(e);
                }
            }
        }
        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(this);
        textureView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        this.progressBar = new ProgressBar(context);
        FrameLayout.LayoutParams plp = new FrameLayout.LayoutParams(80, 80);
        plp.gravity = Gravity.CENTER;
        this.progressBar.setLayoutParams(plp);
        frameLayout.addView(textureView);
        frameLayout.addView(this.progressBar);
        textureView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onScreenClick();
            }
        });
    }

    /**
     * 播放音频
     */
    public void playAudio(String path) {
        L.d("VideoPlayer.playAudio:" + path);
        stop();
        mediaPlayer = new MediaPlayer();
        try {
            if (playPath.startsWith("http:") || playPath.startsWith("https:")) {
                mediaPlayer.setDataSource(this.context, Uri.parse(playPath));
            } else {
                mediaPlayer.setDataSource(playPath);
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (IOException e) {
            L.e("playAudio IOException:" + e.getMessage(), e);
        }
    }

    /**
     * 停止播放
     * 在Activity/Fragment中OnDestory调用
     */
    public void stop() {
        isRunning = false;
        if (frameLayout != null) {
            frameLayout.removeAllViews();
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 暂停播放
     * 在Activity/Fragment中OnDestory调用
     */
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 继续播放
     * 在Activity/Fragment中OnDestory调用
     */
    public void resume() {
        if (mediaPlayer != null)
            mediaPlayer.start();
    }

    /**
     * 是否循环播放-默认循环播放
     */
    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(isLoop);
        }
    }

    /**
     * 设置回调
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void seekTo(int position) {
        L.d("seekTo:" + position);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(position);
        }
    }

    /**
     * 开始播放
     */
    private void startPlay(Surface surface) {
        try {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
            mediaPlayer.setScreenOnWhilePlaying(true);
            if (assetFileDescriptor != null) {
                mediaPlayer.setDataSource(assetFileDescriptor);
            } else if (playPath.startsWith("http:") || playPath.startsWith("https:")) {
                mediaPlayer.setDataSource(this.context, Uri.parse(playPath));
            } else {
                mediaPlayer.setDataSource(playPath);
            }
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            L.d("prepareAsync");
            mediaPlayer.setOnPreparedListener(mp -> {
                isRunning = true;
                this.progressBar.setVisibility(View.GONE);
                mp.start();
                L.d("mediaPlayer.start:" + playPath);
                if (callback != null) {
                    callback.onStarted(mp.getDuration());
                }
                handler.sendEmptyMessage(HANDLE_REPEAT_TIME);
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
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (callback != null) {
                    callback.onError("播放失败");
                }
                L.e("mediaPlayer-err:what=" + what + "::extra=" + extra);
                return false;
            });
            mediaPlayer.setOnInfoListener((mp, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START && frameLayout != null) {
                    frameLayout.setBackground(null);
                }
                return false;
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                isRunning = false;
                if (isLoop) {
                    if (!playList.isEmpty()) {
                        try {
                            playPath = playList.take();
                            startPlay(surface);
                        } catch (InterruptedException e) {
                            L.e("播放 InterruptedException:" + playPath, e);
                        }
                    } else {
                        startPlay(surface);
                    }
                } else if (callback != null) {
                    callback.onComplete();
                }
            });
        } catch (IllegalStateException ie) {
            L.e("mediaPlayer-IllegalStateException:" + ie.getMessage(), ie);
            if (callback != null) {
                callback.onError("播放失败，播放器发生未知错误");
            }
        } catch (IOException e) {
            L.e("mediaPlayer-IOException:" + e.getMessage(), e);
            if (callback != null) {
                callback.onError("播放失败，播放器出错");
            }
        }
    }

    /**
     * 初始化完成
     * @param surface The surface returned by
     *                {@link TextureView#getSurfaceTexture()}
     * @param width The width of the surface
     * @param height The height of the surface
     */
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        L.dd();
        //按比例缩放视频
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Uri uri = null;
        if (playPath != null)
            if (playPath.startsWith("http://") || playPath.startsWith("https://")) {
                uri = Uri.parse(playPath);
            } else {
                if (!new File(playPath).exists()) {
                    L.e("playPath is null");
                    return;
                }
                uri = Uri.fromFile(new File(playPath));
            }
        try {
            if (assetFileDescriptor != null) {
                mmr.setDataSource(assetFileDescriptor.getFileDescriptor());
            } else {
                mmr.setDataSource(context, uri);
            }
            String videoW = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String videoH = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            int vw = 0;
            int vh = 0;
            if (videoW != null) {
                vw = Integer.parseInt(videoW);
            }
            if (videoH != null) {
                vh = Integer.parseInt(videoH);
            }
            int lw = frameLayout.getWidth();
            int lh = frameLayout.getHeight();
            if (vw > lw || vh > lh) {
                // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
                float wRatio = (float) vw / (float) lw;
                float hRatio = (float) vh / (float) lh;
                // 选择大的一个进行缩放
                float ratio = Math.max(wRatio, hRatio);
                vw = (int) Math.ceil((float) vw / ratio);
                vh = (int) Math.ceil((float) vh / ratio);
                // 设置surfaceView的布局参数
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(vw, vh);
                lp.gravity = Gravity.CENTER;
                textureView.setLayoutParams(lp);
            }
            startPlay(new Surface(surface));
        } catch (Exception e) {
            L.e("onSurfaceTextureAvailable err:" + e.getMessage(), e);
            if (callback != null) {
                callback.onError("获取视频属性异常: " + e.getMessage());
            }
        } finally {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mmr.close();
                }
                mmr.release();
            } catch (IOException e) {
                L.e("getVideoFirstFrame-err", e);
            }
        }
    }


    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface,
                                            int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        stop();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    /**
     * 获取视频第一帧
     */
    public void setVideoFirstFrame(@NonNull Context context, View view, @NonNull String path) {
        isRunning = true;
        if (service == null) {
            service = Executors.newSingleThreadExecutor();
        }
        final Context app = context.getApplicationContext();
        service.execute(() -> {
            final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                if (path.startsWith("http:") || path.startsWith("https:")) {
                    mmr.setDataSource(path, new HashMap<>());
                } else {
                    mmr.setDataSource(app, Uri.parse(path));
                }
                Drawable drawable = new BitmapDrawable(app.getResources(),
                        mmr.getFrameAtTime());
                handler.post(() -> {
                    L.d("setVideoFirstFrame");
                    if (!isRunning) return;
                    if (view instanceof ImageView) {
                        ((ImageView) view).setImageDrawable(drawable);
                    } else {
                        view.setBackground(drawable);
                    }
                });
            } catch (IllegalArgumentException e) {
                L.e("getVideoFirstFrame-err:", e);
            } finally {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        mmr.close();
                    }
                    mmr.release();
                } catch (IOException e) {
                    L.e("getVideoFirstFrame-err", e);
                }
            }
        });
    }
}
