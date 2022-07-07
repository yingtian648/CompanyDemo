package com.exa.baselib.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseBindFragment<T extends ViewDataBinding> extends BaseFragment {
    protected T bind;

    @Override
    protected View setContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        bind = DataBindingUtil.inflate(inflater, setContentLayoutId(), container, false);
        return bind.getRoot();
    }

    abstract int setContentLayoutId();
}
