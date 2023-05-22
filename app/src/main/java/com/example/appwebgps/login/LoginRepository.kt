package com.example.appwebgps.login

import com.example.appwebgps.login.models.LoginResponse
import com.example.appwebgps.login.models.UserIdResponse
import com.example.appwebgps.network.utils.result.Result

interface LoginRepository {

    suspend fun getToken(
        login: String,
        password: String
    ): Result<LoginResponse?>

    suspend fun getUserId(userName: String): Result<UserIdResponse?>

    suspend fun saveToken(token: String?)

    suspend fun saveUserId(userId: String?)

    suspend fun deleteUserInfo()
}