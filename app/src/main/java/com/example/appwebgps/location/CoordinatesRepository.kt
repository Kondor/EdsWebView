package com.example.appwebgps.location

interface CoordinatesRepository {

    suspend fun sendCoordinates(latitude: Double, longitude: Double)
}