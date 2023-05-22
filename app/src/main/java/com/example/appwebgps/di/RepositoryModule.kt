package com.example.appwebgps.di

import com.example.appwebgps.location.CoordinatesRepository
import com.example.appwebgps.location.CoordinatesRepositoryImpl
import com.example.appwebgps.login.LoginRepository
import com.example.appwebgps.login.LoginRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindLoginRepository(loginRepository: LoginRepositoryImpl): LoginRepository

    @Binds
    abstract fun bindCoordinatesRepository(
        coordinatesRepository: CoordinatesRepositoryImpl
    ): CoordinatesRepository
}