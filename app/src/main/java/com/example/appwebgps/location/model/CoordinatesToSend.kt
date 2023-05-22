package com.example.appwebgps.location.model

import com.google.gson.annotations.SerializedName

data class CoordinatesToSend(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
