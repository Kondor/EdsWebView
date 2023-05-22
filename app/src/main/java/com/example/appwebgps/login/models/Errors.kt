package com.example.appwebgps.login.models

import com.google.gson.annotations.SerializedName

data class Errors(
    @SerializedName("additionalProp1")
    val additionalProp1: List<String>,
    @SerializedName("additionalProp2")
    val additionalProp2: List<String>,
    @SerializedName("additionalProp3")
    val additionalProp3: List<String>
)