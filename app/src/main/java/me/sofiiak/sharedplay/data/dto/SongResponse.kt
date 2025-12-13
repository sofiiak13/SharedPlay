package me.sofiiak.sharedplay.data.dto

import java.time.LocalDateTime

data class SongResponse(
    val id: String ,
    val yt_id: String ,
    val title: String ,
    val artist: String ,
    val added_by: String,
    val link: String ,
    val playlist_id: String,
    val date_added: LocalDateTime? = null,
    val date_released: LocalDateTime? = null,
)
