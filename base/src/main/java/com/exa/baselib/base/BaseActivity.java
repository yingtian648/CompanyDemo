package com.exa.baselib.base;

import android.os.Bundle;
import android.view.View;

import com.exa.baselib.R;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.translate_y_enter,R.anim.translate_y_exit);
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
                if(getClass().getSimpleName().contains("MainActivity")){
                    System.exit(0);
                }
            }
        });
    }

    protected abstract void initData();

    protected abstract void initView();

    protected abstract int getLayoutId();
}
