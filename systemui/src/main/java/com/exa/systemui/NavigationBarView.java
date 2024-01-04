package com.exa.systemui;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsetsController;

import com.android.internal.view.AppearanceRegion;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Utils;
import com.exa.systemui.databinding.SystemUiNavigationbarBinding;
import com.exa.systemui.minterface.IConfigChangedListener;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

/**
 * @Author lsh
 * @Date 2023/3/14 13:38
 * @Description
 */
public class NavigationBarView implements View.OnClickListener, MCommandQueue.Callback, IConfigChangedListener {
    private final Context mContext;
    private final View mRootView;
    private UiModeManager mUiModeManager;
    private int mNightMode = -1;
    protected SystemUiNavigationbarBinding mBind;
    private final int mDisplayId;
    private int mAppearance = 0;
    private final int[] ids = {
            R.id.btnHome,
            R.id.btnMyCar,
            R.id.btnEngine,
            R.id.btnDemo,
    };

    public View getRootView() {
        return mRootView;
    }

    public NavigationBarView(Context context, UiModeManager modeManager,
                             MCommandQueue commandQueue, int displayId) {
        mContext = context;
        mUiModeManager = modeManager;
        mDisplayId = displayId;
        commandQueue.registerCallback(this);
        mBind = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.system_ui_navigationbar, null, false);
        mRootView = mBind.getRoot();
        initView();
    }

    private void initView() {
        for (int id : ids) {
            mRootView.findViewById(id).setOnClickListener(this);
        }
        updateIconColor();
    }

    @Override
    public void onSystemBarAppearanceChanged(int displayId, int appearance, AppearanceRegion[] appearanceRegions, boolean navbarColorManagedByIme) {
        // 设置白天黑夜模式对应的背景色
        if (displayId != mDisplayId) return;
        // 响应沉浸式-亮色状态栏-亮色导航栏 8=亮色，0=非亮色
        if (mAppearance != appearance) {
            mAppearance = appearance;
            updateIconColor();
        }
    }

    private void updateIconColor() {
        boolean showLight = false;
        mNightMode = mUiModeManager.getNightMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            showLight = mAppearance == WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
        }
        int bgColor = showLight ? R.color.icon_bg_light :
                (isNightMode() ? R.color.icon_bg_night : R.color.icon_bg);
        int textColor = showLight ? R.color.black :
                (isNightMode() ? R.color.black : R.color.white);
        mBind.btnHome.setBackgroundColor(mContext.getColor(bgColor));
        mBind.btnEngine.setBackgroundColor(mContext.getColor(bgColor));
        mBind.btnMyCar.setBackgroundColor(mContext.getColor(bgColor));
        mBind.btnDemo.setBackgroundColor(mContext.getColor(bgColor));

        mBind.btnHome.setTextColor(mContext.getColor(textColor));
        mBind.btnEngine.setTextColor(mContext.getColor(textColor));
        mBind.btnMyCar.setTextColor(mContext.getColor(textColor));
        mBind.btnDemo.setTextColor(mContext.getColor(textColor));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHome:
                goHome();
                break;
            case R.id.btnMyCar:
                Utils.openApp(mContext, "com.gxatek.cockpit.car.settings");
                break;
            case R.id.btnEngine:
                String[] apps = mContext.getResources()
                        .getStringArray(com.exa.baselib.R.array.engine_mode_pkgs);
                for (String item : apps) {
                    if (Utils.openApp(mContext, item)) {
                        return;
                    }
                }
                break;
            case R.id.btnDemo:
                Utils.openApp(mContext, "com.exa.companydemo");
                break;
            default:
                break;
        }
    }

    private boolean isNightMode() {
        return UiModeManager.MODE_NIGHT_YES == mNightMode;
    }

    private void goHome() {
        try {
            Intent mIntent = new Intent();
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntent.setAction(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mContext.startActivity(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("goHome err", e);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        updateIconColor();
    }
}
