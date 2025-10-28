package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.SongResponse

interface SongsRepository {
    suspend fun getSongsFrom(playlistId: String) : List<SongResponse>

    suspend fun getSong(songId: String) : SongResponse

    suspend fun addSong(
        url: String,
        playlistId: String,
        userId: String,
    ) : SongResponse

    suspend fun deleteSong(songId: String) : String
}