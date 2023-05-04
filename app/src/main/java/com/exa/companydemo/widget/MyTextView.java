package com.exa.companydemo.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.fonts.FontFamilyUpdateRequest;
import android.util.AttributeSet;
import android.widget.TextView;

import com.exa.baselib.utils.L;

import androidx.annotation.Nullable;

/**
 * @Author lsh
 * @Date 2023/4/28 16:45
 * @Description
 */
public class MyTextView extends androidx.appcompat.widget.AppCompatTextView {
    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface typeface = getTypeface();
        L.dd("typeface:" + typeface.getStyle());
        Typeface sans = Typeface.create("sans-serif", Typeface.NORMAL);
        L.dd("sans:" + sans);
        L.dd("attr:" + getAttributeSourceResourceMap());
    }
}
