package com.exa.companydemo;

import android.content.res.AssetFileDescriptor;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.VideoPlayer;
import com.exa.companydemo.databinding.ActivitySecondBinding;

import androidx.annotation.Nullable;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

public class VideoPlayerActivity extends BaseBindActivity<ActivitySecondBinding> {

    private VideoPlayer player;
    private boolean wakeController = false;
    private boolean isFullScreen = false;
    private boolean isTouchSeekbar;

    @Override
    protected void initData() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = -3;
        layoutParams.flags |= -2138832824;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = 76;

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
        player.play(this, bind.frame, afd);
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
        return R.layout.activity_second;
    }

    @Override
    protected void initView() {
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
            if (isFullScreen) {
                ScreenUtils.showStatusBars(this);
                bind.fullBtn.setImageResource(com.exa.baselib.R.drawable.fullscreen_white);
//                bind.fullBtn.setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                ScreenUtils.setFullScreen(this);
                bind.fullBtn.setImageResource(com.exa.baselib.R.drawable.fullscreen_exit_white);
//                bind.fullBtn.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|SYSTEM_UI_FLAG_HIDE_NAVIGATION|SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                );
            }
            isFullScreen = !isFullScreen;
            wakeController = true;
            BaseConstants.getHandler().postDelayed(() -> {
                wakeController = false;
                hideController();
            }, 7000);
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }
}