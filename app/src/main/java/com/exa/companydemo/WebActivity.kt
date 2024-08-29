package com.exa.companydemo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.net.http.SslError
import android.webkit.*
import com.exa.baselib.base.BaseViewBindingActivity
import com.exa.baselib.utils.L
import com.exa.companydemo.databinding.ActivityWebBinding

class WebActivity : BaseViewBindingActivity<ActivityWebBinding>() {
    private val baiduUrl = "https://www.baidu.com/"
    private val googleUrl = "https://www.google.com.hk/webhp?hl=zh-CN&sourceid=cnhp&gws_rd=ssl"

    override fun getViewBinding(): ActivityWebBinding = ActivityWebBinding.inflate(layoutInflater)
    override fun initView() {
        bind.titleBar.setBackgroundColor(getColor(R.color.white))
        bind.titleBar.setNavigationOnClickListener { finish() }
    }

    override fun initData() {
        initWebViewConfig()
        bind.root.setBackgroundColor(Color.BLACK)
        bind.webView.loadUrl(baiduUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewConfig(){
        bind.webView.clearHistory()
        bind.webView.clearCache(true)
        bind.webView.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"
        //允许js
        bind.webView.settings.javaScriptEnabled = true
        //支持缩放，默认为true。是下面那个的前提。
        bind.webView.settings.setSupportZoom(true)
        //缩小内容以适应屏幕宽度
        bind.webView.settings.loadWithOverviewMode = true
        // 设置WebView可触摸放大缩小
        bind.webView.settings.builtInZoomControls = true
        bind.webView.settings.useWideViewPort = true
        //设置解码html页时要使用的默认文本编码名称
        bind.webView.settings.defaultTextEncodingName = Charsets.UTF_8.name()
        //设置WebView是否应加载图像资源
        bind.webView.settings.loadsImagesAutomatically = true
        bind.webView.settings.domStorageEnabled = true
        bind.webView.settings.databaseEnabled = true
        //允许加载混合网络协议内容即可
        bind.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        bind.webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        bind.webView.webChromeClient = MWebChromeClient()
        bind.webView.webViewClient = MWebClient()
    }

    class MWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }

    class MWebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            L.dd("$url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            L.dd("$url")
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            L.de("${request?.url} error=${error?.errorCode},${error?.description}")
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            L.de("request=${request?.url} error=${errorResponse?.data}")
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            L.de("error=${error?.url} ${error?.certificate}")
            handler?.cancel()
            view?.reload()
        }
    }

}