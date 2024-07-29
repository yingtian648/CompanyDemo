package com.exa.companydemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.exa.baselib.utils.L;
import com.exa.baselib.utils.SystemBarUtil;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @Author lsh
 * @Date 2023/10/16 11:36
 * @Description
 */
public class TestDialog {
    private static final String TAG = "TestDialog";
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
        TextView titleT = view.findViewById(R.id.titleT);
        titleT.setText("TestDialog.showDialog");
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
                SystemBarUtil.hideStatusBars(dialog.getWindow());
            } else {
                SystemBarUtil.showStatusBars(dialog.getWindow());
            }
        });

//        setAlertDialogWindowAttrs(dialog.getWindow());

//        hideSystemBars(view);

        dialog.setContentView(view);
        dialog.setTitle("TestDialog.showDialog");
        dialog.show();
    }

    public static void hideSystemBars(View view) {
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(visibility);
    }

    public static void showSystemBars(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    public static void showAlertDialog(Context context) {
        L.dd();
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("TestDialog.showAlertDialog")
                .setMessage("showAlertDialogshowAlertDialogshowAlertDialogshowAlertDialog")
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
            L.dd("MyDialog 22");
            setContentView(R.layout.dialog_layout);
            setWindowAttrs();
            setCanceledOnTouchOutside(true);
            initView();
//            ScreenUtils.hideStatusBars(getWindow());
        }

        private void initView() {
            Button sureBtn = findViewById(R.id.sure_button);
            Button cancelBtn = findViewById(R.id.cancel_button);
            TextView titleT = findViewById(R.id.titleT);
            titleT.setText(title);
            sureBtn.setOnClickListener(v -> dismiss());
            cancelBtn.setOnClickListener(v -> cancel());
            findViewById(R.id.switch_button).setOnClickListener(v -> {
                L.d("点击开关按钮");
                isHide = !isHide;
                if (isHide) {
                    SystemBarUtil.hideStatusBars(getWindow());
                } else {
                    SystemBarUtil.showStatusBars(getWindow());
                }
            });
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

        @Override
        public void cancel() {
            L.dd("cancel111");
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

    private static final String content = "1.一二三四五六七八九十一二三四五六七八九十\n"
            + "2.一二三四五六七八九十一二三四五六七八九十\n"
            + "3.一二三四五六七八九十一二三四五六七八九十\n"
            + "4.一二三四五六七八九十一二三四五六七八九十\n"
            + "5.一二三四五六七八九十一二三四五六七八九十";


    private static DialogFragment df;

    public static void showDialogFragment(FragmentActivity activity) {
        if (df == null) {
            L.dd("getDialog().init()");
            df = new MyDialogFragment(R.layout.dialog_layout);
            df.show(activity.getSupportFragmentManager(), "showDialogFragment");
        } else {
            L.dd("getDialog().show()");
            Objects.requireNonNull(df.getDialog()).show();
        }
    }

    public static class MyDialogFragment extends DialogFragment {
        private TextView tvContent;

        public MyDialogFragment() {
        }

        public MyDialogFragment(int contentLayoutId) {
            super(contentLayoutId);
            L.dd();
        }

        @SuppressLint("ClickableViewAccessibility")
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            L.dd();
            Dialog dialog = super.onCreateDialog(savedInstanceState);

            // 设置点击外部对话框不会关闭
//            dialog.setCanceledOnTouchOutside(false);

            // 获取布局视图
            View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
            dialog.setContentView(view);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            dialog.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        L.dd("处理界面外点击事件");
                        // 处理外部点击事件
                        // 例如，可以关闭对话框
                        dialog.hide();
                        return true;
                    }
                    return false;
                }
            });
            return dialog;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            L.dd();
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            L.dd();
            initView(view);
        }

        @Override
        public void onCancel(@NonNull DialogInterface dialog) {
            L.dd();
        }

        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {
            L.dd();
        }

        private void initView(View view) {
            tvContent = view.findViewById(R.id.edt);
            tvContent.setText(content);

            view.findViewById(R.id.cancel_button).setOnClickListener(v -> {
                Objects.requireNonNull(getDialog()).hide();
            });
            view.findViewById(R.id.sure_button).setOnClickListener(v -> {
                Objects.requireNonNull(getDialog()).hide();
            });
            Objects.requireNonNull(getDialog()).setOnCancelListener(dialog -> L.dd("OnCancel"));
            Objects.requireNonNull(getDialog()).setOnDismissListener(dialog -> L.dd("OnDismiss"));
        }
    }
}
