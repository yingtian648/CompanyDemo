package com.exa.companydemo;

import android.content.res.AssetFileDescriptor;
import android.widget.Toast;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.ScreenUtils;
import com.exa.baselib.utils.VideoPlayer;
import com.exa.companydemo.databinding.ActivitySecondBinding;

public class SecondActivity extends BaseBindActivity<ActivitySecondBinding> {

    @Override
    protected void initData() {
        VideoPlayer player = VideoPlayer.getInstance();
        player.setLoop(true);
        player.setCallback(new VideoPlayer.Callback() {
            @Override
            public void onError(String msg) {
                Toast.makeText(SecondActivity.this,msg,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onComplete() {

            }
        });
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.test);
        player.play(this, bind.frame, afd);
    }

    @Override
    protected int setContentViewLayoutId() {
        ScreenUtils.setFullScreen(this);
        return R.layout.activity_second;
    }

    @Override
    protected void initView() {
        bind.text.setOnClickListener(v -> finish());
    }
}