package com.example.appwebgps.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.appwebgps.R
import com.example.appwebgps.connection.NetworkConnection
import com.example.appwebgps.utils.StringConstants
import com.example.appwebgps.databinding.ActivityMainBinding
import com.example.appwebgps.location.workers.LocationService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), UserDataReceived {

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val mainViewModel: MainViewModel by viewModels()
    private val connectivity: NetworkConnection by lazy {
        NetworkConnection(this)
    }

    private lateinit var webViewLaunch: WebViewLaunch

    private var userLogin: String = ""
    private var userPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPermissions()
        checkConnectivity()
    }

    private fun initPermissions() {
        if (!isLocationPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                ),
                0
            )
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }


    private fun checkConnectivity() {
        connectivity.observe(this) { isConnected ->
            if (!isConnected) {
                with(binding) {
                    webViewScreen.isGone = false
                    badNetworkConnectionPage.isVisible = true
                }
            } else {
                with(binding) {
                    webViewScreen.isVisible = true
                    badNetworkConnectionPage.isGone = true
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        initObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObservers() {
        mainViewModel.isUserRemembered.observe(this) { isUserRemembered ->
            if (isUserRemembered != null) {
                if (isUserRemembered) {
                    val savedInfo = mainViewModel.getSavedLoginAndPassword()
                    webViewLaunchScreen(
                        savedInfo[StringConstants.userName],
                        savedInfo[StringConstants.userPassword]
                    )
                } else {
                    webViewLaunchScreen(null, null)
                }
            }
        }
        mainViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(
                    this,
                    StringConstants.errorMessageStart + errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        mainViewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn != null) {
                if (isLoggedIn) {
                    Intent(applicationContext, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        startService(this)
                    }
                }
            }
        }
    }

    /// запуск webView
    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewLaunchScreen(login: String?, password: String?) {
        webViewLaunch = WebViewLaunch(login, password, this)
        webViewLaunch.webViewSetup(binding.webViewScreen)
    }

    /// переопределение кнопки Back
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val webView = binding.webViewScreen
        if (webView.canGoBack()) webView.goBack()
    }

    override fun sendUserData(login: String, password: String) {
        userLogin = login
        userPassword = password
        mainViewModel.doLogin(userLogin, userPassword)
    }

    override fun isLoggedIn(state: Boolean) {
        if (state) {
            mainViewModel.removeLoginAndPassword()
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
    }
}