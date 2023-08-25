package com.exa.companydemo;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.companydemo.databinding.ActivityTestBinding;
import com.exa.companydemo.test.TabFm;
import com.exa.companydemo.test.TabSurfaceViewFm;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TestActivity extends BaseBindActivity<ActivityTestBinding> {

    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        fragmentList.add(new TabFm("我是第1个Fm"));
        fragmentList.add(new TabFm("我是第2个Fm"));
        fragmentList.add(new TabFm("我是第3个Fm"));
        fragmentList.add(new TabSurfaceViewFm());
        bind.vp.setAdapter(new MAdapter(getSupportFragmentManager()));
        bind.vp.setOffscreenPageLimit(4);
        bind.btn1.setOnClickListener(v -> {
            bind.vp.setCurrentItem(0);
        });
        bind.btn2.setOnClickListener(v -> {
            bind.vp.setCurrentItem(1);
        });
        bind.btn3.setOnClickListener(v -> {
            bind.vp.setCurrentItem(2);
        });
        bind.btn4.setOnClickListener(v -> {
            bind.vp.setCurrentItem(3);
        });
    }

    class MAdapter extends FragmentPagerAdapter {

        public MAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "Fragment_" + position;
        }
    }
}