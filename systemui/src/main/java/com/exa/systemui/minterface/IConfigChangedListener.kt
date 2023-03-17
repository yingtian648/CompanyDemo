package com.exa.systemui.minterface

import android.content.res.Configuration

interface IConfigChangedListener {
    fun onConfigurationChanged(configuration: Configuration)
}