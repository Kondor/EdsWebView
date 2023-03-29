package com.example.appwebgps

import android.location.Address

interface DeviceLocationListener {
    // функция callback onDeviceLocationChanged
    // получение текущего местоположения при измнении местоположения устройства
    fun onDeviceLocationChanged(results: List<Address>?)
}