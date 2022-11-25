package com.exa.companydemo.widget;

import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MyFrameLayout extends FrameLayout {

    private static final String TAG = "MyFrameLayout";

    public MyFrameLayout(@NonNull Context context) {
        super(context);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        Log.e(TAG, "dispatchApplyWindowInsets insets:" + insets);
        Insets statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars());
//        setPadding(0, statusBarInsets.top / 2, 0, 0);
        return WindowInsets.CONSUMED;
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        // setPadding(insets.getSystemWindowInsetLeft(),);
        return insets.consumeSystemWindowInsets();
    }
}
