package com.exa.companydemo.accessibility.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.accessibility.AccessibilityNodeInfo
import com.exa.baselib.utils.L
import com.exa.baselib.utils.Tools
import com.exa.companydemo.App


/**
 * @author Liushihua
 * @date 2021-9-13 15:56
 * @describe 手势工具类
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
                L.d("取消滑动")
            }

            override fun onCompleted(gestureDescription: GestureDescription?) {
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
     * @param scaleBigger 是否放大
     *
     * 添加控制点（可以是多个）
     * GestureDescription.addStroke(StrokeDescription(path1, 0, 100))
     */
    fun scaleInCenter(service: AccessibilityService, scaleBigger: Boolean) {
        L.dd(scaleBigger)
        val builder = GestureDescription.Builder()
        val norDistance = 300
        val path1 = Path()
        val path2 = Path()
        val w = Tools.getScreenW(App.getContext())
        val h = Tools.getScreenH(App.getContext())
        val centerX = w / 2
        val centerY = h / 2
        val desc = if (scaleBigger) {
            path1.moveTo(centerX.toFloat(), centerY.toFloat())
            path1.lineTo(centerX.toFloat() - 100, centerY.toFloat() + 50)
            //第二个点
            path2.moveTo(centerX.toFloat() + norDistance, centerY.toFloat())
            path2.lineTo(centerX.toFloat() + norDistance + 100, centerY.toFloat() - 100)

            builder.addStroke(StrokeDescription(path1, 0, 100))
                .addStroke(StrokeDescription(path2, 0, 100))
                .build()
        } else {
            path1.moveTo(centerX.toFloat() - 100, centerY.toFloat() + 100)
            path1.lineTo(centerX.toFloat() - 100 + 100, centerY.toFloat() + 100 + 50)
            //第二个点
            path2.moveTo(centerX.toFloat() + norDistance, centerY.toFloat() + 200)
            path2.lineTo(centerX.toFloat() + norDistance - 100, centerY.toFloat() + 200 - 50)

            builder.addStroke(StrokeDescription(path1, 0, 400))
                .addStroke(StrokeDescription(path2, 0, 400))
                .build()
        }
        service.dispatchGesture(
            desc, object : AccessibilityService.GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    L.d("取消缩放")
                }

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    L.d("缩放成功")
                }
            },
            null
        )
    }

    /**
     * 三指左右滑动
     */
    fun swipeWith3Points(service: AccessibilityService, display: Display, toLeft: Boolean) {
        val path = Path()
        val path1 = Path()
        val path2 = Path()
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
        val cenX = metrics.widthPixels / 2
        val cenY = metrics.heightPixels / 2
        path.moveTo(cenX.toFloat(), cenY.toFloat() - 100)
        path1.moveTo(cenX.toFloat() + 100, cenY.toFloat() - 100 - 100)
        path2.moveTo(cenX.toFloat() + 200, cenY.toFloat() - 80)
        if (toLeft) {
            path.lineTo(cenX.toFloat() - (cenX - 100), cenY.toFloat() - 100)
            path1.lineTo(cenX.toFloat() + 100 - (cenX - 100), cenY.toFloat() - 100 - 10)
            path2.lineTo(cenX.toFloat() + 200 - (cenX - 100), cenY.toFloat() - 80)
        } else {
            path.lineTo(cenX.toFloat() + (cenX - 100), cenY.toFloat() - 100)
            path1.lineTo(cenX.toFloat() + 100 + (cenX - 100), cenY.toFloat() - 100 - 10)
            path2.lineTo(cenX.toFloat() + 200 + (cenX - 100), cenY.toFloat() - 80)
        }
        val gestureDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            GestureDescription.Builder()
                .setDisplayId(display.displayId)
                .addStroke(StrokeDescription(path, 0, 500))
                .addStroke(StrokeDescription(path1, 0, 500))
                .addStroke(StrokeDescription(path2, 0, 500))
                .build()
        } else {
            GestureDescription.Builder()
                .addStroke(StrokeDescription(path, 0, 500))
                .addStroke(StrokeDescription(path1, 0, 500))
                .addStroke(StrokeDescription(path2, 0, 500))
                .build()
        }
        service.dispatchGesture(
            gestureDescription,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    L.d("swipeWith3Points onCancelled")
                }

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    L.d("swipeWith3Points onCompleted")
                }
            },
            null
        )
    }
}