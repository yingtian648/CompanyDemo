package com.exa.companydemo;

import android.app.UiModeManager;
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

import androidx.core.widget.TextViewKt;

public class TestActivity extends BaseBindActivity<ActivityTestBinding> {

    private MySurfaceView surfaceView;
    private int nightMode = 0;
    private UiModeManager mUiModeManager;

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
        mUiModeManager = getSystemService(UiModeManager.class);
        nightMode = mUiModeManager.getNightMode();

        bind.showCar.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                bind.mapBox.setVisibility(View.GONE);
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
        bind.btnUIMode.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                if (nightMode == UiModeManager.MODE_NIGHT_NO) {
                    mUiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                } else {
                    mUiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
                }
                nightMode = mUiModeManager.getNightMode();
                bind.btnUIMode.setText((nightMode == UiModeManager.MODE_NIGHT_YES) ? "白天模式" : "黑夜模式");
            }
        });
    }
}