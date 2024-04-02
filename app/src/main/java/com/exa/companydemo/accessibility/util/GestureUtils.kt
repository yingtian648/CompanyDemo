package com.exa.companydemo.accessibility.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.exa.baselib.utils.L
import com.exa.baselib.utils.Tools
import com.exa.companydemo.App


/**
 * @作者 Liushihua
 * @创建日志 2021-9-13 15:56
 * @描述
 */
object GestureUtils {

    /**
     * 上下滑动
     */
    fun scrollUpDown(
        service: AccessibilityService,
        nodeInfo: AccessibilityNodeInfo,
        isUp: Boolean
    ) {
        L.dd(isUp)
        val builder = GestureDescription.Builder()
        val path = Path()
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        val centerX = rect.left.toFloat() + (rect.right.toFloat() - rect.left.toFloat()) / 2
        if (isUp) {//向上滑动
            path.moveTo(centerX, rect.bottom.toFloat())
            path.lineTo(centerX, rect.top.toFloat())
        } else {//向下滑动
            path.moveTo(centerX, rect.top.toFloat())
            path.lineTo(centerX, rect.bottom.toFloat())
        }
        val description = builder.addStroke(
            StrokeDescription(path, 20L, 1000L)
        ).build()

        service.dispatchGesture(description, object : AccessibilityService.GestureResultCallback() {
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                L.d("滑动成功")
            }

            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                L.d("滑动失败")
            }
        }, null)
    }

    /**
     * 左右滑动
     * @param toLeft 是否从右往左滑动
     */
    fun scrollHorizontal(
        service: AccessibilityService,
        nodeInfo: AccessibilityNodeInfo,
        toLeft: Boolean
    ) {
        L.dd(toLeft)
        val builder = GestureDescription.Builder()
        val path = Path()
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        val centerY = rect.top.toFloat() + (rect.bottom.toFloat() - rect.top.toFloat()) / 2
        if (toLeft) {//向左滑动
            path.moveTo(rect.right.toFloat(), centerY)
            path.lineTo(rect.left.toFloat(), centerY)
        } else {//向右滑动
            path.moveTo(rect.left.toFloat(), centerY)
            path.lineTo(rect.right.toFloat(), centerY)
        }
        val description = builder.addStroke(
            StrokeDescription(path, 20L, 1000L)
        ).build()

        service.dispatchGesture(description, object : AccessibilityService.GestureResultCallback() {
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                L.d("取消滑动")
            }

            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                L.d("滑动成功")
            }
        }, null)
    }

    /**
     * 左右滑动屏幕中间
     */
    fun scrollHorizontalScreenCenter(service: AccessibilityService, toLeft: Boolean): Boolean {
        L.dd(toLeft)
        val w = Tools.getScreenW(App.getContext())
        val h = Tools.getScreenH(App.getContext())

        val builder = GestureDescription.Builder()
        val path = Path()
        if (toLeft) {
            path.moveTo(w.toFloat() * 2 / 3, h.toFloat() / 2)
            path.lineTo(w.toFloat() / 3, h.toFloat() / 2)
        } else {
            path.moveTo(w.toFloat() / 3, h.toFloat() / 2)
            path.lineTo(w.toFloat() * 2 / 3, h.toFloat() / 2)
        }
        val description = builder.addStroke(
            StrokeDescription(path, 20L, 200L)
        ).build()
        return service.dispatchGesture(description, null, null)
    }

    /**
     * 缩放
     * 两点触控
     * @param scale 两个手指之间的距离 >0 放大 <0 缩小
     */
    fun scaleAtScreenCenter(service: AccessibilityService, scale: Int) {
        val builder = GestureDescription.Builder()
        val path1 = Path()
        val path2 = Path()
        val w = Tools.getScreenW(App.getContext())
        val h = Tools.getScreenH(App.getContext())
        val centerX = w / 2
        val centerY = h / 2
        val offset = 60
        path1.moveTo(centerX + offset.toFloat(), centerY.toFloat())
        path1.lineTo(centerX + offset.toFloat() + scale / 2, centerY.toFloat())
        path1.moveTo(centerX - offset.toFloat(), centerY.toFloat())
        path1.lineTo(centerX - offset.toFloat() - scale / 2, centerY.toFloat())
        val description = builder
            .addStroke(StrokeDescription(path1, 0L, 500))
            .addStroke(StrokeDescription(path2, 0L, 500))
            .build()
        service.dispatchGesture(description, object : AccessibilityService.GestureResultCallback() {
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                L.d("取消缩放")
            }

            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                L.d("缩放成功")
            }
        }, null)
    }
}