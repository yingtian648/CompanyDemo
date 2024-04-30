package com.exa.lsh.library;

import android.view.View;

/**
 * 防止快速点击导致重复打开弹框/界面
 */
public abstract class OnSafeClickListener implements View.OnClickListener{
    private long lastClickTime;
    private long timeInterval = 800L;//有效点击间隔时长（默认点击之后800毫秒内点击无效）

    public OnSafeClickListener() {

    }

    /**
     * 构造方法
     * @param timeInterval 有效时间间隔
     */
    public OnSafeClickListener(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - lastClickTime > timeInterval) {
            lastClickTime = nowTime;
            onClickView(v);
        }
    }

    public abstract void onClickView(View v);
}
