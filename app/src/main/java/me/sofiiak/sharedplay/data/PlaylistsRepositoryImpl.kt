package me.sofiiak.sharedplay.data

import jakarta.inject.Inject
import me.sofiiak.sharedplay.data.datasource.dto.PlaylistResponse

class PlaylistsRepositoryImpl @Inject constructor(
    private val service: PlaylistService,
) : PlaylistsRepository {

    override suspend fun getPlaylistsForUser(userId: String): List<PlaylistResponse> {
        try {
            return service.getUserPlaylists(userId)
        } catch (e: Exception) {
            // Handle network error, log, retry, or return cached data
            throw e
        }
    }

    override suspend fun createPlaylist(owner: String, playlist: PlaylistResponse): PlaylistResponse {
        return service.createPlaylist(owner, playlist)
    }
}

