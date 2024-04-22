package com.exa.companydemo.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.android.internal.policy.PhoneWindow;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.Tools;
import com.exa.companydemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author lsh
 * @Date 2023/3/10 10:07
 * @Description
 */
public class BuildTestDialog implements Window.Callback, KeyEvent.Callback {
    private View dialogView;
    private TextView tv;
    private Context mContext;
    private static Window mWindow;
    private View mDecor;
    private static boolean mShowing;
    private static WindowManager mWindowManager;
    private static boolean showSystemBars = true;
    @SuppressLint("StaticFieldLeak")
    private static final BuildTestDialog testDialog = new BuildTestDialog();

    public static BuildTestDialog getInstance() {
        return testDialog;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void makeMyToast(Activity activity) {
        mContext = activity;
        mWindowManager = activity.getWindowManager();
        dialogView = LayoutInflater.from(activity).inflate(R.layout.transient_notification_customer, null, false);
        tv = dialogView.findViewById(R.id.message);
        tv.setBackgroundResource(R.drawable.toast_customer_normal);
        tv.setText("111111111111111111111111111111111111");
        initGestureDetector(activity);
        tv.setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP;
        params.windowAnimations = 0;
        mWindowManager.addView(dialogView, params);
        mWindowManager.removeViewImmediate(dialogView);
    }

    public void addNoteView(Context context){
        GuideRemindView remindView = new GuideRemindView(context);
        remindView.showGuideView();
    }

    public void addView(Context context) {
        mContext = new ContextThemeWrapper(context, R.style.DialogTheme);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindow = new PhoneWindow(mContext);
        mWindow.setCallback(this);
        mWindow.setWindowManager(mWindowManager, null, null);
        mWindow.setGravity(Gravity.CENTER);
        dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout, null, false);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_button);
        Button sureBtn = dialogView.findViewById(R.id.sure_button);
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });
        sureBtn.setOnClickListener(v -> {
            dismiss();
        });
        mWindow.setContentView(dialogView);
        mDecor = mWindow.getDecorView();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        mDecor.setPadding(0, 0, 0, 0);
        mWindow.setLayout(Tools.getScreenW(context), Tools.getScreenH(context));
        mDecor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        mWindow.setStatusBarColor(Color.TRANSPARENT);
//        mWindow.setNavigationBarColor(Color.TRANSPARENT);
        mWindow.setStatusBarColor(Color.parseColor("#E3E3E3"));
        mWindow.setNavigationBarColor(Color.parseColor("#E3E3E3"));
        mWindowManager.addView(mDecor, lp);
        mShowing = true;

//        mWindow.setStatusBarColor(Color.YELLOW);
//        mWindow.setNavigationBarColor(Color.YELLOW);
        L.dd("mShowing");
    }

    private void switchSystemBar() {
        if (mWindow == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowInsetsController controller = mWindow.getInsetsController();
            if (controller != null) {
                showSystemBars = !showSystemBars;
                if (showSystemBars) {
                    controller.show(WindowInsets.Type.systemBars());
                } else {
                    controller.hide(WindowInsets.Type.systemBars());
                }
            }
        }
    }

    public void hide() {
        if (mDecor != null) {
            mDecor.setVisibility(View.GONE);
        }
    }

    public void dismiss() {
        if (mDecor == null || !mShowing) {
            return;
        }

//        if (mWindow.isDestroyed()) {
//            Log.e(TAG, "Tried to dismissDialog() but the Dialog's window was already destroyed!");
//            return;
//        }

        try {
            mWindowManager.removeViewImmediate(mDecor);
        } finally {
            mDecor = null;
            mWindow.closeAllPanels();
            mShowing = false;
        }
    }

    /**
     * Used to determine the minimum value of user's up-slip gesture
     * The height value of the upward slide is a negative number
     */
    private final float SCROLL_UP_HEIGHT = -150F;
    private GestureDetector mGestureDetector;

    private void initGestureDetector(Context context) {
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

    private void cancel() {
        startSlideTopAnimation(tv);
    }

    /**
     * slide-top animation
     */
    private void startSlideTopAnimation(View view) {
        Animation animation = new TranslateAnimation(0, 0, 0, -120);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mWindowManager.removeViewImmediate(dialogView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mWindow.superDispatchKeyEvent(event)) {
            return true;
        }
        return event.dispatch(this, mDecor != null
                ? mDecor.getKeyDispatcherState() : null, this);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (mWindow.superDispatchKeyShortcutEvent(event)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mWindow.superDispatchTouchEvent(event)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    @Nullable
    @Override
    public View onCreatePanelView(int featureId) {
        return null;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        return false;
    }

    @Override
    public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {
        return false;
    }

    @Override
    public boolean onMenuOpened(int featureId, @NonNull Menu menu) {
        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {

    }

    @Override
    public void onContentChanged() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    @Override
    public void onPanelClosed(int featureId, @NonNull Menu menu) {

    }

    @Override
    public boolean onSearchRequested() {
        return false;
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return false;
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return null;
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onActionModeFinished(ActionMode mode) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }
}
