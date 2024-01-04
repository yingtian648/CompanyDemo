package com.exa.systemui;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;

import com.exa.baselib.utils.DateUtil;
import com.exa.baselib.utils.L;
import com.exa.systemui.databinding.SystemUiStatusbarBinding;
import com.exa.systemui.minterface.IConfigChangedListener;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

/**
 * @Author lsh
 * @Date 2023/3/20 17:02
 * @Description
 */
public class StatusBarView implements View.OnClickListener, IConfigChangedListener {
    private final String TAG = "StatusBarView";
    private final Context mContext;
    private final View mRootView;
    private final UiModeManager mUiModeManager;
    private int mNightMode = -1;
    protected SystemUiStatusbarBinding mBind;
    // 是否亮色状态栏
    private boolean isAppearance = false;
    // 是否沉浸式
    private boolean isImmersive = false;
    private final int[] ids = {
            R.id.btnUser,
            R.id.btnMail,
            R.id.btnWifi,
    };
    private int mDisplayId;

    public View getRootView() {
        return mRootView;
    }

    public StatusBarView(Context context, UiModeManager modeManager,  int displayId) {
        mContext = context;
        mUiModeManager = modeManager;
        mDisplayId = displayId;
        mBind = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.system_ui_statusbar, null, false);
        mRootView = mBind.getRoot();
        initView();
        registerTimeUpdateReceiver();
        updateIconColor();
    }

    private boolean isNightMode() {
        return UiModeManager.MODE_NIGHT_YES == mNightMode;
    }

    @SuppressLint("WrongConstant")
    private void updateIconColor() {
        L.d(TAG, "updateIconColor " + isNightMode());
        if (isNightMode()) {
            mBind.btnMail.setImageResource(R.drawable.mail_white);
            mBind.btnUser.setImageResource(R.drawable.user_white);
            mBind.btnWifi.setImageResource(R.drawable.wifi_white);
            mBind.tvTime.setTextColor(mContext.getColor(R.color.white));
        } else {
            mBind.btnMail.setImageResource(R.drawable.mail_black);
            mBind.btnUser.setImageResource(R.drawable.user_black);
            mBind.btnWifi.setImageResource(R.drawable.wifi_black);
            mBind.tvTime.setTextColor(mContext.getColor(R.color.black));
        }
        mNightMode = mUiModeManager.getNightMode();

    }

    private void initView() {
        for (int id : ids) {
            mRootView.findViewById(id).setOnClickListener(this);
        }
        updateTimeUi();
    }

    @Override
    public void onClick(View v) {

    }

    private void registerTimeUpdateReceiver() {
        L.d(TAG, L.getCurrentMethodName());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        mContext.registerReceiver(mTimeUpdateReceiver, filter);
    }

    private final BroadcastReceiver mTimeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (action == null || action.isEmpty()) return;
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                //系统每1分钟发送一次广播
                updateTimeUi();
            } else if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                //系统手动更改时间发送广播
                updateTimeUi();
            }
        }
    };

    private void updateTimeUi() {
        mBind.tvTime.setText(DateUtil.getNowDateHM());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        updateIconColor();
    }
}
