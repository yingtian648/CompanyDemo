package edo.liu.ebrowser.ui.accessibility.util

import android.accessibilityservice.AccessibilityService

/**
 * @作者 Liushihua
 * @创建日志 2021-9-13 15:41
 * @描述
 */
object SystemKeyEvent {
    fun back(service: AccessibilityService){
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }

    fun home(service: AccessibilityService){
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }
}