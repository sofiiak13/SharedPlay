package me.sofiiak.sharedplay.data

import android.util.Log
import jakarta.inject.Inject
import me.sofiiak.sharedplay.data.dto.SongResponse

private const val TAG = "SongsRepositoryImpl"
class SongsRepositoryImpl @Inject constructor(
    private val service: Service
) : SongsRepository {

    override suspend fun getSongsFrom(playlistId: String): Result<List<SongResponse>> =
        runCatching {
            val songs = service.getSongsFrom(playlistId)
            Log.i(TAG, "getSongsFrom($playlistId): $songs")
            songs
        }.onFailure { error ->
            Log.e(TAG, "getSongsFrom($playlistId) failed", error)
        }

    override suspend fun getSong(songId: String): Result<SongResponse> =
        runCatching {
            service.getSong(songId)
        }.onFailure { error ->
            Log.e(TAG, "getSong($songId) failed", error)
        }

    override suspend fun addSong(
        url: String,
        playlistId: String,
        userId: String,
    ): Result<SongResponse> =
        runCatching {
            service.createSong(
                url = url,
                playlistId = playlistId,
                userId = userId
            )
        }.onFailure { error ->
            Log.e(TAG, "addSong(url=$url, playlistId=$playlistId, userId=$userId) failed", error)
        }

    override suspend fun deleteSong(songId: String): Result<String> =
        runCatching {
            service.deleteSong(songId)
        }.onFailure { error ->
            Log.e(TAG, "deleteSong($songId) failed", error)
        }
}