package me.sofiiak.sharedplay.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.PlaylistsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun providePlaylistsRepository(
        impl: PlaylistsRepositoryImpl,
    ): PlaylistsRepository
}