package com.example.appwebgps.network

import com.example.appwebgps.location.model.CoordinatesToSend
import com.example.appwebgps.network.utils.result.Result
import com.example.appwebgps.login.models.LoginRequest
import com.example.appwebgps.login.models.LoginResponse
import com.example.appwebgps.login.models.UserIdResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/v2.6/token")
    suspend fun getAccessToken(
        @Body loginRequest: LoginRequest
    ): Result<LoginResponse>

    @GET("/api/v2.6/Users")
    suspend fun getUserId(
        @Query("UserName") userName: String
    ): Result<UserIdResponse>

    @POST("/api/v2.6/Users/{id}/Coordinates")
    suspend fun sendCoordinatesInfo(
        @Body coordinatesToSend: CoordinatesToSend,
        @Path("id") userId: String?
    )
}