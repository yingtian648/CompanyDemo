package com.exa.baselib.base;

import android.os.Bundle;
import android.view.View;

import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.dd();
        setContentView(getLayoutId());
        initView();
        initData();
    }

    protected void setToolbarId(int id) {
        Toolbar toolbar = findViewById(id);
        toolbar.setNavigationOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                onBackPressed();
            }
        });
    }

    protected abstract void initData();

    protected abstract void initView();

    protected abstract int getLayoutId();
}
