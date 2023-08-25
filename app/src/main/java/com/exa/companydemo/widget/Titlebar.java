package com.exa.companydemo.widget;

import android.app.UiModeManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author lsh
 * @Date 2023/6/28 14:22
 * @Description
 */
public class Titlebar extends FrameLayout {

    private final int BACK_DAY_RES = com.exa.baselib.R.drawable.arrow_back_black;
    private final int BACK_NIGHT_RES = com.exa.baselib.R.drawable.arrow_back_white;

    private int mBackgroundColor = Color.WHITE;
    private int mBackgroundNightColor = Color.BLACK;
    private int mTextColor = Color.BLACK;
    private int mTextColorNight = Color.WHITE;

    private View.OnClickListener mNavigationClickListener;
    /**
     * 是否透明背景
     */
    private boolean mTranslucent = true;
    private int mTitleSize = 28;
    private int mSubTitleSize = 28;
    private int mGravity = Gravity.CENTER_VERTICAL;
    private ImageButton mBackIb;
    private TextView mTitleTv;
    private TextView mSubTitleTv;
    private UiModeManager mUiModeManager;

    private int mHeight = 100;
    private int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;

    public Titlebar(Context context) {
        this(context, null);
    }

    public Titlebar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Titlebar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mUiModeManager = (UiModeManager) getContext().getSystemService(Context.UI_MODE_SERVICE);
        setPadding(20, 20, 20, 20);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBackIb = new ImageButton(getContext());
        mBackIb.setBackground(null);
        mBackIb.setOnClickListener(v -> {
            if (mNavigationClickListener != null) {
                mNavigationClickListener.onClick(v);
            }
        });
        mBackIb.setImageResource(BACK_DAY_RES);
        mTitleTv = new TextView(getContext());
        mSubTitleTv = new TextView(getContext());
        mBackIb.setLayoutParams(params);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
        params.setMargins(60, 0, 0, 0);
        mSubTitleTv.setLayoutParams(params);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mTitleTv.setLayoutParams(params);

        mTitleTv.setTextSize(mTitleSize);
        mTitleTv.setMaxLines(1);
        mTitleTv.setEllipsize(TextUtils.TruncateAt.END);
        mTitleTv.setAllCaps(false);
        mTitleTv.setTextColor(mTextColor);
        mSubTitleTv.setText("返回");
        mSubTitleTv.setTextSize(mSubTitleSize);
        mSubTitleTv.setMaxLines(1);
        mSubTitleTv.setEllipsize(TextUtils.TruncateAt.END);
        mSubTitleTv.setAllCaps(false);
        mSubTitleTv.setTextColor(mTextColor);
        mSubTitleTv.setOnClickListener(v -> {
            if (mNavigationClickListener != null) {
                mNavigationClickListener.onClick(v);
            }
        });

        addView(mBackIb);
        addView(mSubTitleTv);
        addView(mTitleTv);

        setLayoutParams(new ViewGroup.LayoutParams(mWidth,
                mHeight));
        setGravity(mGravity);
        onConfigurationChanged();
    }

    public void onConfigurationChanged() {
        boolean isNight = mUiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES;
        if (mTranslucent) {
            setBackground(null);
        }
        if (isNight) {
            if (!mTranslucent) {
                setBackgroundColor(mBackgroundNightColor);
            }
            mBackIb.setImageResource(BACK_NIGHT_RES);
            mTitleTv.setTextColor(mTextColorNight);
            mSubTitleTv.setTextColor(mTextColorNight);
        } else {
            if (!mTranslucent) {
                setBackgroundColor(mBackgroundColor);
            }
            mBackIb.setImageResource(BACK_DAY_RES);
            mTitleTv.setTextColor(mTextColor);
            mSubTitleTv.setTextColor(mTextColor);
        }
    }

    public void setTranslucent(boolean translucent) {
        if (translucent != mTranslucent) {
            mTranslucent = translucent;
            onConfigurationChanged();
        }
    }

    public void setTitle(@NonNull String title) {
        mTitleTv.setText(title);
    }

    public void setSubTitle(@NonNull String title) {
        mSubTitleTv.setText(title);
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        super.setBackgroundColor(mBackgroundColor);
    }

    public void setNavigationOnClickListener(View.OnClickListener clickListener) {
        this.mNavigationClickListener = clickListener;
    }

    public void setTitleSize(int mTitleSize) {
        this.mTitleSize = mTitleSize;
    }

    public void setSubTitleSize(int mSubTitleSize) {
        this.mSubTitleSize = mSubTitleSize;
    }

    public void setGravity(int mGravity) {
        this.mGravity = mGravity;
    }
}
