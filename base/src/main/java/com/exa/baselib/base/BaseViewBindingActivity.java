package com.exa.baselib.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

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
        bind = getViewBinding();
        setContentView(bind.getRoot());
        initView();
        initData();
    }

    /**
     * 反射获取viewbinding
     */
    private void initBind() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        assert type != null;
        Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            bind = (T) inflate.invoke(null, getLayoutInflater());
            setContentView(bind.getRoot());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract T getViewBinding();
}
