package com.exa.companydemo.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * @作者 刘世华
 * @创建时间 2021-2-1 15:05
 */
public abstract class BaseFragment extends Fragment {
    //判断是否已进行过加载，避免重复加载
    private boolean isLoad=false;

    protected abstract View setContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setContentView(inflater,container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        lazyLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoad=false; //当view销毁时需要把isLoad置为false
    }

    private void lazyLoad() {
        if (!isLoad){
            isLoad=true;
            initData();
        }
    }

    /**
     * 初始化控件
     * @param view
     */
    protected abstract void initView(View view);
    /**
     * 初始化数据
     */
    protected abstract void initData();
}
