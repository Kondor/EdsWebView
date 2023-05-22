package com.example.appwebgps.location

import android.util.Log
import com.example.appwebgps.utils.StringConstants
import com.example.appwebgps.location.model.CoordinatesToSend
import com.example.appwebgps.network.ApiService
import com.example.appwebgps.security.PreferenceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoordinatesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceHelper: PreferenceHelper,
    private val ioDispatcher: CoroutineDispatcher
): CoordinatesRepository {

    override suspend fun sendCoordinates(latitude: Double, longitude: Double) {
        val userId = preferenceHelper.getString(StringConstants.userIdTitle)
        val coordinatesToSend = CoordinatesToSend(
            latitude = latitude,
            longitude = longitude
        )
        Log.e("QWERTY", "$latitude, $longitude")
        withContext(ioDispatcher) {
            try {
                apiService.sendCoordinatesInfo(
                    coordinatesToSend = coordinatesToSend,
                    userId = userId
                )
            } catch(_: Exception) { }
        }
    }
}