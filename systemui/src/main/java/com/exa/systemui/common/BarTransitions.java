/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exa.systemui.common;

import android.annotation.IntDef;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BarTransitions {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_COLORS = false;

    public static final int MODE_OPAQUE = 0;
    public static final int MODE_SEMI_TRANSPARENT = 1;
    public static final int MODE_TRANSLUCENT = 2;
    public static final int MODE_LIGHTS_OUT = 3;
    public static final int MODE_TRANSPARENT = 4;
    public static final int MODE_WARNING = 5;
    public static final int MODE_LIGHTS_OUT_TRANSPARENT = 6;

    @IntDef(flag = true, prefix = { "MODE_" }, value = {
            MODE_OPAQUE,
            MODE_SEMI_TRANSPARENT,
            MODE_TRANSLUCENT,
            MODE_LIGHTS_OUT,
            MODE_TRANSPARENT,
            MODE_WARNING,
            MODE_LIGHTS_OUT_TRANSPARENT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransitionMode {}

    public static final int LIGHTS_IN_DURATION = 250;
    public static final int LIGHTS_OUT_DURATION = 1500;
    public static final int BACKGROUND_DURATION = 200;

    private final String mTag;
    private final View mView;
    private int mMode;
    private boolean mAlwaysOpaque = false;

    public BarTransitions(View view, int gradientResourceId) {
        mTag = "BarTransitions." + view.getClass().getSimpleName();
        mView = view;
    }

    public void destroy() {
        // To be overridden
    }

    public int getMode() {
        return mMode;
    }

    public void setAutoDim(boolean autoDim) {
        // Default is don't care.
    }

    /**
     * @param alwaysOpaque if {@code true}, the bar's background will always be opaque, regardless
     *         of what mode it is currently set to.
     */
    public void setAlwaysOpaque(boolean alwaysOpaque) {
        mAlwaysOpaque = alwaysOpaque;
    }

    public boolean isAlwaysOpaque() {
        // Low-end devices do not support translucent modes, fallback to opaque
        return mAlwaysOpaque;
    }

    public void transitionTo(int mode, boolean animate) {
        if (isAlwaysOpaque() && (mode == MODE_SEMI_TRANSPARENT || mode == MODE_TRANSLUCENT
                || mode == MODE_TRANSPARENT)) {
            mode = MODE_OPAQUE;
        }
        if (isAlwaysOpaque() && (mode == MODE_LIGHTS_OUT_TRANSPARENT)) {
            mode = MODE_LIGHTS_OUT;
        }
        if (mMode == mode) return;
        int oldMode = mMode;
        mMode = mode;
        if (DEBUG) Log.d(mTag, String.format("%s -> %s animate=%s",
                modeToString(oldMode), modeToString(mode),  animate));
        onTransition(oldMode, mMode, animate);
    }

    protected void onTransition(int oldMode, int newMode, boolean animate) {
        applyModeBackground(oldMode, newMode, animate);
    }

    protected void applyModeBackground(int oldMode, int newMode, boolean animate) {
        if (DEBUG) Log.d(mTag, String.format("applyModeBackground oldMode=%s newMode=%s animate=%s",
                modeToString(oldMode), modeToString(newMode), animate));
    }

    public static String modeToString(int mode) {
        if (mode == MODE_OPAQUE) return "MODE_OPAQUE";
        if (mode == MODE_SEMI_TRANSPARENT) return "MODE_SEMI_TRANSPARENT";
        if (mode == MODE_TRANSLUCENT) return "MODE_TRANSLUCENT";
        if (mode == MODE_LIGHTS_OUT) return "MODE_LIGHTS_OUT";
        if (mode == MODE_TRANSPARENT) return "MODE_TRANSPARENT";
        if (mode == MODE_WARNING) return "MODE_WARNING";
        if (mode == MODE_LIGHTS_OUT_TRANSPARENT) return "MODE_LIGHTS_OUT_TRANSPARENT";
        throw new IllegalArgumentException("Unknown mode " + mode);
    }

    protected boolean isLightsOut(int mode) {
        return mode == MODE_LIGHTS_OUT || mode == MODE_LIGHTS_OUT_TRANSPARENT;
    }
}
