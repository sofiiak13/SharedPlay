package me.sofiiak.sharedplay.data.datasource

import me.sofiiak.sharedplay.data.datasource.dto.PlaylistResponse
import javax.inject.Inject

class PlaylistsDataSource @Inject constructor() {
    suspend fun getPlaylists(): List<PlaylistResponse> = listOf(
        PlaylistResponse(
            id = "1",
            name = "Name1",
            owner = "Sonya",
            editors = listOf("Sonya", "Bob"),
            date_created = "12.02.2025",
            last_updated = "12.02.2025",
        ),
        PlaylistResponse(
            id = "2",
            name = "Name2",
            owner = "Alex",
            editors = listOf("Sonya", "Alex"),
            date_created = "12.02.2024",
            last_updated = "12.02.2024",
        )
    )
}