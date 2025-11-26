package me.sofiiak.sharedplay.data

import android.util.Log
import jakarta.inject.Inject
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import me.sofiiak.sharedplay.data.dto.PlaylistUpdate

private const val TAG = "PlaylistsRepositoryImpl"

class PlaylistsRepositoryImpl @Inject constructor(
    private val service: Service,
) : PlaylistsRepository {

    override suspend fun getPlaylistsForUser(userId: String): Result<List<PlaylistResponse>> =
        runCatching {
            service.getUserPlaylists(userId)
        }.onFailure { error ->
            Log.e(TAG, "getPlaylistsForUser: ", error)
        }

    override suspend fun getPlaylistDetails(playlistId: String): Result<PlaylistResponse> =
        runCatching {
            service.getPlaylistDetails(playlistId)
        }.onFailure { error ->
            Log.e(TAG, "getPlaylistDetails: ", error)
        }


    override suspend fun createPlaylist(playlist: PlaylistResponse): Result<PlaylistResponse> =
        runCatching {
            service.createPlaylist(playlist)
        }.onFailure { error ->
            Log.e(TAG, "createPlaylist: ", error)
        }


    override suspend fun editPlaylist(
        playlistId: String, playlist: PlaylistUpdate): Result<PlaylistResponse> =
        runCatching {
            service.editPlaylist(playlistId, playlist)
        }.onFailure { error ->
            Log.e(TAG, "editPlaylist: ", error)
        }

    override suspend fun deletePlaylist(playlistId: String): Result<String> =
        runCatching {
            service.deletePlaylist(playlistId)
        }.onFailure { error ->
            Log.e(TAG, "deletePlaylist: ", error)
        }
}

