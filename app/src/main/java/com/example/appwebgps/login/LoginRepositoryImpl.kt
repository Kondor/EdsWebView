package com.example.appwebgps.login

import com.example.appwebgps.utils.StringConstants
import com.example.appwebgps.login.models.LoginRequest
import com.example.appwebgps.login.models.LoginResponse
import com.example.appwebgps.login.models.UserIdResponse
import com.example.appwebgps.network.utils.result.Result
import com.example.appwebgps.network.ApiService
import com.example.appwebgps.security.PreferenceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceHelper: PreferenceHelper,
    private val ioDispatcher: CoroutineDispatcher
) : LoginRepository {

    override suspend fun getToken(
        login: String,
        password: String
    ): Result<LoginResponse?> =
        withContext(ioDispatcher) {
            val loginRequest = LoginRequest(
                userName = login,
                password = password
            )
            return@withContext apiService.getAccessToken(loginRequest)
        }

    override suspend fun getUserId(userName: String): Result<UserIdResponse?> =
        withContext(ioDispatcher) {
            return@withContext apiService.getUserId(userName)
        }


    override suspend fun saveToken(token: String?) {
        preferenceHelper.saveString(StringConstants.accessTokenTitle, token)
    }

    override suspend fun saveUserId(userId: String?) {
        preferenceHelper.saveString(StringConstants.userIdTitle, userId)
    }

    override suspend fun deleteUserInfo() {
        preferenceHelper.removeString(StringConstants.accessTokenTitle)
        preferenceHelper.removeString(StringConstants.userIdTitle)
    }
}