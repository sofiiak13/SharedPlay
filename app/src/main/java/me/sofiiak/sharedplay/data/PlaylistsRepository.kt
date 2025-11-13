package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.PlaylistResponse

interface PlaylistsRepository {
    suspend fun getPlaylistsForUser(userId: String): Result<List<PlaylistResponse>>
    suspend fun getPlaylistDetails(playlistId: String): Result<PlaylistResponse>
    suspend fun createPlaylist(owner: String, playlist: PlaylistResponse): Result<PlaylistResponse>
    suspend fun editPlaylist(playlistId: String, playlist: PlaylistResponse): Result<PlaylistResponse>
    suspend fun deletePlaylist(playlistId: String): Result<String>

}


