package com.exa.companydemo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exa.baselib.utils.L

class DemoActivity : AppCompatActivity() {
    private lateinit var tv: Button
    private var index = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        tv = findViewById(R.id.tv)
        findViewById<TextView>(R.id.btn).setOnClickListener {
            finish()
        }
        tv.setOnClickListener {
            index++
            tv.text = "点击 $index"
            test()
        }
        L.dd()
    }

    private fun test(){
        L.dd()

    }

    override fun onResume() {
        super.onResume()
        L.dd()
    }

    override fun onPause() {
        super.onPause()
        L.dd()
    }

    override fun onStop() {
        super.onStop()
        L.dd()
    }

    override fun onDestroy() {
        super.onDestroy()
        L.dd()
    }
}