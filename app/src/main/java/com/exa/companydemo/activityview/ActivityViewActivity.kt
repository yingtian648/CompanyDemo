package com.exa.companydemo.activityview

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.exa.companydemo.R
import com.exa.companydemo.widget.MySurfaceView

class ActivityViewActivity : AppCompatActivity() {

    private lateinit var surfaceView: MySurfaceView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        surfaceView = findViewById(R.id.msv)
        drawLine()
    }

    private fun drawLine() {

    }
}