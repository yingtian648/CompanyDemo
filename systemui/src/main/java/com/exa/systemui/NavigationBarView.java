package com.exa.systemui;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;

import com.android.internal.view.AppearanceRegion;
import com.exa.baselib.utils.L;
import com.exa.systemui.R;
import com.exa.systemui.databinding.SystemUiNavigationbarBinding;

import androidx.databinding.DataBindingUtil;

/**
 * @Author lsh
 * @Date 2023/3/14 13:38
 * @Description
 */
public class NavigationBarView implements View.OnClickListener, MCommandQueue.Callback {
    private final Context mContext;
    private final View mRootView;
    private UiModeManager mUiModeManager;
    private int mNightMode;
    private MCommandQueue mCommandQueue;
    protected SystemUiNavigationbarBinding mBind;
    private int mDisplayId;
    private int mAppearance = 0;
    private final int[] ids = {
            R.id.btnHome,
            R.id.btnMyCar,
            R.id.btnEngine,
            R.id.btnUpgrade,
    };

    public View getRootView() {
        return mRootView;
    }

    public NavigationBarView(Context context, UiModeManager modeManager,
                             MCommandQueue commandQueue, int displayId) {
        mContext = context;
        mUiModeManager = modeManager;
        mCommandQueue = commandQueue;
        mDisplayId = displayId;
        mNightMode = modeManager.getNightMode();
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
        updateBackgroundColor();
    }

    private void updateBackgroundColor() {
        if (isNightMode()) {
            mBind.getRoot().setBackgroundColor(mContext.getColor(R.color.nav_bg_night));
        } else {
            mBind.getRoot().setBackgroundColor(mContext.getColor(R.color.nav_bg));
        }
    }

    @Override
    public void onSystemBarAppearanceChanged(int displayId, int appearance, AppearanceRegion[] appearanceRegions, boolean navbarColorManagedByIme) {
        // 设置白天黑夜模式对应的背景色
        if (displayId != mDisplayId) return;
        mNightMode = mUiModeManager.getNightMode();
        if (mNightMode != mUiModeManager.getNightMode()) {
            updateBackgroundColor();
        }
        // 响应沉浸式-亮色状态栏-亮色导航栏 8=亮色，0=非亮色
        if (mAppearance != appearance) {
            mAppearance = appearance;
            updateAppearance();
        }
    }

    private void updateAppearance() {
        boolean showLight = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            showLight = mAppearance == WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
        }
        if (showLight) {
            mBind.btnHome.setTextColor(Color.WHITE);
            mBind.btnEngine.setTextColor(Color.WHITE);
            mBind.btnMyCar.setTextColor(Color.WHITE);
            mBind.btnUpgrade.setTextColor(Color.WHITE);
        } else {
            mBind.btnHome.setTextColor(Color.BLACK);
            mBind.btnEngine.setTextColor(Color.BLACK);
            mBind.btnMyCar.setTextColor(Color.BLACK);
            mBind.btnUpgrade.setTextColor(Color.BLACK);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHome:
                openApp("com.exa.companydemo");
                break;
            case R.id.btnMyCar:
                openApp("com.gxatek.cockpit.car.settings");
                break;
            case R.id.btnEngine:
                openApp("com.android.engmode");
                break;
            case R.id.btnUpgrade:
                openApp("com.desaysv.ivi.vds.upgrade");
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
            mIntent.setClassName("com.gxatek.cockpit.launcher", "com.gxatek.cockpit.launcher.CarLauncher");
            mContext.startActivity(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
            L.e("goHome err", e);
        }
    }

    private void openApp(String packageName) {
        if (packageName != null) {
            try {
                PackageManager packageManager = mContext.getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    mContext.startActivity(intent);
                } else {
                    L.e(String.format("openApp err: has not found %s launcher activity", packageName));
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e("openApp err", e);
            }
        }
    }
}
