package com.exa.companydemo.activityview

import android.app.ActivityOptions
import android.app.ActivityView
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.exa.baselib.utils.L
import com.exa.companydemo.R

class StartActivityViewActivity : AppCompatActivity() {
    private lateinit var toolBar: Toolbar
    private lateinit var actView: ActivityView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_view)
        toolBar = findViewById(R.id.toolbar)
        actView = findViewById(R.id.actView)
        toolBar.setNavigationOnClickListener {
            finish()
        }
//        ScreenUtils.hideStatusBars(this)
        actView.setCallback(object : ActivityView.StateCallback() {
            override fun onActivityViewReady(activityView: ActivityView?) {
                L.dd()
                startActivityTask()
            }

            override fun onActivityViewDestroyed(activityView: ActivityView?) {
                L.dd()
            }
        })
    }

    private fun startActivityTask() {
        val intent = Intent()
        val pkg = "com.exa.companyclient"
        val clazz = "com.exa.companyclient.ActivityViewActivity"
        intent.component = ComponentName(pkg, clazz)
        actView.startActivity(intent)
    }
}