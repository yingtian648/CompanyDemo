package com.exa.companyclient

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.exa.baselib.utils.L
import com.exa.baselib.view.MySurfaceView

class ActivityViewActivity : AppCompatActivity() {

    private lateinit var surfaceView: MySurfaceView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(
            (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                    or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    )
        )
        super.onCreate(savedInstanceState)
        L.dd()
        setContentView(R.layout.activity_view)
        surfaceView = findViewById(R.id.msv)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        afterInitView()
    }

    private fun afterInitView() {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK
        val options = ActivityOptions.makeBasic().setLaunchDisplayId(1)
        startActivity(intent, options.toBundle())
    }

    override fun onStart() {
        super.onStart()
//        ScreenUtils.hideStatusBars(this)
    }
}