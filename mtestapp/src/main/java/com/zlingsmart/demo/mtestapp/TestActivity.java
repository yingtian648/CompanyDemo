package com.zlingsmart.demo.mtestapp;

import com.exa.baselib.base.BaseBindActivity;
import com.zlingsmart.demo.mtestapp.databinding.ActivityTestBinding;

public class TestActivity extends BaseBindActivity<ActivityTestBinding> {

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        bind.btnTest.setOnClickListener(v -> {
            test();
        });
    }

    private void test() {

    }

    @Override
    protected void initData() {

    }
}