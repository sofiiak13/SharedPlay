package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.CommentResponse
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import me.sofiiak.sharedplay.data.dto.PlaylistUpdate
import me.sofiiak.sharedplay.data.dto.ReactionResponse
import me.sofiiak.sharedplay.data.dto.SongResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
    @GET("playlist/{user_id}/playlists")
    suspend fun getUserPlaylists(
        @Path("user_id") userId: String
    ): List<PlaylistResponse>

    @GET("playlist/{playlist_id}")
    suspend fun getPlaylistDetails(
        @Path("playlist_id") playlistId: String
    ): PlaylistResponse

    @POST("playlist")
    suspend fun createPlaylist(
        @Query("owner") owner: String,
        @Body playlist: PlaylistUpdate
    ): PlaylistResponse

    @PATCH("playlist/{playlist_id}")
    suspend fun editPlaylist(
        @Path("playlist_id") playlistId: String,
        @Body playlist: PlaylistUpdate
    ): PlaylistResponse

    @DELETE("playlist/{playlist_id}")
    suspend fun deletePlaylist(
        @Path("playlist_id") playlistId: String
    ): String


    @GET("song/{song_id}")
    suspend fun getSong(
        @Path("song_id") songId: String
    ): SongResponse

    @GET("song/{playlist_id}/songs")
    suspend fun getSongsFrom(
        @Path("playlist_id") playlistId: String
    ): List<SongResponse>


    @POST("song")
    suspend fun createSong(
        @Query("playlist_id") playlistId: String,
        @Query("user_id") userId: String,
        @Query("url") url: String,
    ): SongResponse

    // might delete for now. only can be used for moving a song to a different playlist
    @PATCH("song/{song_id}")
    suspend fun editSong(
        @Path("song_id") songId: String,
        @Body playlist: SongResponse
    ): SongResponse

    @DELETE("song/{song_id}")
    suspend fun deleteSong(
        @Path("song_id") songId: String
    ) : String


    @POST("/comment/")
    suspend fun createComment(
        @Query("content") content: String,
        @Query("parentId") parentId: String,
        @Query("userId") userId: String
    ): CommentResponse

    @GET("/comment/{comment_id}")
    suspend fun getComment(
        @Path("comment_id") commentId: String
    ): CommentResponse

    @PATCH("/comment/{comment_id}")
    suspend fun updateComment(
        @Path("comment_id") commentId: String,
        @Query("content") content: String
    ): CommentResponse

    @DELETE("/comment/{comment_id}")
    suspend fun deleteComment(
        @Path("comment_id") commentId: String
    ): String


    @POST("/reaction/")
    suspend fun createReaction(
        @Query("commentId") commentId: String,
        @Query("userId") userId: String,
        @Query("reactionType") reactionType: String
    ): ReactionResponse

    @GET("/reaction/{reaction_id}")
    suspend fun getReaction(
        @Path("reaction_id") reactionId: String
    ): ReactionResponse

    @PATCH("/reaction/{reaction_id}")
    suspend fun updateReaction(
        @Path("reaction_id") reactionId: String,
        @Query("reactionType") reactionType: String
    ): ReactionResponse

    @DELETE("/reaction/{reaction_id}")
    suspend fun deleteReaction(
        @Path("reaction_id") reactionId: String
    ): String

}