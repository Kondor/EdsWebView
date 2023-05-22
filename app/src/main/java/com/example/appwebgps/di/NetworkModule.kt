package com.example.appwebgps.di

import android.content.Context
import com.example.appwebgps.BuildConfig
import com.example.appwebgps.network.ApiService
import com.example.appwebgps.network.utils.interceptors.*
import com.example.appwebgps.network.utils.retrofit.ResultAdapterFactory
import com.example.appwebgps.security.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val baseUrl = BuildConfig.base_url

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        prefHelper: PreferenceHelper
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(createAuthorizationInterceptor(prefHelper))
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(mOkHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(mOkHttpClient)
            .addCallAdapterFactory(ResultAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthService(client: Retrofit): ApiService = client.create(ApiService::class.java)
}