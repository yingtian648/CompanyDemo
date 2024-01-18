package com.exa.companydemo.common;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.base.adapter.BaseRecyclerAdapter;
import com.exa.baselib.base.adapter.OnClickItemListener;
import com.exa.baselib.bean.AppInfo;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.SystemBarUtil;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.Constants;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivityAppInfoBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppInfoActivity extends BaseBindActivity<ActivityAppInfoBinding> {

    private final List<AppInfo> dataList = new ArrayList<>();

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_app_info;
    }

    @Override
    protected void initView() {
        setToolbarId(R.id.toolbar);
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        bind.recyclerView.setAdapter(new BaseRecyclerAdapter<AppInfo>(this, R.layout.item_app_list, dataList, new OnClickItemListener() {
            @Override
            public void onClickItem(int position) {
                Tools.copyText(AppInfoActivity.this, dataList.get(position).packageName);
            }

            @Override
            public void onLongClickItem(int position) {

            }
        }) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onViewHolder(@NonNull View view, @NonNull AppInfo data, int position) {
                ImageView iv = view.findViewById(R.id.iv);
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                TextView tvVersion = view.findViewById(R.id.tvVersion);
                TextView tvPkg = view.findViewById(R.id.tvPkg);
                Button btn = view.findViewById(R.id.btn);
                btn.setOnClickListener(v -> {
                    Tools.startApp(AppInfoActivity.this, data.packageName);
                });
                try {
                    iv.setImageDrawable(data.iconDrawable);
                } catch (Exception e) {
                    L.e("err1", e);
                }
                tvTitle.setText(data.name + "");
                tvVersion.setText(data.versionCode + ", " + data.versionName);
                tvPkg.setText(data.packageName + "");
            }
        });
        SystemBarUtil.setInvasionSystemBars(this);
    }

    @Override
    protected void initData() {
        Constants.getSinglePool().execute(() -> {
            List<AppInfo> apps = Tools.getMobileAppsInfo(this, true);
            dataList.addAll(apps);
            updateList();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList() {
        bind.toolbar.post(() -> {
                    bind.loadBar.setVisibility(View.GONE);
                    Objects.requireNonNull(bind.recyclerView.getAdapter()).notifyDataSetChanged();
                }
        );
    }
}