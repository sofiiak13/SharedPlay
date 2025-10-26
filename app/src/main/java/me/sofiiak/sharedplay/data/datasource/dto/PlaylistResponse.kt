package me.sofiiak.sharedplay.data.datasource.dto

import java.time.LocalDateTime

data class PlaylistResponse(
    val id: String,
    val name: String,
    val date_created: LocalDateTime,
    val last_updated: LocalDateTime,
//    val date_created: OffsetDateTime,
//    val last_updated: OffsetDateTime,
    val owner: String,
    val editors: List<String>,
)