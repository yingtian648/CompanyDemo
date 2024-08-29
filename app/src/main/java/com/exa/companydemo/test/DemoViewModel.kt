package com.exa.companydemo.test

import androidx.lifecycle.ViewModel
import com.exa.companydemo.utils.LogUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * @author lsh
 * @date 2024/8/23 10:43
 * @description
 */
class DemoViewModel : ViewModel() {
    private val tag = "DemoViewModel"
    private val mStateFlow = MutableStateFlow(false)
    val stateFlow = mStateFlow.asStateFlow()

    fun setStateFlow(state: Boolean) {
        viewModelScope.launch {
            LogUtil.i(tag, "setShowLightShowMusicDialog state=$state")
            mStateFlow.value = state
        }
    }
}