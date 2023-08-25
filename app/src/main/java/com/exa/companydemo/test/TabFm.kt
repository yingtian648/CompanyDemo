package com.exa.companydemo.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import com.exa.baselib.base.BaseFragment
import com.exa.companydemo.R
import com.exa.companydemo.utils.WebViewConfig

/**
 * @Author lsh
 * @Date 2023/8/2 18:22
 * @Description
 */
class TabFm(var title:String) : BaseFragment() {
    private lateinit var webView:WebView
    private lateinit var rootView:LinearLayout
    override fun setContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun initView(view: View) {
        view.findViewById<TextView>(R.id.tv).text = title
        webView = view.findViewById(R.id.webView)
        rootView= view.findViewById(R.id.rootView)
    }

    override fun initData() {
        var url = "https://m.baidu.com/"
        url = "https://www.baidu.com/"

        val webConfig = WebViewConfig(activity,webView,rootView,object :WebViewConfig.WCallBack{
            override fun onProgress(progress: Int) {
            }

            override fun onDownload(url: String?) {
            }

            override fun onReceivedTitle(title: String, url: String) {
            }
        })
        lifecycle.addObserver(webConfig)
        webView.loadUrl(url)
    }
}