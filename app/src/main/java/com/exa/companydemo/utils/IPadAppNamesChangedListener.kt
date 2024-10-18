package com.exa.companydemo.utils

/**
 * 当PAD端App列表发生变化时，onPadAppNamedChanged()方法把最新App列表回调给监听者。
 */
interface IPadAppNamesChangedListener {

    fun onPadAppNamedChanged(padAppNames: Array<String>)

}