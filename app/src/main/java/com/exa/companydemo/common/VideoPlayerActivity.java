package com.exa.companydemo.common;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.widget.SeekBar;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.SystemBarUtil;
import com.exa.baselib.utils.VideoPlayer;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityVideoPlayerBinding;
import com.exa.companydemo.utils.Ces2715Util;

import java.io.IOException;

public class VideoPlayerActivity extends BaseBindActivity<ActivityVideoPlayerBinding> {

    private VideoPlayer player;
    private boolean wakeController = false;
    private boolean isFullScreen = false;
    private boolean isTouchSeekbar;

    @SuppressLint("WrongConstant")
    @Override
    protected void initData() {
        switchFullScreen();
        // use TextureView, need set android:hardwareAccelerated="true"
        player = VideoPlayer.getInstance();
        player.setLoop(true);
        player.setCallback(new VideoPlayer.Callback() {
            @Override
            public void onError(String msg) {
                Toast.makeText(VideoPlayerActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStarted(int duration) {
                bind.progressBar.setEnabled(true);
                bind.progressBar.setMax(duration / 1000);
                bind.totalT.setText(String.valueOf(duration / 1000));
                delayHideControl();
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onPlayTime(int timeMillis) {
                bind.durT.setText(String.valueOf(timeMillis / 1000));
                bind.progressBar.setProgress(timeMillis / 1000);
            }

            @Override
            public void onScreenClick() {
                BaseConstants.getHandler().removeCallbacksAndMessages(null);
                updateControllerStatus();
            }

        });
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.test);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test);
        try {
            afd = getAssets().openFd("test.mp4");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.play(this, bind.frame, afd);
    }

    private void switchFullScreen() {
        isFullScreen = !isFullScreen;
        if (isFullScreen) {
            SystemBarUtil.hideStatusBars(this);
            bind.fullBtn.setImageResource(com.exa.baselib.R.drawable.fullscreen_white);
        } else {
            SystemBarUtil.showStatusBars(this);
            bind.fullBtn.setImageResource(com.exa.baselib.R.drawable.fullscreen_exit_white);
        }
        wakeController = true;
        BaseConstants.getHandler().postDelayed(() -> {
            wakeController = false;
            hideController();
        }, 7000);
    }

    private boolean isShowController = true;

    private void updateControllerStatus() {
        isShowController = !isShowController;
//        bind.controllerLine.setVisibility(isShowController ? View.VISIBLE : View.GONE);
        if (isShowController) {
            delayHideControl();
        }
    }

    private void delayHideControl() {
        BaseConstants.getHandler().postDelayed(this::hideController, 7000);
    }

    private void hideController() {
//        if (!wakeController) {
//            isShowController = false;
//            bind.controllerLine.setVisibility(View.GONE);
//        }
    }

    @Override
    protected int setContentViewLayoutId() {
//        overridePendingTransition(R.anim.task_open_enter,R.anim.task_open_enter);
        return R.layout.activity_video_player;
    }

    private void test(){
        L.d(getClass().getSimpleName() + "#test");
//        startService(new Intent(this, DemoService.class));
        Ces2715Util.switchMoveTask(this);
    }

    @Override
    protected void initView() {
        bind.testBtn.setOnClickListener(v -> {
            test();
        });
        bind.text.setOnClickListener(v -> finish());
        bind.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isTouchSeekbar) {
                    BaseConstants.getHandler().post(() -> {
                        bind.durT.setText(String.valueOf(progress));
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                wakeController = true;
                isTouchSeekbar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                L.d("max:" + seekBar.getMax());
                player.seekTo(seekBar.getProgress() * 1000);
                BaseConstants.getHandler().postDelayed(() -> {
                    wakeController = false;
                    isTouchSeekbar = false;
                    hideController();
                }, 7000);
            }
        });
        bind.progressBar.setEnabled(false);
        bind.fullBtn.setOnClickListener(v -> {
            switchFullScreen();
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
        L.dd(getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.resume();
        L.dd(getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
        L.dd(getClass().getSimpleName());
    }
}