package com.exa.companydemo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.net.http.SslError
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.exa.baselib.utils.L


/**
 * 观察activity的生命周期
 */
@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
class WebViewConfig(
    var activity: Activity?,
    private val webview: WebView,
    val rootView: ViewGroup,
    val callback: WCallBack
) : LifecycleObserver {

    interface WCallBack {
        fun onProgress(progress: Int)
        fun onDownload(url: String?)
        fun onReceivedTitle(title: String, url: String)
    }

    private val baseUrlBaidu = "https://www.baidu.com/"
    private val baseUrlBaiduMobile = "https://m.baidu.com/"
    private val baseUrlGoogle = "https://www.google.com.hk/webhp?hl=zh-CN&sourceid=cnhp&gws_rd=ssl"

    private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
    private var customView: View? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    init {
//        webview.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"
        webview.settings.javaScriptEnabled = true //允许js
        webview.settings.setSupportZoom(true)  //支持缩放，默认为true。是下面那个的前提。
        webview.settings.loadWithOverviewMode = true //缩小内容以适应屏幕宽度
        webview.settings.builtInZoomControls = true// 设置WebView可触摸放大缩小
        webview.settings.useWideViewPort = true
        webview.settings.defaultTextEncodingName = Charsets.UTF_8.name() //设置解码html页时要使用的默认文本编码名称
        webview.settings.loadsImagesAutomatically = true //设置WebView是否应加载图像资源

        webview.settings.domStorageEnabled = true
        webview.settings.databaseEnabled = true
        webview.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW//允许加载混合网络协议内容即可
        webview.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        webview.addJavascriptInterface(
            JavaScriptInterface(),
            "injectedObject"
        )// js注入【添加js调用Android方法】

        webview.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->

            //下载
            L.d("下载文件：${url}")
        }

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                url?.apply {
                    if (this.startsWith("http://") || this.startsWith("https://"))
                        view.loadUrl(this) //本webView加载【不会去调用系统的加载】
                    else {
                        L.e("非正常网页：$this")
                    }
                }
                return true
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()//继续请求
            }

            override fun onPageFinished(view: WebView?, url: String?) {// 页面加载完成
                super.onPageFinished(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                request?.url.apply {
                    L.d("WebPage load url:$this")
                }
                return super.shouldInterceptRequest(view, request)
            }
        }

        webview.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                activity?.apply {
                    if (!isDestroyed && !isFinishing)
                        callback.onProgress(newProgress)
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                view?.apply {
                    L.d("onReceivedTitle:title=$title \nurl=$url")
                    if (url == null || title == null || url == baseUrlBaidu || url == baseUrlBaiduMobile || url == baseUrlGoogle || activity?.isDestroyed == true ||
                        title.startsWith("http://") || title.startsWith("https://") ||
                        title == "百度一下" || title == "网页无法打开"
                    ) return
                    activity?.apply {
                        if (!isDestroyed && !isFinishing)
                            callback.onReceivedTitle(title, url!!)
                    }
                }
            }

            //上传文件
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@WebViewConfig.filePathCallback?.onReceiveValue(null)
                this@WebViewConfig.filePathCallback = filePathCallback
                return true
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback
            ) {
                callback.invoke(origin, true, false) //注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                L.d("onShowCustomView")
                view?.let {
                    customView = view
                    rootView.addView(view)
                }
                mCustomViewCallback = callback
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                super.onShowCustomView(view, callback)
            }

            override fun onHideCustomView() {
                L.d("onHideCustomView")
                customView?.let {
                    rootView.removeView(it)
                }
                mCustomViewCallback?.onCustomViewHidden()
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                super.onHideCustomView()
            }
        }
    }

    fun onWebChooseFileBack(data: Intent?) {
        var results: Array<Uri> = arrayOf()
        data?.apply {
            val dataString = dataString
            clipData?.let {
                for (i in 0 until it.itemCount) {
                    val item = it.getItemAt(i)
                    results[i] = item.uri
                }
            }
            if (dataString != null) results = arrayOf(Uri.parse(dataString))
            if (!results.isEmpty())
                filePathCallback?.onReceiveValue(results)
            filePathCallback = null
        }
    }

    //activity onDestory时调用
    fun destory() {
        (webview.getParent() as ViewGroup).removeView(webview)
        webview.stopLoading()
        // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
        webview.clearCache(true)
        webview.clearFormData()
        webview.clearHistory()
        webview.removeAllViews()
        webview.destroy()
    }

    /**
     * 查看源码
     */
    fun showResourceCode(view: WebView?) {
//        view?.loadUrl(
//            "javascript:window.injectedObject.showSource('<head>'+"
//                    + "document.getElementsByTagName('html')[0].innerHTML+'</head>');"
//        )
        view?.evaluateJavascript(
            "javascript:window.injectedObject.showSource('<head>'+"
                    + "document.getElementsByTagName('html')[0].innerHTML+'</head>');"
        ) { value ->
            L.d("----------------源码 onReceiveValue----------------")
            L.d(
                "${Html.fromHtml(value)}"
            )
            L.d("----------------源码 onReceiveValue----------------")
        }
    }

    class JavaScriptInterface {
        // 调用方式
        // 1.webview.loadUrl("javascript:window.injectedObject.showSource('<head>'+"+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');")
        // 1.webview.evaluateJavascript("javascript:window.injectedObject.showSource('<head>'+"+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');")
        @JavascriptInterface
        fun showSource(html: String?) {
            L.d("----------------源码----------------")
            L.d(html)
            L.d("----------------源码----------------")
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestory() {
        destory()
        activity = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        webview.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        webview.onPause()
    }
}