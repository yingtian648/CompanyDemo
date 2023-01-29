package com.exa.companydemo;

import android.widget.Button;
import android.widget.TextView;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.companydemo.databinding.ActivityTestBinding;

public class TestActivity extends BaseBindActivity<ActivityTestBinding> {
    private TextView textView;
    private Button btn1;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        textView = findViewById(R.id.textView);
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(v -> {
            TestUtil.usbPermission(this);
        });
    }

    @Override
    protected void initData() {
        textView.setText("1.插入U盘后，点击测试按钮可调出申请USB权限的原生弹框。" + "\n" + "2.点击取消后再次点击会再次弹出，点击确定后，再次点击不再弹出");
    }
}