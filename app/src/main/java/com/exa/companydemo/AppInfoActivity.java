package com.exa.companydemo;

import android.text.method.ScrollingMovementMethod;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.bean.AppInfo;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.databinding.ActivityAppInfoBinding;

import java.util.List;

public class AppInfoActivity extends BaseBindActivity<ActivityAppInfoBinding> {

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_app_info;
    }

    @Override
    protected void initView() {
        bind.text.setMovementMethod(ScrollingMovementMethod.getInstance());
        bind.text.setText("获取app列表...");
    }

    @Override
    protected void initData() {
        Constants.getSinglePool().execute(() -> {
            List<AppInfo> infos = Tools.getMobileAppsInfo(this);
            setText(infos);
        });
    }

    private void setText(List<AppInfo> infos) {
        if (infos == null) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < infos.size(); i++) {
            stringBuilder.append(infos.get(i).toString()).append("\n");
        }
        runOnUiThread(() -> {
            bind.text.setText(stringBuilder.toString());
        });
    }
}