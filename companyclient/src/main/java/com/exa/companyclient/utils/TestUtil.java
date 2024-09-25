package com.exa.companyclient.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.SystemBarUtil;
import com.exa.companyclient.R;

import androidx.annotation.NonNull;

/**
 * @author lsh
 * @date 2024/9/18 11:41
 * @description
 */
public class TestUtil {
    private MyDialog dialog;

    public void showDialog(Context context) {
        dialog = new MyDialog(context, R.style.AllMenu);
        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private static class MyDialog extends Dialog {

        public MyDialog(@NonNull Context context) {
            this(context, 0);
        }

        public MyDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_layout_full);
            setWindowAttrs();
            setOnShowListener(dialog -> {
                L.d("onshow");
                SystemBarUtil.hideOnlyStatusBars(getWindow());
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            });
        }

        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                L.dd("点击去弹框之外区域");
            }
            return super.onTouchEvent(event);
        }

        @Override
        public void cancel() {
            L.dd("cancel");
        }

        /* access modifiers changed from: protected */
        @SuppressLint("WrongConstant")
        private Window setWindowAttrs() {
            Window window = getWindow();
//            window.getDecorView().clearAnimation();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes == null) {
                return window;
            }
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;//WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.MATCH_PARENT;// WindowManager.LayoutParams.WRAP_CONTENT;
            attributes.gravity = Gravity.CENTER;
//            attributes.format = PixelFormat.TRANSLUCENT;
//            attributes.dimAmount = 0f;
            attributes.flags = attributes.flags
                    | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    // 弹出后不会抢window焦点 有此Flag的dialog在AH8上会显示在shortcut上面
//                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION
            ;
            attributes.setTitle("TestUtil_Dialog");
            attributes.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
            window.setAttributes(attributes);
            return window;
        }
    }
}
