package com.exa.companydemo.test;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/**
 * @author n027087
 */
public class DialogFragmentTest extends DialogFragment {

    private static final String TAG = "Dialog_intentFragment";

    private static final int WINDOW_TYPE = 2508;
    private static final float AMBIGUITY_LEVEL = 0.6f;
    private Window window;
    private View view;
    private Button sureBtn;
    private Button cancelBtn;

    public DialogFragmentTest() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setWindowAttrs();
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // 设置点击对话框外部区域不会使对话框消失
        dialog.setCanceledOnTouchOutside(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_layout, container);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialog);
        L.dd();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.dd();
        sureBtn = view.findViewById(R.id.sure_button);
        cancelBtn = view.findViewById(R.id.cancel_button);
        sureBtn.setOnClickListener(v -> {
            dismiss();
        });
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });
        setWindowAttrs();
    }

    @Override
    public void onStart() {
        super.onStart();
        L.dd();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        L.dd();
    }

    private void setWindowAttrs() {
        window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_sound_bg);
        window.getAttributes().width = Tools.getScreenW(getContext());
        window.getAttributes().height = Tools.getScreenH(getContext());
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        window.setGravity(Gravity.CENTER);
        window.setDimAmount(AMBIGUITY_LEVEL);
        hideSystemBar(window);
    }


    public static void hideSystemBar(final Window window) {
        View decorView = window.getDecorView();
        int vis = decorView.getSystemUiVisibility();
        int option = (View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        decorView.setSystemUiVisibility(vis | option);
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        L.d(TAG, "AdigoSoundDialog onDismiss");
    }
}
