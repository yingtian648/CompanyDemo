package com.exa.companydemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.exa.companydemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShareVideoDialog extends Dialog {
    private static final String TAG = "ShareVideoDialog";

    private CompoundButton.OnCheckedChangeListener topDisplayCheckListener;
    private CompoundButton.OnCheckedChangeListener mainDisplayCheckListener;

    public ShareVideoDialog(@NonNull Context context) {
        super(context, R.style.ShareDialogStyle);
        initView();
    }

    public ShareVideoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShareVideoDialog(@NonNull Context context, boolean cancelable,
            @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    private void initView() {

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.dimAmount = 0.15F;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(layoutParams);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);
        setContentView(viewDialog);

        setMainDisplayEnable(true);
        setTopDisplayEnable(true);

        viewDialog.findViewById(R.id.sure_button).setOnClickListener(v -> dismiss());
        viewDialog.findViewById(R.id.cancel_button).setOnClickListener(v -> dismiss());
    }

    public void setTopDisplayCheckListener(CompoundButton.OnCheckedChangeListener topDisplayCheckListener) {
        this.topDisplayCheckListener = topDisplayCheckListener;
    }


    public void setMainDisplayCheckListener(CompoundButton.OnCheckedChangeListener mainDisplayCheckListener) {
        this.mainDisplayCheckListener = mainDisplayCheckListener;
    }


    public void setMainDisplayEnable(boolean enable) {
    }

    public void setTopDisplayEnable(boolean enable) {
    }
}
