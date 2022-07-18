package com.exa.companydemo.healthy;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityHelthyBinding;
import com.exa.companydemo.utils.StatubarUtil;

public class HealthyActivity extends BaseBindActivity<ActivityHelthyBinding> {
    private boolean boo = false;
    @Override
    protected int setContentViewLayoutId() {
        StatubarUtil.setStatusBarInvasion(this);
        StatubarUtil.setStatusBar(this,true);
        return R.layout.activity_helthy;
    }

    @Override
    protected void initView() {
        bind.image.setOnClickListener(v -> {
            if(boo){
                bind.image.setImageResource(R.mipmap.a1);
                StatubarUtil.setStatusBar(this,true);
            }else {
                bind.image.setImageResource(R.mipmap.a3);
                StatubarUtil.setStatusBar(this,false);
            }
            boo = !boo;
        });
    }

    @Override
    protected void initData() {

    }
}