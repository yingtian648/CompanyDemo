package com.exa.companydemo.image

import android.graphics.BitmapFactory
import android.widget.Button
import com.exa.baselib.base.BaseBindActivity
import com.exa.baselib.utils.ImageUtils
import com.exa.baselib.utils.L
import com.exa.companydemo.R
import com.exa.companydemo.databinding.ActivityImageutilBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageUtilActivity : BaseBindActivity<ActivityImageutilBinding>() {

    override fun setContentViewLayoutId(): Int = R.layout.activity_imageutil

    override fun initView() {
        BitmapFactory.decodeStream(assets.open("img_res.jpg"))?.let{
            bind.iv.setImageBitmap(it)
        }
        bind.sharpBtn.setOnClickListener {
            L.d((it as Button).text.toString() + "")
            GlobalScope.launch {
                BitmapFactory.decodeStream(assets.open("img_res.jpg"))?.let {
                    ImageUtils.sharpening(it)?.apply {
                        bind.iv.setImageBitmap(this)
                    }
                }
            }
        }
    }

    override fun initData() {

    }
}