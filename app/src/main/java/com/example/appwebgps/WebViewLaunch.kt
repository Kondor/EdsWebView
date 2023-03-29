package com.example.appwebgps

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.HttpAuthHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import org.json.JSONObject

class WebViewLaunch {

    /// создаем WebView клиент
    /// установка WebViewClient нужна для того,
    /// чтобы пользователь нажимал на ссылку внутри веб-страницы нашего приложения
    /// и эта ссылка открывалась в нашем приложении, а не браузером по умолчанию.
    private class MyWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            //Log.d("requestHeaders", request.requestHeaders.toString())
            return true
        }

        // Для старых устройств
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (url != null) {
                Log.d("URLStarted", url)
                Log.d("Favicon", favicon.toString())
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (url != null && url.contains("access_token=")) {
                Log.d("Contains", "is Token")
            } else {
                Log.d("Contains", "isn't Token")
            }
        }

//        override fun onReceivedHttpAuthRequest(
//            view: WebView?,
//            handler: HttpAuthHandler?,
//            host: String?,
//            realm: String?
//        ) {
//            super.onReceivedHttpAuthRequest(view, handler, host, realm)
//            handler?.proceed("username", "password")
//            Log.d("Proceed", handler.toString())
////            Log.d("LogPass", handler?.proceed("username", "password").toString())
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    fun webViewSetup(webView: WebView) {
        webView.webViewClient = MyWebViewClient()

        webView.apply {
            // указываем страницу загрузки
            // loadUrl("https://ulyanovsk-eds.aisgorod.ru/")
            loadUrl("https://test-webads.aisgorod.ru/")

            // включаем поддержку JavaScript
            settings.javaScriptEnabled = true

            // запрет отключения функции безопасного просмотра
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true  // api 26
            }

        }
    }
}