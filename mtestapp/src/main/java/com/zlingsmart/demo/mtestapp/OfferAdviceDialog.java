/*
 * Copyright (C) 2022 Baidu, Inc. All Rights Reserved.
 */
package com.zlingsmart.demo.mtestapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * @author 帮助与建议dialog
 */
public class OfferAdviceDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    private TextView contentTv;
    private TextView voiceText;
    private TextView voiceChangeTitle;
    private TextView oneText;
    private ImageView talkBtn;
    private TextView titleTop;
    private String positiveName;
    private String negativeName;

    public OfferAdviceDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_mycar);
        setCanceledOnTouchOutside(true);
        initView();
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setDimAmount(0f);

    }

    protected void initView() {
        Button cancelBtn = findViewById(R.id.ad_btn_cancel);
        Button confirmBtn = findViewById(R.id.ad_btn_confirm);
        FrameLayout rootDialog = findViewById(R.id.root_dialog);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        rootDialog.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            WindowInsetsController windowInsetsController = getWindow().getDecorView().getWindowInsetsController();
//            windowInsetsController.hide(WindowInsets.Type.statusBars());
//            windowInsetsController.hide(WindowInsets.Type.navigationBars());
//        }
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ad_btn_cancel) {
            dismiss();
        } else if (id == R.id.ad_btn_confirm) {
            dismiss();
        } else if (id == R.id.root_dialog) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
