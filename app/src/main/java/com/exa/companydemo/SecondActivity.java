package com.exa.companydemo;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.VideoPlayer;
import com.exa.baselib.utils.VideoViewPlayer;
import com.exa.companydemo.databinding.ActivitySecondBinding;

public class SecondActivity extends BaseBindActivity<ActivitySecondBinding> {

    private VideoPlayer player;

    @Override
    protected void initData() {
        player = VideoPlayer.getInstance();
        player.setLoop(true);
        player.setCallback(new VideoPlayer.Callback() {
            @Override
            public void onError(String msg) {
                Toast.makeText(SecondActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStarted(int duration) {
                bind.progressBar.setMax(duration / 1000);
                bind.totalT.setText(String.valueOf(duration / 1000));
                BaseConstants.getHandler().postDelayed(()->{
                    hideController();
                },8000);
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
                updateControllerStatus();
            }

        });
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.test);
        player.play(this, bind.frame, afd);

//        VideoViewPlayer player = new VideoViewPlayer(this, bind.video);
//        player.play(Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.test));
    }

    private boolean isShowController = true;

    private void updateControllerStatus() {
        L.dd();
        isShowController = !isShowController;
        bind.controllerLine.setVisibility(isShowController ? View.GONE : View.VISIBLE);
    }

    private void hideController() {
        L.dd();
        isShowController = false;
        bind.controllerLine.setVisibility(View.GONE);
    }

    @Override
    protected int setContentViewLayoutId() {
        ScreenUtils.setFullScreen(this);
        return R.layout.activity_second;
    }

    @Override
    protected void initView() {
        bind.text.setOnClickListener(v -> finish());
        bind.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}