package me.sofiiak.sharedplay.data.dto

import java.time.LocalDateTime

data class InviteResponse (
    val id: String,
    val playlist_id: String,
    val created_by: String, // user id
    val expires_at: LocalDateTime? = null,
)