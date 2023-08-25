package com.exa.companydemo.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.exa.companydemo.R;

/**
 * @Author lsh
 * @Date 2023/3/10 10:07
 * @Description
 */
public class BuildTestToast {
    private static WindowManager windowManager;
    private static View toastView;
    private static TextView tv;

    public static void makeMyToast(Activity activity) {
        windowManager = activity.getWindowManager();
        toastView = LayoutInflater.from(activity).inflate(R.layout.transient_notification_customer, null, false);
        tv = toastView.findViewById(R.id.message);
        tv.setBackgroundResource(R.drawable.toast_customer_normal);
        tv.setText("111111111111111111111111111111111111");
        initGestureDetector(activity);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP;
        params.windowAnimations = 0;
        windowManager.addView(toastView, params);
    }

    /**
     * Used to determine the minimum value of user's up-slip gesture
     * The height value of the upward slide is a negative number
     */
    private static final float SCROLL_UP_HEIGHT = -150F;
    private static GestureDetector mGestureDetector;

    private static void initGestureDetector(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            float startY = 0;

            @Override
            public boolean onDown(MotionEvent e) {
                startY = e.getY();
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                cancel();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                if (velocityY < SCROLL_UP_HEIGHT) {
                    cancel();
                }
                return true;
            }
        });
    }

    private static void cancel() {
        startSlideTopAnimation(tv);
    }

    /**
     * slide-top animation
     */
    private static void startSlideTopAnimation(View view) {
        Animation animation = new TranslateAnimation(0, 0, 0, -120);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                windowManager.removeViewImmediate(toastView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(200);
        view.startAnimation(animation);
    }
}
