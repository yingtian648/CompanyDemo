package com.exa.companydemo.accessibility.ext

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo

fun AccessibilityService.back(){
    this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}

fun AccessibilityService.home(){
    this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
}

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
