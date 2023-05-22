package com.example.appwebgps.login.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("validTo")
    val validTo: String,

    @SerializedName("additionalProp1")
    val additionalProp1: String,
    @SerializedName("additionalProp2")
    val additionalProp2: String,
    @SerializedName("additionalProp3")
    val additionalProp3: String,
    @SerializedName("detail")
    val detail: String,
    @SerializedName("errors")
    val errors: Errors,
    @SerializedName("instance")
    val instance: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String
)