package com.exa.baselib.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.util.zip.Inflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

/**
 * @Author lsh
 * @Date 2023/9/15 10:14
 * @Description
 */
public abstract class BaseViewBindingActivity<T extends ViewBinding> extends AppCompatActivity {
    protected T bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentViewId());
    }

    protected abstract int setContentViewId();
}
