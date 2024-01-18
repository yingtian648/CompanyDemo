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

    override fun getViewBinding(): ActivityWebBinding = ActivityWebBinding.inflate(layoutInflater)
    override fun initView() {
        bind.titleBar.setBackgroundColor(getColor(R.color.black_alpha))
        bind.titleBar.setNavigationOnClickListener { finish() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        bind.webView.webChromeClient = MWebChromeClient()
        bind.webView.webViewClient = MWebClient()
        bind.webView.settings.javaScriptEnabled = true
        bind.root.setBackgroundColor(Color.BLACK)

        var assetsFile = "guangcheng_privacy_default_day.html"
        assetsFile = "guangcheng_privacy_default_night.html"
        val url = "file:///android_asset/$assetsFile"

        bind.webView.loadUrl(url)
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
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            L.dd("$url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            L.dd("$url")
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            L.de("failingUrl=$failingUrl errorCode=$errorCode description=$description")
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            L.de("request=${request?.url} error=${error?.errorCode},${error?.description}")
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