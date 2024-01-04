package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Logs;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.baselib.utils.ScreenUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author lsh
 * @Date 2023/10/16 11:36
 * @Description
 */
public class TestDialog {
    private static boolean isHide = false;
    private static WindowManager.LayoutParams mParams, mCardListParams;

    public static void showDialog(Activity context) {
        View decor = context.getWindow().getDecorView();
        // 设置Activity的DecorView模糊效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // radiusX,radiusY模糊半径，值越大越模糊
//            decor.setRenderEffect(RenderEffect.createBlurEffect(5F, 5F, Shader.TileMode.CLAMP));
        }
        /**
         * 未加载style的时候——没有半透明背景
         */
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null, false);
//        final Dialog dialog = new Dialog(context);
        final Dialog dialog = new Dialog(context, R.style.MyAlertDialog);
        dialog.setCancelable(true);
        view.findViewById(R.id.sure_button).setOnClickListener(v -> {
            dialog.dismiss();
            // 设置Activity的DecorView模糊效果（取消）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                decor.setRenderEffect(null);
            }
        });
        view.findViewById(R.id.cancel_button).setOnClickListener(v -> {
            dialog.dismiss();
            // 设置Activity的DecorView模糊效果（取消）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                decor.setRenderEffect(null);
            }
        });
        view.findViewById(R.id.switch_button).setOnClickListener(v -> {
            isHide = !isHide;
            if (isHide) {
                ScreenUtils.hideStatusBars(dialog.getWindow());
            } else {
                ScreenUtils.showStatusBars(dialog.getWindow());
            }
        });

//        setAlertDialogWindowAttrs(dialog.getWindow());

        dialog.setContentView(view);
        dialog.setTitle("CompanyDemo_showAlertDialog");
        dialog.show();
    }

    public static void showAlertDialog(Context context) {
        L.dd("isActivity 111:" + (context instanceof Activity));
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("测试标题")
                .setMessage("测试内容")
                .create();
        dialog.show();
        Context context1 = dialog.getContext();
        L.dd("isActivity 222:" + (context1 instanceof Activity));

    }

    private static void setAlertDialogWindowAttrs(Window window) {
        WindowManager.LayoutParams attrs = new WindowManager.LayoutParams();
        attrs.format = PixelFormat.TRANSPARENT;
        window.setAttributes(attrs);
        window.addFlags(WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
        );
        window.setNavigationBarColor(Color.YELLOW);
    }

    @SuppressLint("WrongConstant")
    public static void showLayout(Context context) {
        L.dd();
        mParams = new WindowManager.LayoutParams();

        mCardListParams = new WindowManager.LayoutParams();
        mCardListParams.format = -3;
        mCardListParams.type = 2;
        mCardListParams.flags |= -2138832824;
        mCardListParams.setTitle("launcher_desktop_cardlist");
        mCardListParams.height = 600;
        mCardListParams.width = 400;
        mCardListParams.gravity = 51;
        mCardListParams.x = 200;
        mCardListParams.y = 400;

        mParams.format = -3;
        mParams.type = 1999;
        mParams.flags |= 8;
        mParams.height = 116;
        mParams.width = 116;
        mParams.x = 640;
        mParams.y = 846;
        mParams.gravity = 83;
        mParams.setTitle("CompanyDemo_showLayout");
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout_full, null);
        manager.addView(view, mCardListParams);
    }

    public static void showBCallView(Context context) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        buildParams(params);
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null, false);
        Button cancel_button = view.findViewById(R.id.cancel_button);
        Button sure_button = view.findViewById(R.id.sure_button);
        view.setLayoutParams(params);
        cancel_button.setOnClickListener(v -> {
            windowManager.removeView(view);
        });
        windowManager.addView(view, params);
    }

    @SuppressLint("WrongConstant")
    private static void buildParams(WindowManager.LayoutParams mEcallLayoutParams) {
        boolean isGQA79P6125Channel = false;
        mEcallLayoutParams.setTitle("TestEBCall");
        if (isGQA79P6125Channel) {
            mEcallLayoutParams.type = 2021;
            mEcallLayoutParams.flags = 8651584;
        } else {
            mEcallLayoutParams.type = 2502;
            mEcallLayoutParams.flags = 2099232;
        }
        mEcallLayoutParams.width = -1;
        mEcallLayoutParams.height = -1;
        mEcallLayoutParams.gravity = 17;
        mEcallLayoutParams.format = -3;

        //2099232
        int flags = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

        int flagNew = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                ;
        mEcallLayoutParams.flags = flagNew;
        L.d("flags=" + flags);
    }


    public static void showMyDialog(Activity activity, String title, int windowType) {
        MyDialog dialog = new MyDialog(activity, R.style.MyDialog, windowType, title);
        dialog.show();
    }

    private static class MyDialog extends Dialog {
        private final int windowType;
        private final String title;

        public MyDialog(@NonNull Context context) {
            this(context, 0, -1, "normal");
        }

        public MyDialog(@NonNull Context context, int themeResId, int windowType, String title) {
            super(context, themeResId);
            this.windowType = windowType;
            this.title = title;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            L.dd("MyDialog 11");
            setContentView(R.layout.dialog_layout);
//            setWindowAttrs();
            setCanceledOnTouchOutside(true);
            initView();
            getWindow().getDecorView().setSystemUiVisibility(772);
//            ScreenUtils.hideOnlyStatusBars(getWindow());
        }

        private void initView() {
            Button sureBtn = findViewById(R.id.sure_button);
            Button cancelBtn = findViewById(R.id.cancel_button);
            TextView titleT = findViewById(R.id.titleT);
            titleT.setText(title);
            sureBtn.setOnClickListener(v -> dismiss());
            cancelBtn.setOnClickListener(v -> cancel());
            findViewById(R.id.switch_button).setOnClickListener(v -> {
                isHide = !isHide;
                if (isHide) {
                    ScreenUtils.hideStatusBars(getWindow());
                } else {
                    ScreenUtils.showStatusBars(getWindow());
                }
            });
        }

        @Override
        protected void onStart() {
            super.onStart();
            L.dd("MyDialog");
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            if (Build.VERSION.SDK_INT >= 28) {
                attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            attributes.setTitle("CompanyDemo_MyDialog");
            getWindow().setAttributes(attributes);
            // visible = 772
            int visible = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
//            getWindow().getDecorView().setSystemUiVisibility(visible);
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            L.dd("MyDialog");
//            getWindow().getDecorView().setSystemUiVisibility(772);
        }

        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                L.dd("点击去弹框之外区域");
            }
            return super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        @SuppressLint("WrongConstant")
        private Window setWindowAttrs() {
            Window window = getWindow();
            window.setStatusBarColor(Color.BLUE);
            window.setNavigationBarColor(Color.BLUE);
            window.getDecorView().clearAnimation();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes == null) {
                return window;
            }
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;//WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;// WindowManager.LayoutParams.WRAP_CONTENT;
            attributes.gravity = Gravity.CENTER;
            attributes.format = PixelFormat.TRANSLUCENT;
            attributes.dimAmount = 0f;
            attributes.flags = attributes.flags
                    | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    // 弹出后不会抢window焦点 有此Flag的dialog在AH8上会显示在shortcut上面
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            ;
            // 未设置FLAG_NOT_FOCUSABLE时，次标志可防止此窗口成为输入法的目标
            // 设置了FLAG_NOT_FOCUSABLE时，即使窗口不可聚焦，设置此标志也会请求将窗口作为输入法目标
            attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
            attributes.setTitle("MainActivity_Dialog");
//            attributes.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
//            attributes.type = 2;
            if (windowType > 2500) {
                attributes.type = windowType;//对应windowType
            }
            window.setAttributes(attributes);
            return window;
        }
    }
}
