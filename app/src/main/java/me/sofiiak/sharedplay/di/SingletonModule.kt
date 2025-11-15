package me.sofiiak.sharedplay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sofiiak.sharedplay.data.ApiClient
import me.sofiiak.sharedplay.data.Service
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun provideService(): Service =
        ApiClient.service

}