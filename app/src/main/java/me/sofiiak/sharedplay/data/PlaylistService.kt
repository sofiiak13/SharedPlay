package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.datasource.dto.PlaylistResponse
import retrofit2.http.*

interface PlaylistService {
    @GET("user/{user_id}/playlists")
    suspend fun getUserPlaylists(
        @Path("user_id") userId: String
    ): List<PlaylistResponse>

    @POST("playlist/")
    suspend fun createPlaylist(
        @Query("owner") owner: String,
        @Body playlist: PlaylistResponse
    ): PlaylistResponse
}