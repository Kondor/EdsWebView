package com.example.appwebgps

import android.annotation.SuppressLint
import android.location.Address
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.System;
import android.util.Log
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity(), DeviceLocationListener {
    private lateinit var webView: WebView
    private lateinit var webViewLaunch: WebViewLaunch
    private lateinit var webDeviceLocation: WebDeviceLocation

    var currentLatitude: Double? = null
    var currentLongitude: Double? = null

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // получение ссылок на View
        webView = findViewById(R.id.webViewScreen)

        // вызов
        launchApp()
    }

    // запуск app
    @RequiresApi(Build.VERSION_CODES.O)
    private fun launchApp() {
        webViewLaunchScreen()
        webDeviceLocation = WebDeviceLocation(this, this)
    }

    /// запуск webView
    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewLaunchScreen() {
        webViewLaunch = WebViewLaunch()
        webViewLaunch.webViewSetup(webView)
    }

    /// переопределение кнопки Back
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
    }

    /// переопределение onDeviceLocationChanged
    override fun onDeviceLocationChanged(results: List<Address>?) {
        val currentLocation = results?.get(0)

        currentLocation?.apply {
            currentLatitude = latitude
            currentLongitude = longitude
//            Country = countryCode
//            cityName = getAddressLine(0)
        }

        Log.e("currentLatitude", "CurrentLatitude $currentLatitude")
    }

    override fun onDestroy() {
        super.onDestroy()
        webDeviceLocation.stopUpdateGPS()
    }
}