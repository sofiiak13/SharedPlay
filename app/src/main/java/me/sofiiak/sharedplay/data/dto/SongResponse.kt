package me.sofiiak.sharedplay.data.dto

data class SongResponse(
    val id: String ,
    val title: String ,
    val artist: String ,
    val added_by: String,
    val link: String ,
    val playlist_id: String,
)
