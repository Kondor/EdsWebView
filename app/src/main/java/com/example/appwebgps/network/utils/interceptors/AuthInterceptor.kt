package com.example.appwebgps.network.utils.interceptors

import com.example.appwebgps.utils.StringConstants
import com.example.appwebgps.security.PreferenceHelper
import okhttp3.Interceptor


fun createAuthorizationInterceptor(prefHelper: PreferenceHelper): Interceptor {
    return Interceptor { chain ->
        val newBuilder = chain.request().newBuilder()
        val accessToken = prefHelper.getString(StringConstants.accessTokenTitle)
        if (accessToken != null) {
            newBuilder.addHeader("Authorization", StringConstants.bearerHeader + accessToken)
        }
        return@Interceptor chain.proceed(newBuilder.build())
    }
}
