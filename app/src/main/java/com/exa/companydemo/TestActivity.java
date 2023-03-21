package com.exa.companydemo;

import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.companydemo.databinding.ActivityTestBinding;
import com.exa.companydemo.widget.MySurfaceView;

public class TestActivity extends BaseBindActivity<ActivityTestBinding> {

    private MySurfaceView surfaceView;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        surfaceView = new MySurfaceView(TestActivity.this);
        bind.mapContainer.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        bind.carFragment.addView(imageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Glide.with(this).load(R.drawable.win_bg).into(imageView);
    }

    @Override
    protected void initData() {
        bind.showCar.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                bind.mapBox.setVisibility(View.VISIBLE);
                bind.carFragment.setVisibility(View.VISIBLE);
            }
        });
        bind.showMap.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                bind.mapBox.setVisibility(View.VISIBLE);
                bind.carFragment.setVisibility(View.GONE);
            }
        });
    }
}