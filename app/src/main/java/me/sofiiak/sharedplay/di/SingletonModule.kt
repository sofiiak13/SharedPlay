package me.sofiiak.sharedplay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sofiiak.sharedplay.data.ApiClient
import me.sofiiak.sharedplay.data.CommentService
import me.sofiiak.sharedplay.data.PlaylistService
import me.sofiiak.sharedplay.data.SongService
import javax.inject.Singleton

// TODO: merge all services into single interface
@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun providePlaylistService(): PlaylistService =
        ApiClient.playlistService

    @Provides
    @Singleton
    fun provideSongService(): SongService =
        ApiClient.songService

    @Provides
    @Singleton
    fun provideCommentService(): CommentService =
        ApiClient.commentService
}