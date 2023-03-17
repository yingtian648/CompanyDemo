package com.exa.systemui;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
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
    private final int[] ids = {
            R.id.BtnHome,
            R.id.BtnMyCar,
            R.id.BtnEngine,
            R.id.BtnUpgrade,
    };

    public View getRootView() {
        return mRootView;
    }

    public NavigationBarView(Context context, UiModeManager modeManager, MCommandQueue commandQueue) {
        mContext = context;
        mUiModeManager = modeManager;
        mCommandQueue = commandQueue;
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
        setBackgroundColor();
    }

    private void setBackgroundColor() {
        if (isNightMode()) {
            mBind.getRoot().setBackgroundColor(mContext.getColor(R.color.nav_bg_night));
        } else {
            mBind.getRoot().setBackgroundColor(mContext.getColor(R.color.nav_bg));
        }
    }

    @Override
    public void onSystemBarAppearanceChanged(int displayId, int appearance, AppearanceRegion[] appearanceRegions, boolean navbarColorManagedByIme) {
        // 设置白天黑夜模式对应的背景色
        if (mNightMode != mUiModeManager.getNightMode()) {
            mNightMode = mUiModeManager.getNightMode();
            setBackgroundColor();
        }
        // 响应沉浸式-亮色状态栏-亮色导航栏 8=亮色，0=非亮色
        L.dd("appearance:" + appearance);
        
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnHome:
                openApp("com.exa.systemui");
                break;
            case R.id.BtnMyCar:
                openApp("com.gxatek.cockpit.car.settings");
                break;
            case R.id.BtnEngine:
                openApp("com.android.engmode");
                break;
            case R.id.BtnUpgrade:
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
