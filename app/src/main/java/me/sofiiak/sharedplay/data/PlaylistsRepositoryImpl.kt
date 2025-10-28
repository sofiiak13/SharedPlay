package me.sofiiak.sharedplay.data

import android.util.Log
import jakarta.inject.Inject
import me.sofiiak.sharedplay.data.dto.PlaylistResponse

private const val TAG = "PlaylistsRepositoryImpl"

// TODO: wrap all functions into kotlin.Result
class PlaylistsRepositoryImpl @Inject constructor(
    private val service: PlaylistService,
) : PlaylistsRepository {

    override suspend fun getPlaylistsForUser(userId: String): Result<List<PlaylistResponse>> =
        runCatching {
            service.getUserPlaylists(userId)
        }.onFailure { error ->
            Log.e(TAG, "getPlaylistsForUser: ", error)
        }

    override suspend fun getPlaylistDetails(playlistId: String): PlaylistResponse {
        try {
            val playlist = service.getPlaylistDetails(playlistId)
            return playlist
        } catch (e: Exception) {
            // Handle network error, log, retry, or return cached data
            throw e
        }
    }

    override suspend fun createPlaylist(owner: String, playlist: PlaylistResponse): PlaylistResponse {
        return service.createPlaylist(owner, playlist)
    }

    override suspend fun editPlaylist(
        playlistId: String, playlist: PlaylistResponse): PlaylistResponse {
        try {
            return service.editPlaylist(playlistId, playlist)
        } catch (e: Exception) {
            // Handle network error, log, retry, or return cached data
            throw e
        }
    }

    override suspend fun deletePlaylist(playlistId: String): String {
        try {
            return service.deletePlaylist(playlistId)
        } catch (e: Exception) {
            // Handle network error, log, retry, or return cached data
            throw e
        }
    }
}

