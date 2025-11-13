package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import retrofit2.http.*

interface PlaylistService {
    @GET("playlist/{user_id}/playlists")
    suspend fun getUserPlaylists(
        @Path("user_id") userId: String
    ): List<PlaylistResponse>

    @GET("playlist/{playlist_id}")
    suspend fun getPlaylistDetails(
        @Path("playlist_id") playlistId: String
    ): PlaylistResponse

    @POST("playlist/")
    suspend fun createPlaylist(
        @Query("owner") owner: String,
        @Body playlist: PlaylistResponse
    ): PlaylistResponse

    @PATCH("playlist/{playlist_id}")
    suspend fun editPlaylist(
        @Path("playlist_id") playlistId: String,
        @Body playlist: PlaylistResponse
    ): PlaylistResponse

    @DELETE("playlist/{playlist_id}")
    suspend fun deletePlaylist(
        @Path("playlist_id") playlistId: String
    ): String
}