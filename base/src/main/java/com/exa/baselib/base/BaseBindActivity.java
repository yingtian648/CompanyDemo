package com.exa.baselib.base;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.exa.baselib.utils.OnClickViewListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public abstract class BaseBindActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T bind;
    protected AppCompatActivity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        super.onCreate(savedInstanceState);
        activity = this;
        bind = DataBindingUtil.setContentView(this, setContentViewLayoutId());
        initView();
        initData();
    }

    protected void setToolbarId(int id) {
        Toolbar toolbar = findViewById(id);
        toolbar.setNavigationOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                finish();
            }
        });
    }

    protected abstract int setContentViewLayoutId();

    protected abstract void initView();

    protected abstract void initData();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }
}
