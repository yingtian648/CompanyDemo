package com.exa.systemui;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsetsController;

import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.baselib.utils.Utils;
import com.exa.systemui.databinding.SystemUiNavigationbarBinding;
import com.exa.systemui.minterface.IConfigChangedListener;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

/**
 * @Author lsh
 * @Date 2023/3/14 13:38
 * @Description
 */
public class NavigationBarView implements View.OnClickListener, MCommandQueue.Callbacks, IConfigChangedListener {
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

    public NavigationBarView(Context context, UiModeManager modeManager, int displayId) {
        mContext = context;
        mUiModeManager = modeManager;
        mDisplayId = displayId;
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
//                openApp("com.gxatek.cockpit.car.settings");
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName("com.douyu.xl.douyucar","com.douyu.vehicle.splashpage.SplashActivity"));
                mContext.startActivity(intent);
                break;
            case R.id.btnEngine:
                String[] engines = mContext.getResources()
                        .getStringArray(com.exa.baselib.R.array.engine_mode_pkgs);
                for (String item : engines) {
                    if (Utils.openApp(mContext, item)) {
                        break;
                    }
                }
                break;
            case R.id.btnDemo:
                openApp("com.exa.companydemo");
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        updateIconColor();
    }
}
