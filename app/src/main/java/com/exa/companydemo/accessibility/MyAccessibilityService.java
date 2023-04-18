package com.exa.companydemo.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.exa.baselib.utils.L;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MyAccessibilityService extends AccessibilityService {
    public static MyAccessibilityService service;
    private static boolean isActionFinish = false;
    private static OnCompleteListener listener;

    public interface OnCompleteListener {
        void onComplete();
    }

    //初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        service = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        L.d("AppActiveService onAccessibilityEvent：" + event.getClassName().toString());
//        L.d("AppActiveService onAccessibilityEvent：" + event.getPackageName().toString());
        String className = event.getClassName().toString();
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://通知栏发生变化
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://状态变化
                L.d("TYPE_WINDOW_STATE_CHANGED");
                if (!isActionFinish) {
                    isActionFinish = true;
                    if (MyAccessibilityService.listener != null) {
                        MyAccessibilityService.listener.onComplete();
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://窗体内容发生变化
//                L.d("TYPE_WINDOW_CONTENT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED://点击一个控件
                L.d("TYPE_VIEW_CLICKED:"+className);
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER://[手指]覆盖在屏幕上
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT://[手指]退出覆盖在屏幕上
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT://表示应用程序发布公告的事件
                break;
            case AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT://表示助理当前读取用户屏幕上下文的事件
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END://结束手势检测
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START://开始手势检测
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END://一个触摸探索的手势结束
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START://开始一个触摸探索的手势
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END://用户结束触摸屏幕
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START://用户开始触摸屏幕
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://获取可访问性焦点
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED://清除可访问性焦点
                break;
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED://上下文点击一个控件
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED://控件获取到焦点
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED://长按控件
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED://滑动控件
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED://控件被选中
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED://文本编辑发生改变EditText
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED://选中的EditText已改变
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY://以给定的移动粒度遍历视图的文本
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED://表示屏幕上显示的系统窗口中的事件更改。此事件类型只能由系统调度
                break;
        }
    }

    /**
     * 行为开始时调用
     *
     * @param listener
     */
    public static void startAction(OnCompleteListener listener) {
        MyAccessibilityService.isActionFinish = false;
        MyAccessibilityService.listener = listener;
    }

    /**
     * @return 返回顶层节点node
     */
    @Nullable
    public AccessibilityNodeInfo getRootWindowNode() {
        return getRootInActiveWindow();
    }

    @Override
    public void onInterrupt() {
        service = null;
        L.d("AppActiveService onInterrupt");
    }

    /**
     * 处理通知栏信息 *
     * if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {//只接收点击事件
     * AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
     * dfsnode(nodeInfo, 0);
     * }
     * }
     *
     * @Override public void onInterrupt() {//被中断
     * mService = null;
     * L.d("UserActiveCheckService onInterrupt");
     * }
     * @Override protected void onServiceConnected() {
     * super.onServiceConnected();
     * L.d("UserActiveCheckService onServiceConnected");
     * mService = this;
     * }
     * <p>
     * /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return service != null;
    }

    // 停止服务
    public void stopAccessibility(){
        try {
            disableSelf();
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
            L.de(e);
        }
    }
}
