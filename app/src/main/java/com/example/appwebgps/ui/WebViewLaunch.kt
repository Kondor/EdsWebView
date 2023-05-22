package com.example.appwebgps.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.annotation.RequiresApi

class WebViewLaunch(
    private var login: String?,
    private var password: String?,
    private val userDataReceived: UserDataReceived
) {

    private var isLoggingOut = false

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val logoutCode = "javascript: (function isLoggedOut() { " +
                "button = document.getElementById('logoutForm'); " +
                    "button.addEventListener('click', function() { " +
                    "JSBridgeLogOut.showMessage(true); }" + ")})();"
            if (login != null && password != null && !isLoggingOut) {
                val codeIfUserExists =
                    "javascript: (function(){document.getElementById('Input_Login').value ='$login'; " +
                            "document.getElementById('Input_Password').value ='$password'; " +
                            "button = document.getElementsByTagName('button'); button[0].click(); " + "})();"
                view?.loadUrl(codeIfUserExists)
            } else {
                isLoggingOut = false
                val codeIfUserDoesNotExists =
                    "javascript: (function sendMessage() { " +
                            "button = document.getElementsByTagName('button'); " +
                            "button[0].addEventListener('click', function() { " +
                            "login = document.getElementById('Input_Login').value; " +
                            "password = document.getElementById('Input_Password').value;" +
                            "JSBridge.showMessageInNative(login, password); }" + ")})();"

                view?.loadUrl(codeIfUserDoesNotExists)
            }
            view?.loadUrl(logoutCode)
        }
    }

    inner class JSBridgeLogOut {
        @JavascriptInterface
        fun showMessage(isLoggedIn: Boolean) {
            isLoggingOut = true
            login = null
            password = null
            userDataReceived.isLoggedIn(isLoggedIn)
        }
    }

    inner class JSBridge {
        @JavascriptInterface
        fun showMessageInNative(login: String, password: String) {
            userDataReceived.sendUserData(login, password)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    fun webViewSetup(webView: WebView) {
        webView.webViewClient = MyWebViewClient()
        webView.apply {
            loadUrl("https://test-webads.aisgorod.ru/Identity/Account/Login")
            settings.javaScriptEnabled = true
            addJavascriptInterface(JSBridge(),"JSBridge")
            addJavascriptInterface(JSBridgeLogOut(),"JSBridgeLogOut")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true  // api 26
            }
        }
    }
}