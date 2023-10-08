package com.exa.companydemo

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.AppComponentFactory

/**
 * @Author lsh
 * @Date 2023/9/8 15:21
 * @Description
 */
@RequiresApi(Build.VERSION_CODES.P)
class MAppComponentFactory : AppComponentFactory() {
    private val TAG = "MAppComponentFactory"
    override fun instantiateApplicationCompat(cl: ClassLoader, className: String): Application {
        return super.instantiateApplicationCompat(cl, className)
    }

    override fun instantiateActivityCompat(
        cl: ClassLoader,
        className: String,
        intent: Intent?
    ): Activity {
        return super.instantiateActivityCompat(cl, className, intent)
    }

    override fun instantiateReceiverCompat(
        cl: ClassLoader,
        className: String,
        intent: Intent?
    ): BroadcastReceiver {
        return super.instantiateReceiverCompat(cl, className, intent)
    }

    override fun instantiateServiceCompat(
        cl: ClassLoader,
        className: String,
        intent: Intent?
    ): Service {
        return super.instantiateServiceCompat(cl, className, intent)
    }

    override fun instantiateProviderCompat(cl: ClassLoader, className: String): ContentProvider {
        val contentProvider = super.instantiateProviderCompat(cl, className)
        if (contentProvider is ContextInitializer) {
            (contentProvider as ContextInitializer).setContextAvailableCallback(
                object : ContextAvailableCallback {
                    override fun onContextAvailable(context: Context?) {
//                        SystemUIFactory.createFromConfig(context)
//                        val rootComponent: SysUIComponent =
//                            SystemUIFactory.getInstance().getSysUIComponent()
//                        try {
//                            val injectMethod: Method = rootComponent.getClass()
//                                .getMethod("inject", contentProvider.javaClass)
//                            injectMethod.invoke(rootComponent, contentProvider)
//                        } catch (e: NoSuchMethodException) {
//                            Log.w(TAG, "instantiateProviderCompat err:" + e.message)
//                        } catch (e: IllegalAccessException) {
//                            Log.w(TAG, "instantiateProviderCompat err:" + e.message)
//                        } catch (e: InvocationTargetException) {
//                            Log.w(TAG,"instantiateProviderCompat err:" + e.message)
//                        }
                    }
                }
            )
        }
        return contentProvider
    }

    override fun instantiateClassLoader(cl: ClassLoader, aInfo: ApplicationInfo): ClassLoader {
        return super.instantiateClassLoader(cl, aInfo)
    }

    /**
     * Implemented in classes that get started by the system before a context is available.
     */
    interface ContextInitializer {
        fun setContextAvailableCallback(callback: ContextAvailableCallback?)
    }

    /**
     * A callback that receives a Context when one is ready.
     */
    interface ContextAvailableCallback {
        fun onContextAvailable(context: Context?)
    }

}