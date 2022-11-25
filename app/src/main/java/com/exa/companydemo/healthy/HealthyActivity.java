package com.exa.companydemo.healthy;

import android.view.View;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityHelthyBinding;
import com.exa.baselib.utils.StatubarUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HealthyActivity extends BaseBindActivity<ActivityHelthyBinding> {
    private int boo = 0;

    @Override
    protected int setContentViewLayoutId() {
        StatubarUtil.setStatusBarInvasion(this);
        StatubarUtil.setStatusBar(this, true);
        return R.layout.activity_helthy;
    }

    @Override
    protected void initView() {
        setBaseText();
        bind.obox.setVisibility(View.GONE);
        bind.image.setOnClickListener(v -> {
            boo++;
            if (boo > 2) {
                boo = 0;
            }
            bind.obox.setVisibility(View.GONE);
            switch (boo) {
                case 0:
                    bind.image.setImageResource(R.mipmap.a1);
                    StatubarUtil.setStatusBar(this, true);
                    break;
                case 1:
                    bind.obox.setVisibility(View.VISIBLE);
                    bind.image.setImageResource(R.mipmap.jk2);
                    StatubarUtil.setStatusBar(this, false);
                    break;
                case 2:
                    bind.image.setImageResource(R.mipmap.a3);
                    StatubarUtil.setStatusBar(this, false);
                    break;
            }
        });
    }

    private void setBaseText() {
        Date date = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
        String data = dateFormat.format(date);
        bind.ftxt.setText(data + " 15时");
        date = new Date();
        SimpleDateFormat dateFormata = new SimpleDateFormat("yyyy-MM-dd");
        bind.afterT.setText(dateFormata.format(date));
        SimpleDateFormat dateFormatm = new SimpleDateFormat("HH:mm:");
        bind.beforeT.setText(dateFormatm.format(date));
    }

    @Override
    protected void initData() {

    }
}