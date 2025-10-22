package me.sofiiak.sharedplay.data.datasource.dto

data class PlaylistResponse(
    val id: String ,
    val name: String ,
    val date_created: String ,
    val last_updated: String,
    val owner: String ,
    val editors: List<String>,
)