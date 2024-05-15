package com.raite.launcher.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gxatek.cockpit.launcher.R

class SubDisplayActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SubDisplayActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_test)
        findViewById<TextView>(R.id.tvTitle).text = "SubDisplayActivity"
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