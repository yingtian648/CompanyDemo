package com.raite.launcher.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gxatek.cockpit.launcher.R

/**
 * @Author lsh
 * @Date 2024/5/11 11:31
 * @Description
 */
/**
 * @author lsh
 * @date 2024/5/11 11:31
 * @description
 */
class DefaultDisplayActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DefaultDisplayActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_test)
        findViewById<TextView>(R.id.tvTitle).text = "DefaultDisplayActivity"
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        Log.w(TAG, "onKeyUp: ${event.keyCode}")
        return super.onKeyUp(keyCode, event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        Log.w(TAG, "dispatchKeyEvent: ${event.keyCode}")
        return super.dispatchKeyEvent(event)
    }
}