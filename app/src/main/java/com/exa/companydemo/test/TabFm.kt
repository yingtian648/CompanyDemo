package com.exa.companydemo.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.exa.baselib.base.BaseFragment
import com.exa.baselib.utils.L
import com.exa.companydemo.R

/**
 * @Author lsh
 * @Date 2023/8/2 18:22
 * @Description
 */
class TabFm(var title: String) : BaseFragment() {
    private lateinit var rootView: FrameLayout
    private var dialog: DemoDialog? = null
    override fun setContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun initView(view: View) {
        view.findViewById<TextView>(R.id.tv).text = title
        rootView = view.findViewById(R.id.rootView)
        view.findViewById<TextView>(R.id.btn).setOnClickListener {
            L.d("$title dialog?.show()")
            dialog ?: run { dialog = DemoDialog(requireContext()) }
            dialog?.show(childFragmentManager)
        }
    }

    override fun initData() {

    }

    override fun onStop() {
        super.onStop()
        L.dd()
        dialog?.dismiss()
    }
}