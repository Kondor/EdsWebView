package com.example.appwebgps.di

import android.content.Context
import com.example.appwebgps.security.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePreferenceHelper(@ApplicationContext context: Context) = PreferenceHelper(context)
}