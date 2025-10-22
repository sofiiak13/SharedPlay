package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.datasource.dto.PlaylistResponse
import retrofit2.http.*

interface PlaylistsRepository {
    suspend fun getPlaylistsForUser(userId: String): List<PlaylistResponse>
    suspend fun createPlaylist(owner: String, playlist: PlaylistResponse): PlaylistResponse
}


