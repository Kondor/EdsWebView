package com.example.appwebgps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.coroutines.CoroutineContext

class WebDeviceLocation(context: Context, deviceLocationListener: DeviceLocationListener) : LocationListener, CoroutineScope {
    companion object {
        // минимальная дистанция для обновления в метрах
        private const val UPDATE_FREQUENCY_DISTANCE: Long = 5 // 5 метров
        // минимальное время между обновлениями в милисекундах
        private const val UPDATE_FREQUENCY_TIME: Long = 1000 // 1 секунда
        private val TAG = WebDeviceLocation::class.java.simpleName
    }

    private var deviceLocation: Location? = null
    private val context: WeakReference<Context>
    private var locationManager: LocationManager? = null
    private var deviceLocationListener: DeviceLocationListener
    private val job = Job()
    private val requestCode = 101

    @SuppressLint("HardwareIds")
    private val uId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    /// задаем для класса контекст CoroutineContext по умолчанию
    override val coroutineContext: CoroutineContext
        @SuppressLint("LongLogTag")
        get() = job + Dispatchers.Default + CoroutineName("Activity Scope")

    init {
        this.context = WeakReference(context)
        this.deviceLocationListener = deviceLocationListener

        initializeLocationProviders()
    }

    @SuppressLint("MissingPermission")
     private fun initializeLocationProviders() {

        // инициализация locationManager если не была инициализирована ранее
        if (locationManager == null) {
            locationManager = context.get()
                ?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        locationManager?.apply {
            // проверяем доступ к Gps
            val hasGPS = isProviderEnabled(LocationManager.GPS_PROVIDER)
            // проверяем доступ к сети
            val hasNetwork = isProviderEnabled(LocationManager.PASSIVE_PROVIDER) // PASSIVE-PROVIDER - дублирует данные

            // запрос на разрешение, если разрешения нет
            if (!isLocationPermissionGranted()) {
                requestPermissions()
            }

            // если разрешение предоставлено
            if (isLocationPermissionGranted()) {
                // GPS
                if (hasGPS) {
                    requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        UPDATE_FREQUENCY_TIME,
                        UPDATE_FREQUENCY_DISTANCE.toFloat(), this@WebDeviceLocation)
                    deviceLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                } else {
                    // оповещание открытия GPS
                    context.get()?.apply {
                        AlertDialog.Builder(this)
                            .setTitle(getString(R.string.title_enable_gps))
                            .setMessage(getString(R.string.desc_enable_gps))
                            .setPositiveButton(getString(R.string.btn_settings)
                            ) { dialog, which ->
                                val intent = Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivity(intent)
                            }.setNegativeButton(getString(R.string.btn_cancel))
                            { dialog, which -> dialog.cancel() }.show()
                    }
                }

                // Network
                if(deviceLocation == null && hasNetwork) {
                    requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        0, 0f,
                        this@WebDeviceLocation)

                    deviceLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            }
        }
    }

    /// проверяем не предоставлено ли разрешение через ActivityCompat
    private fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(context.get()!!, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context.get()!!, ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    /// запрашиваем разрешение в реальном времени с помощью requestPermissions
    /// принимает три аргумента — Context, Permission , RequestCode
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            context.get()!! as Activity,
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            ),
            requestCode
        )
    }

    /// прекращение получения обновления местоположения в диспетчере местоположений
    fun stopUpdateGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@WebDeviceLocation)
        }
    }

    /// обновление данных местоположения
    override fun onLocationChanged(newDeviceLocation: Location) {
        deviceLocation = newDeviceLocation

        launch(Dispatchers.Default) {
            withContext(Dispatchers.IO) {
                var addressList: List<Address?>? = null

                try {
                    addressList = Geocoder(
                        context.get()!!,
                        Locale.ENGLISH).getFromLocation(
                        deviceLocation!!.latitude,
                        deviceLocation!!.longitude,
                        1)

                    deviceLocationListener.onDeviceLocationChanged(addressList)
                    Log.i(TAG, "Fetch address list $addressList")
                } catch (e: IOException) {
                    Log.e(TAG, "Failed fetched Address List")
                }
            }
        }
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}