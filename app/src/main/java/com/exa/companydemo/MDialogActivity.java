package com.exa.companydemo;

import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.SystemBarUtil;
import com.exa.companydemo.databinding.ActivityMDialogBinding;

/**
 * @Author lsh
 * @Date 2023/11/1 17:52
 * @Description
 */
public class MDialogActivity extends BaseBindActivity<ActivityMDialogBinding> {

    private boolean isShow = false;
    int x;

    @Override
    protected int setContentViewLayoutId() {
        return R.layout.activity_m_dialog;
    }

    @Override
    protected void initView() {
        Handler mHandler = new Handler();
        getResources().getConfiguration().hardKeyboardHidden =
                android.content.res.Configuration.HARDKEYBOARDHIDDEN_YES;
        bind.cancelButton.setOnClickListener(v -> finish());
        bind.sureButton.setOnClickListener(v -> finish());
        bind.switchButton.setOnClickListener(v -> {
            isShow = !isShow;
            if (isShow) {
                SystemBarUtil.showStatusBars(this);
            } else {
                SystemBarUtil.hideStatusBars(this);
            }
        });
    }

    @Override
    protected void initData() {
        setAlertDialogWindowAttrs(getWindow());
    }

    private static void setAlertDialogWindowAttrs(Window window) {
        L.dd("77");
        window.setElevation(0);
//        window.setBackgroundDrawableResource(R.color.permission_bg);
        WindowManager.LayoutParams attrs = window.getAttributes();
//        attrs.format = PixelFormat.TRANSLUCENT;
        window.setAttributes(attrs);
//        window.setDimAmount(0);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        View decor = window.getDecorView();
        int vis = decor.getSystemUiVisibility();
        decor.setSystemUiVisibility(vis
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}
