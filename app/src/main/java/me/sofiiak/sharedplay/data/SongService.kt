package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.SongResponse
import retrofit2.http.*

interface SongService {

    @GET("song/{song_id}")
    suspend fun getSong(
        @Path("song_id") songId: String
    ): SongResponse

    @GET("song/{playlist_id}/songs")
    suspend fun getSongsFrom(
        @Path("playlist_id") playlistId: String
    ): List<SongResponse>

    // addSong? addSongToPlaylist?
    @POST("song")
    suspend fun createSong(
        @Query("playlist_id") playlistId: String,
        @Query("user_id") userId: String,
        @Query("url") url: String,
    ): SongResponse

    // need it for moving to a different playlist
    @PATCH("song/{song_id}")
    suspend fun editSong(
        @Path("song_id") songId: String,
        @Body playlist: SongResponse
    ): SongResponse

    @DELETE("song/{song_id}")
    suspend fun deleteSong(
        @Path("song_id") songId: String
    ) : String
}