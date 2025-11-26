package me.sofiiak.sharedplay.data.dto

import java.time.LocalDateTime

data class CommentResponse(
    val id: String,
    val text: String,
    val author_id: String,
    val author: String,
    val date: LocalDateTime? = null,
    val prev: String? = null,
    val song_id: String,
    val edited: Boolean = false,
    val depth: Int = 0
)
