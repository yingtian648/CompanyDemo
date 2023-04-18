package com.exa.companydemo.accessibility.ext

import android.view.accessibility.AccessibilityNodeInfo

/**
 * @作者 Liushihua
 * @创建日志 2021-9-13 15:34
 * @描述
 */

fun AccessibilityNodeInfo.click() {
    this.performAction(AccessibilityNodeInfo.ACTION_CLICK)
}

fun AccessibilityNodeInfo.clickLong() {
    this.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
}

fun AccessibilityNodeInfo.findNodeById(elementId: String): AccessibilityNodeInfo? {
    this.findAccessibilityNodeInfosByViewId(elementId)?.apply {
        return this[0]
    }
    return null
}

fun AccessibilityNodeInfo.findNodeByText(text: String): AccessibilityNodeInfo? {
    this.findAccessibilityNodeInfosByText(text)?.apply {
        return this[0]
    }
    return null
}
