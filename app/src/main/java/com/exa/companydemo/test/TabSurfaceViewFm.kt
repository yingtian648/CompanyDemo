package com.exa.companydemo.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.exa.baselib.base.BaseFragment
import com.exa.companydemo.R
import com.exa.companydemo.widget.MySurfaceView

/**
 * @Author lsh
 * @Date 2023/8/3 17:19
 * @Description
 */
class TabSurfaceViewFm : BaseFragment() {
    private var isGone = true
    override fun setContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_surface, container, false)
    }

    override fun initView(view: View) {
        val surfaceView = view.findViewById<MySurfaceView>(R.id.sv)
        view.findViewById<Button>(R.id.btn).setOnClickListener {
            isGone = !isGone
            surfaceView.visibility = if (isGone) View.GONE else View.VISIBLE
        }
    }

    override fun initData() {

    }
}