package me.sofiiak.sharedplay.data

import android.util.Log
import jakarta.inject.Inject
import me.sofiiak.sharedplay.data.dto.SongResponse

// TODO: wrap return into kotlin.Result and replace throw e with logging
private const val TAG = "SongsRepositoryImpl"
class SongsRepositoryImpl @Inject constructor(
    private val service: SongService
) : SongsRepository {

    override suspend fun getSongsFrom(playlistId: String): List<SongResponse> {
        try {
            val songs = service.getSongsFrom(playlistId)
            Log.i(TAG, "getSongsFrom(${playlistId}) : $songs")
            return songs
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSong(songId: String): SongResponse {
        try {
            return service.getSong(songId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addSong(
        url: String,
        playlistId: String,
        userId: String,
    ): SongResponse {
        try {
            return service.createSong(
                url = url,
                playlistId = playlistId,
                userId = userId
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteSong(songId: String): String {
        try {
            return service.deleteSong(songId)
        } catch (e: Exception) {
            throw e
        }
    }
}