package com.exa.companydemo.pms;

import android.content.Context;
import android.content.pm.PackageManager;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityPmsBinding;

public class PmsActivity extends BaseBindActivity<ActivityPmsBinding> {
    private PackageManager mPackageManager;
    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_pms;
    }

    @Override
    protected void initView() {
        mPackageManager = getPackageManager();





    }

    @Override
    protected void initData() {

    }
}