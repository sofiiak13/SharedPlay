package me.sofiiak.sharedplay.data.dto

import java.time.LocalDateTime

data class PlaylistResponse(
    val id: String,
    val name: String,
    val date_created: LocalDateTime? = null,
    val last_updated: LocalDateTime? = null,
    val owner: String,
    val editors: List<String>? = null,
)
