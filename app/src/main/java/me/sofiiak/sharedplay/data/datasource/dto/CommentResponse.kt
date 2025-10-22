package me.sofiiak.sharedplay.data.datasource.dto

data class CommentResponse(
    val id: String ,
    val text: String ,
    val author: String ,
    val date: String,
    val prev: String ,
    val song_id: String,
)
