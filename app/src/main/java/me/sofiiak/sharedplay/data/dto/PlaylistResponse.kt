package me.sofiiak.sharedplay.data.dto

import java.time.LocalDateTime

data class PlaylistResponse(
    val id: String,
    val name: String,
    val date_created: LocalDateTime,
    val last_updated: LocalDateTime,
    val owner: String,
    val editors: List<String>,
)
