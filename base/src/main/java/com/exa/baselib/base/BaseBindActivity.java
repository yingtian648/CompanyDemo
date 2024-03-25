package com.exa.baselib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public abstract class BaseBindActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T bind;
    protected AppCompatActivity activity;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        overridePendingTransition(R.anim.translate_y_enter,R.anim.translate_y_exit);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            L.d(getClass().getSimpleName() + " show on display=" + getDisplay().getDisplayId());
        }
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        activity = this;
        bind = DataBindingUtil.setContentView(this, setContentViewLayoutId());
        //用于启动新的Activity并获取返回值，用于替换StartActivityForResult
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);
        initView();
        onViewReady();
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

    protected void startActivity(Class clazz) {
        try {
            launcher.launch(new Intent(this, clazz));
        } catch (Exception e) {
            e.printStackTrace();
            L.e(getClass().getSimpleName() + ".startActivity err", e);
        }
    }

    protected void onActivityResult(ActivityResult result) {
        L.d("onActivityResult: " + result);
    }

    protected abstract int setContentViewLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected void onViewReady() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
        launcher.unregister();
    }
}
