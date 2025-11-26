package me.sofiiak.sharedplay.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sofiiak.sharedplay.data.CommentSectionRepository
import me.sofiiak.sharedplay.data.CommentSectionRepositoryImpl
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.PlaylistsRepositoryImpl
import me.sofiiak.sharedplay.data.SongsRepository
import me.sofiiak.sharedplay.data.SongsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun providePlaylistsRepository(
        impl: PlaylistsRepositoryImpl,
    ): PlaylistsRepository

    @Binds
    abstract fun provideSongsRepository(
        impl: SongsRepositoryImpl,
    ): SongsRepository

    @Binds
    abstract fun provideCommentSectionRepository(
        impl: CommentSectionRepositoryImpl,
    ): CommentSectionRepository
}