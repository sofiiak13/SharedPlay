package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.SongResponse

interface SongsRepository {
    suspend fun getSongsFrom(playlistId: String) : Result<List<SongResponse>>

    suspend fun getSong(songId: String) : Result<SongResponse>

    suspend fun addSong(
        url: String,
        playlistId: String,
        userId: String,
    ) : Result<SongResponse>

    suspend fun deleteSong(songId: String) : Result<String>
}